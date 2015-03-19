package com.fetal.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.fetal.R;
import com.fetal.base.ActivityBase;
import com.fetal.bean.TopicBean;
import com.fetal.util.BeatReplayer;
import com.fetal.util.DatabaseOperator;
import com.fetal.util.WebServer;
import com.fetal.view.HorizontalSeekBar;

public class TopicActivity extends ActivityBase implements OnEditorActionListener{
	
	private LinearLayout comments, say;
	private RelativeLayout body;
	private ImageView play, pause, thumbnail, chart, bgsurface;
	private SurfaceView surface;
	private TextView calendar, date, txt;
	private EditText input;
	private InputMethodManager imm;
	private ProgressDialog loading, commenting;
	private TopicBean topic;
	private Handler loadingHandler, commentHandler;
	private String[] point;
	private BeatReplayer replayer;
	private boolean isPlaying = false, isInput = false;
	private Handler waveHandler;
	private Runnable waveRunnable, playRunnable;
	private int index = 0;
	private com.fetal.view.HorizontalSeekBar progress;
	private WebServer server;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_topic);
		initTopUI();
		initUI();
	}
	
	/**
	 * 初始化UI
	 */
	private void initUI() {
		imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		top_title.setText("胎心分享");
		top_left.setImageResource(R.drawable.btn_back);
		top_left.setVisibility(View.VISIBLE);
		top_left.setOnClickListener(this);
		play = (ImageView) findViewById(R.id.play);
		play.setOnClickListener(this);
		pause = (ImageView) findViewById(R.id.pause);
		pause.setOnClickListener(this);
		comments = (LinearLayout) findViewById(R.id.comments);
		calendar = (TextView) findViewById(R.id.calendar);
		date = (TextView) findViewById(R.id.date);
		txt = (TextView) findViewById(R.id.txt);
		thumbnail = (ImageView) findViewById(R.id.thumbnail);
		chart = (ImageView) findViewById(R.id.chart);
		surface = (SurfaceView) findViewById(R.id.surface);
		bgsurface = (ImageView) findViewById(R.id.bgsurface);
		progress = (HorizontalSeekBar) findViewById(R.id.progress);
		loading = new ProgressDialog(this);
		loading.setMessage("正在载入...");
		loading.show();
		loadingHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				loading.dismiss();
				switch (msg.what) {
					case 1:
						topic = (TopicBean) msg.obj;
						calendar.setText("孕" + topic.getPregnancy() + "周 " + topic.getPregnancy() * 7 + "天");
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
						date.setText("分享于" + format.format(new Date(topic.getDate())));
						progress.setMax(topic.getTime());
						switch (topic.getReport()) {
							case 1:
								txt.setText("孕妈，本次监测结果显示  宝宝心率正常  注：请坚持定期监护胎心状况；可发给医生做专业分析；也可分享给您身边的其他孕妈。");
								break;
							case 2:
								txt.setText("孕妈，本次监测结果显示  宝宝心率偏高  注：请坚持定期监护胎心状况；可发给医生做专业分析；也可分享给您身边的其他孕妈。");
								break;
							case 3:
								txt.setText("孕妈，本次监测结果显示  宝宝心率偏低  注：请坚持定期监护胎心状况；可发给医生做专业分析；也可分享给您身边的其他孕妈。");
								break;
						}
						try {
							thumbnail.setImageBitmap(BitmapFactory.decodeStream((InputStream) new URL(topic.getThumbnail()).getContent()));
							chart.setImageBitmap(BitmapFactory.decodeStream((InputStream) new URL(topic.getChart()).getContent()));
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//波形图相关
						ViewTreeObserver vto = thumbnail.getViewTreeObserver();
						vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
							
							@Override
							public void onGlobalLayout() {
								// TODO Auto-generated method stub
								chart.getViewTreeObserver().removeGlobalOnLayoutListener(this);
								//背景图
								bgsurface.setBackgroundResource(R.drawable.img_report_bg);
								RelativeLayout.LayoutParams bparams = new RelativeLayout.LayoutParams(chart.getWidth(), chart.getHeight());
								bgsurface.setLayoutParams(bparams);
								bgsurface.setVisibility(View.GONE);
								//画布
								LayoutParams sparams = (LayoutParams) surface.getLayoutParams();
								sparams.width = chart.getWidth();
								sparams.height = chart.getHeight();
								surface.setLayoutParams(sparams);
								surface.setZOrderOnTop(true);
								surface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
								surface.setVisibility(View.GONE);
								point = topic.getPoint().split(";");
								replayer = new BeatReplayer(getBaseContext(), topic.getSound());
								replayer.setSurface(surface);
							}
						});
						//评论
						JSONArray array = topic.getComment();
						int clength = array.length();
						if (clength > 0) {
							for (int i = 0; i < clength; i++) {
								try {
									JSONObject obj = array.getJSONObject(i);
									TextView comment = new TextView(getBaseContext());
									comment.setText(obj.getString("nickname") + "：" + obj.getString("content"));
									comment.setTextSize(12);
									comment.setTextColor(Color.rgb(151, 151, 151));
									if (i != clength) {
										comment.setPadding(0, 0, 0, 5);
									}
									comments.addView(comment);
									comment = null;
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						} else {
							comments.setVisibility(View.INVISIBLE);
						}
						break;
					case 0:
						Toast.makeText(getBaseContext(), "加载信息失败", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};
		sp = getSharedPreferences("user", MODE_PRIVATE);
		server = new WebServer();
		server.topic(getIntent().getIntExtra("id", 0), loadingHandler);
		commenting = new ProgressDialog(this);
		commenting.setMessage("正在提交...");
		commentHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				commenting.dismiss();
				switch (msg.what) {
				case 1:
					imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
		            input.setVisibility(View.GONE);
					comments.setVisibility(View.VISIBLE);
					TextView comment = new TextView(getBaseContext());
					comment.setText(sp.getString("nickname", "游客") + "：" + input.getText());
					comment.setTextSize(12);
					comment.setTextColor(Color.rgb(151, 151, 151));
					comment.setPadding(0, 0, 0, 5);
					comments.addView(comment);
					comment = null;
					break;
				case 0:
					Toast.makeText(getBaseContext(), "评论失败，请稍后尝试", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		say = (LinearLayout) findViewById(R.id.say);
		say.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				input.setVisibility(View.VISIBLE);
				input.requestFocus();
				imm.showSoftInput(input, InputMethodManager.SHOW_FORCED);
			}
		});
		waveHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if (msg.what == 0) {
					replayer.paintBeat(point[msg.what], true);
				} else {
					replayer.paintBeat(point[msg.what], false);
				}
				if (++msg.what >= point.length) {
					stopPlaying();
				}
			}
		};
		waveRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (isPlaying) {
					Message msg = Message.obtain();
					msg.what = index;
					waveHandler.sendMessage(msg);
					if (++index < point.length) {
						progress.incrementProgressBy(1);
						waveHandler.postDelayed(waveRunnable, 1000);
					} else {
						index = 0;
					}
				} else {
					index = 0;
				}
			}
		};
		playRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				replayer.playBeat();
			}
		};
		body = (RelativeLayout) findViewById(R.id.body);
		body.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				int height = body.getRootView().getHeight() - body.getHeight();
				if (height > 100) {
					input.requestFocus();
					input.setVisibility(View.VISIBLE);
				} else {
					if (isInput) {
						submitComment();
						isInput = false;
					}
					input.clearFocus();
					input.setVisibility(View.GONE);
				}
			}
		});
		input = (EditText) findViewById(R.id.input);
		input.setOnEditorActionListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.top_left:
				finish();
				break;
			case R.id.play:
				play.setVisibility(View.GONE);
				pause.setVisibility(View.VISIBLE);
				isPlaying = true;
				chart.setVisibility(View.GONE);
				surface.setVisibility(View.VISIBLE);
				bgsurface.setVisibility(View.VISIBLE);
				new Thread(waveRunnable).start();
				new Thread(playRunnable).start();
				break;
			case R.id.pause:
				stopPlaying();
				break;
		}
	}
	
	/**
	 * 停止播放
	 */
	private void stopPlaying(){
		isPlaying = false;
		replayer.stopPlay();
		bgsurface.setVisibility(View.GONE);
		surface.setVisibility(View.GONE);
		chart.setVisibility(View.VISIBLE);
		pause.setVisibility(View.GONE);
		play.setVisibility(View.VISIBLE);
		progress.setProgress(0);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
			submitComment();
		} else {
			isInput = true;
		}
		return false;
	}
	
	/**
	 * 提交数据
	 */
	private void submitComment() {
        commenting.show();
        DatabaseOperator db = new DatabaseOperator(this);
		int member = db.getMemberRemoteId(sp.getInt("id", 0));
		db.closeDatabase();
		server.commment(topic.getId(), member, input.getText().toString(), commentHandler);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (isPlaying) {
			stopPlaying();
		}
		replayer.destroyReplayer();
		replayer = null;
	}
	
	@Override
	protected void onDestroy(){
//		ShareSDK.stopSDK(this);
		super.onDestroy();
	}
}
