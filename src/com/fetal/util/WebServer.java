package com.fetal.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fetal.bean.MemberBean;
import com.fetal.bean.MixBean;
import com.fetal.bean.OrderBean;
import com.fetal.bean.RecordBean;
import com.fetal.bean.TopicBean;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class WebServer {
	
	private AsyncHttpClient client = new AsyncHttpClient();
	private String url = "http://192.168.0.117/fetal/";
	
	/**
	 * 新用户同步
	 * @param member
	 * @throws FileNotFoundException 
	 */
	public void newMember(final Context context, final MemberBean member) throws FileNotFoundException {
		RequestParams params = new RequestParams();
		params.put("nickname", member.getNickname());
		params.put("mobile", member.getMobile());
		params.put("birthday", String.valueOf(member.getBirthday()));
		params.put("thumbnail", new File(member.getThumbnail()));
		client.post(url + "new-member.php", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				try {
					JSONObject obj = new JSONObject(content);
					switch (obj.getInt("code")) {
						case 0:
							break;
						case 1:
							member.setRemoteId(obj.getInt("id"));
							DatabaseOperator db = new DatabaseOperator(context);
							db.modifyMemberRemoteId(member);
							db.closeDatabase();
							break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable error) {
				super.onFailure(error);
			}
		});
	}
	
	/**
	 * 修改用户信息
	 * @param member
	 * @throws FileNotFoundException
	 */
	public void modifyMember(MemberBean member) throws FileNotFoundException {
		RequestParams params = new RequestParams();
		params.put("id", String.valueOf(member.getRemoteId()));
		params.put("nickname", member.getNickname());
		params.put("mobile", member.getMobile());
		params.put("birthday", String.valueOf(member.getBirthday()));
		params.put("thumbnail", new File(member.getThumbnail()));
		client.post(url + "modify-member.php", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
			}
			
			@Override
			public void onFailure(Throwable error) {
				super.onFailure(error);
			}
		});
	}
	
	/**
	 * 分享
	 * @param id
	 * @throws FileNotFoundException 
	 */
	public void share(final Context context, final RecordBean record) throws FileNotFoundException {
		RequestParams params = new RequestParams();
		params.put("member", String.valueOf(record.getRemoteMid()));
		params.put("pregnancy", String.valueOf(record.getPregnancy()));
		params.put("date", String.valueOf(record.getDate()));
		params.put("time", String.valueOf(record.getTime()));
		params.put("chart", record.getChart());
		params.put("tape", record.getTape());
		params.put("sound", record.getSound());
		params.put("point", record.getPoint());
		params.put("report", String.valueOf(record.getReport()));
		client.post(url + "share.php", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				try {
					JSONObject obj = new JSONObject(content);
					switch (obj.getInt("code")) {
						case 0:
							break;
						case 1:
							DatabaseOperator db = new DatabaseOperator(context);
							db.shareRecord(record.getId());
							db.closeDatabase();
							break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable error) {
				super.onFailure(error);
			}
		});
	}
	
	/**
	 * 分享
	 * @param order
	 * @throws FileNotFoundException 
	 */
	public void shareFile(final RecordBean record, final Handler handler) throws FileNotFoundException {
		final Message msg = Message.obtain();
		RequestParams params = new RequestParams();
		params.put("member", String.valueOf(record.getRemoteMid()));
		params.put("file", new File(record.getChart()));
		client.post(url + "share-file.php", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				RequestParams params = new RequestParams();
				params.put("member", String.valueOf(record.getRemoteMid()));
				try {
					params.put("file", new File(record.getTape()));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				client.post(url + "share-file.php", params, new AsyncHttpResponseHandler(){
					@Override
					public void onSuccess(String content) {
						super.onSuccess(content);
						RequestParams params = new RequestParams();
						params.put("member", String.valueOf(record.getRemoteMid()));
						try {
							params.put("file", new File(record.getSound()));
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						client.post(url + "share-file.php", params, new AsyncHttpResponseHandler(){
							@Override
							public void onSuccess(String content) {
								super.onSuccess(content);
								msg.what = 1;
							}
						});
					}
				});
			}
			
			@Override
			public void onFailure(Throwable error) {
				super.onFailure(error);
				msg.what = 0;
			}
			
			@Override
			public void onFinish() {
				handler.sendMessage(msg);
				super.onFinish();
			}
		});
	}
	
	/**
	 * 下订单
	 * @param order
	 */
	public void order(OrderBean order, final Handler handler) {
		final Message msg = Message.obtain();
		RequestParams params = new RequestParams();
		params.put("member", String.valueOf(order.getMember()));
		params.put("product", String.valueOf(order.getProduct()));
		params.put("name", order.getName());
		params.put("mobile", order.getMobile());
		params.put("addr", order.getAddr());
		client.get(url + "order.php", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				try {
					JSONObject obj = new JSONObject(content);
					switch (obj.getInt("code")) {
						case 1:
							msg.what = 1;
							break;
						case 0:
							msg.what = 0;
							break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable error) {
				super.onFailure(error);
				msg.what = 0;
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				handler.sendMessage(msg);
			}
		});
	}
	
	/**
	 * 获取分享列表
	 * @param page
	 */
	public List<TopicBean> communicate(int page, final Handler handler) {
		final Message msg = Message.obtain();
		RequestParams params = new RequestParams();
		final List<TopicBean> list = new ArrayList<TopicBean>();
		client.get(url + "communicate.php", params, new AsyncHttpResponseHandler(){
			
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				try {
					JSONArray array;
					try {
						array = new JSONArray(new String(content.getBytes("UTF-8")));
						for (int i = 0; i < array.length(); i++) {
							JSONObject obj = array.getJSONObject(i);
							TopicBean topic = new TopicBean();
							String path = url + "files/" + obj.getString("member") + "/record/";
							topic.setId(obj.getInt("id"));
							topic.setNickname(obj.getString("nickname"));
							topic.setThumbnail(url + obj.getString("thumbnail"));
							topic.setPregnancy(obj.getInt("pregnancy"));
							topic.setDate(obj.getLong("date"));
							topic.setChart(path + obj.getString("chart"));
							topic.setReport(obj.getInt("report"));
							list.add(topic);
							topic = null;
							obj = null;
						}
						msg.what = 1;
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						msg.what = 0;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = 0;
				}
			}
			
			@Override
			public void onFailure(Throwable error) {
				super.onFailure(error);
				msg.what = 0;
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				handler.sendMessage(msg);
			}
		});
		return list;
	}
	
	/**
	 * 获取分享信息详细
	 * @param id
	 */
	public void topic(int id, final Handler handler) {
		final Message msg = Message.obtain();
		RequestParams params = new RequestParams();
		params.put("id", String.valueOf(id));
		final TopicBean topic = new TopicBean();
		client.get(url + "topic.php", params, new AsyncHttpResponseHandler(){
			
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				try {
					final JSONObject obj = new JSONObject(content);
					String path = url + "files/" + obj.getInt("member") + "/record/";
					topic.setId(obj.getInt("id"));
					topic.setNickname(obj.getString("nickname"));
					topic.setThumbnail(url + obj.getString("thumbnail"));
					topic.setPregnancy(obj.getInt("pregnancy"));
					topic.setDate(obj.getLong("date"));
					topic.setTime(obj.getInt("time"));
					topic.setChart(path + obj.getString("chart"));
					topic.setPoint(obj.getString("point"));
					topic.setReport(obj.getInt("report"));
					topic.setComment(obj.getJSONArray("comment"));
					final String sound = "/sdcard/Fetal/cache/" + obj.getInt("member") + "_" + obj.getString("sound");
					topic.setSound(sound);
					final File file = new File(sound);
					if (!file.exists()) {
						String[] allowedTypes = new String[]{"audio/x-wav"};
						client.get(path + obj.getString("sound"), new BinaryHttpResponseHandler(allowedTypes){
							@Override
							public void onSuccess(byte[] binaryData) {
								super.onSuccess(binaryData);
								try {
									File dir = new File("/sdcard/Fetal/cache");
									if (!dir.exists()) {
										dir.mkdir();
									}
									FileOutputStream fos =new FileOutputStream(file);
									fos.write(binaryData);
									fos.close();
									fos = null;
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
							@Override
							public void onFailure(Throwable error) {
								super.onFailure(error);
								Log.e("error", error.getMessage());
							}
						});
					}
					msg.what = 1;
					msg.obj = topic;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.what = 0;
				}
			}
			
			@Override
			public void onFailure(Throwable error) {
				super.onFailure(error);
				msg.what = 0;
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				handler.sendMessage(msg);
			}
		});
	}
	
	/**
	 * 评论
	 */
	public void commment(int id, int member, String content, final Handler handler) {
		final Message msg = Message.obtain();
		RequestParams params = new RequestParams();
		params.put("record", String.valueOf(id));
		params.put("member", String.valueOf(member));
		params.put("content", content);
		client.get(url + "comment.php", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				try {
					JSONObject obj = new JSONObject(content);
					switch (obj.getInt("code")) {
						case 1:
							msg.what = 1;
							break;
						case 0:
							msg.what = 0;
							break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable error) {
				super.onFailure(error);
				msg.what = 0;
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				handler.sendMessage(msg);
			}
		});
	}
	
	/**
	 * 分享
	 * @param id
	 * @throws FileNotFoundException 
	 */
	public void shareMix(final Context context, final MixBean mix, final Handler handler) throws FileNotFoundException {
		final Message msg = Message.obtain();
		RequestParams params = new RequestParams();
		params.put("member", String.valueOf(mix.getRemoteMid()));
		params.put("date", String.valueOf(mix.getDate()));
		params.put("time", String.valueOf(mix.getTime()));
		params.put("chart", mix.getRecord());
		params.put("tape", mix.getMusic());
		String file[] = mix.getSound().split("/");
		params.put("sound", file[file.length - 1]);
		client.post(url + "share-mix.php", params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				try {
					JSONObject obj = new JSONObject(content);
					switch (obj.getInt("code")) {
						case 0:
							msg.what = -1;
							break;
						case 1:
							DatabaseOperator db = new DatabaseOperator(context);
							db.shareMix(mix.getId());
							db.closeDatabase();
							RequestParams params = new RequestParams();
							params.put("member", String.valueOf(mix.getRemoteMid()));
							try {
								params.put("file", new File(mix.getSound()));
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							client.post(url + "share-file.php", params, new AsyncHttpResponseHandler(){
								@Override
								public void onSuccess(String content) {
									super.onSuccess(content);
									msg.what = 1;
								}
							});
							break;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable error) {
				super.onFailure(error);
				msg.what = -1;
			}
			
			@Override
			public void onFinish() {
				handler.sendMessage(msg);
				super.onFinish();
			}
		});
	}
	
	/**
	 * 获取分享信息详细
	 * @param id
	 */
	public void tmusic(int id, final Handler handler) {
		RequestParams params = new RequestParams();
		params.put("id", String.valueOf(id));
		final TopicBean topic = new TopicBean();
		client.get(url + "topic.php", params, new AsyncHttpResponseHandler(){
			
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				try {
					JSONObject obj;
					try {
						obj = new JSONObject(new String(content.getBytes("UTF-8")));
						String path = url + "files/" + obj.getInt("member") + "/record/";
						topic.setDate(obj.getLong("date"));
						topic.setTime(obj.getInt("time"));
						topic.setChart(obj.getString("chart"));
						topic.setTape(obj.getString("tape"));
						final String sound = "/sdcard/Fetal/cache/" + obj.getInt("member") + "_" + obj.getString("sound");
						topic.setSound(sound);
						final File file = new File(sound);
						if (!file.exists()) {
							String[] allowedTypes = new String[]{"audio/x-wav"};
							client.get(path + obj.getString("sound"), new BinaryHttpResponseHandler(allowedTypes){
								@Override
								public void onSuccess(byte[] binaryData) {
									super.onSuccess(binaryData);
									try {
										File dir = new File("/sdcard/Fetal/cache");
										if (!dir.exists()) {
											dir.mkdir();
										}
										FileOutputStream fos =new FileOutputStream(file);
										fos.write(binaryData);
										fos.close();
										fos = null;
									} catch (FileNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
								
								@Override
								public void onFailure(Throwable error) {
									super.onFailure(error);
									Log.e("error", error.getMessage());
								}
							});
						}
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable error) {
				super.onFailure(error);
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				Message msg = Message.obtain();
				msg.obj = topic;
				handler.sendMessage(msg);
			}
		});
	}
}
