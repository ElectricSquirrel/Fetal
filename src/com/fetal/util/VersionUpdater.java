package com.fetal.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class VersionUpdater {
	
	private Context context;
	private int newCode = 0;
	private String newName, download, path;
	private String url = "http://192.168.0.113/download/test.php";
	private Dialog availableDialog, unavailableDialog;
	private ProgressDialog bar;
	private Handler handler;
	
	public VersionUpdater(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	/**
	 * ��ȡ��ǰ�汾��
	 * @return
	 */
	private int getVersionCode() {
		try {
			return context.getPackageManager().getPackageInfo("com.fetalmonitor", 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * ��ȡ��ǰ�汾����
	 * @return
	 */
	private String getVersionName() {
		try {
			return context.getPackageManager().getPackageInfo("com.fetalmonitor", 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * ��ȡ���°汾��Ϣ
	 * @return
	 */
	public void getNewVersion(final boolean isManual, final Handler handler) {
		this.handler = handler;
		final Message msg = Message.obtain();
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(context, url, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content){
				try {
					JSONObject obj = new JSONObject(content);
					newCode = Integer.parseInt(obj.getString("code"));
					newName = obj.getString("name");
					path = Environment.getExternalStorageDirectory() + "/FetalMonitor/" + obj.getString("apk");
					download = obj.getString("download");
					//���²���
					if (newCode > getVersionCode()) {
						doNewVersionUpdate();
						msg.what = 1;
					} else if (isManual){
						notNewVersionShow();
						msg.what = 2;
					}
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					msg.what = 0;
				}
			}
			
			@Override
			public void onFailure(Throwable error){
				if (isManual){
					notNewVersionShow();
				}
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
	 * û�и��°汾
	 */
	private void notNewVersionShow() {
		int code = getVersionCode();
		String name = getVersionName();
		StringBuffer buffer = new StringBuffer();
		buffer.append("��ǰ�汾:");
		buffer.append(name);
		buffer.append(" Code:");
		buffer.append(code);
		buffer.append("\n�������°棬�������");
		unavailableDialog = new AlertDialog.Builder(context)
								.setTitle("�������")
								.setMessage(buffer.toString())
								.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										unavailableDialog.dismiss();
									}
								})
								.create();
		unavailableDialog.show();
	}
	
	/**
	 * ���ָ��°汾
	 */
	private void doNewVersionUpdate() {
		int code = getVersionCode();
		String name = getVersionName();
		StringBuffer buffer = new StringBuffer();
		buffer.append("��ǰ�汾:");
		buffer.append(name);
		buffer.append(" Code:");
		buffer.append(code);
		buffer.append("\n�����°汾:");
		buffer.append(newName);
		buffer.append(" Code:");
		buffer.append(newCode);
		buffer.append("\n�Ƿ����");
		availableDialog = new AlertDialog.Builder(context)
								.setTitle("����")
								.setMessage(buffer.toString())
								.setPositiveButton("����", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										bar = new ProgressDialog(context);
										bar.setTitle("��������");
										bar.setMessage("���Ժ�...");
										bar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
										downloadFile(download);
									}
								})
								.setNegativeButton("�ݲ�����", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										availableDialog.dismiss();
									}
								})
								.create();
		availableDialog.show();
	}
	
	/**
	 * �����ļ�
	 * @param url
	 */
	private void downloadFile(final String url) {
		bar.show();
		new Thread(){
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					FileOutputStream fos = null;
					if (is != null) {
						File file = new File(path);
						fos = new FileOutputStream(file);
						byte[] buf = new byte[1024];
						int ch = -1;
						int count = 0;
						while ((ch = is.read(buf)) != -1) {
							fos.write(buf, 0, ch);
							count += ch;
							if (length <= count) {
								break;
							}
						}
					}
					fos.flush();
					if (fos != null) {
						fos.close();
					}
					handler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							bar.cancel();
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(
								Uri.fromFile(new File(path)), 
								"application/vnd.android.package-archive"
							);
							context.startActivity(intent);
						}
					});
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}
}
