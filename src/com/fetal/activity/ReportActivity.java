package com.fetal.activity;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.fetal.R;
import com.fetal.base.ActivityBase;
import com.fetal.bean.RecordBean;
import com.fetal.util.DatabaseOperator;
import com.fetal.util.BeatReplayer;
import com.fetal.util.WebServer;

public class ReportActivity extends ActivityBase implements OnTouchListener{

	private TextView dater, calendar, timer, average, min, max;
	private DatabaseOperator db;
	private RecordBean record;
	private LinearLayout menu;
	private ImageView menu_on, menu_off, report, thumbnail, bgsurface;
	private boolean isPlaying = false;
	private SurfaceView surface;
	private String[] point;
	private BeatReplayer replayer;
	private Handler waveHandler, shareHandler;
	private Runnable waveRunnable, playRunnable;
	private ConnectivityManager cm;
	private int index = 0;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		initTopUI();
		initUI();
	}
	
	/**
	 * 初始化UI
	 */
	private void initUI() {
		cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		top_title.setText("监护结果");
		dater = (TextView) findViewById(R.id.dater);
		calendar = (TextView) findViewById(R.id.calendar);
		timer = (TextView) findViewById(R.id.timer);
		average = (TextView) findViewById(R.id.average);
		min = (TextView) findViewById(R.id.min);
		max = (TextView) findViewById(R.id.max);
		menu = (LinearLayout) findViewById(R.id.menu);
		menu.setOnTouchListener(this);
		menu_on = (ImageView) findViewById(R.id.menu_on);
		menu_off = (ImageView) findViewById(R.id.menu_off);
		db = new DatabaseOperator(this);
		if (getIntent().hasExtra("id")) {
			record = db.getOneRecord(getIntent().getIntExtra("id", 1));
		} else {
			record = db.getLastRecord();
		}
		SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
		record.setRemoteMid(db.getMemberRemoteId(sp.getInt("id", 0)));
		db.closeDatabase();
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm");
		dater.setText(format.format(new Date(record.getDate())));
		calendar.setText("孕" + record.getPregnancy() + "周 " + record.getPregnancy() * 7 + "天");
		Date clock = new Date();
		int minute = (int) Math.floor(record.getTime() / 60);
		int second = (int) (record.getTime() - 60 * minute);
		timer.setText((String.valueOf(minute).length() == 1 ? "0" + minute : minute) + ":" + (String.valueOf(second).length() == 1 ? "0" + second : second));
		average.setText(String.valueOf(record.getAverageBeat()));
		min.setText("最低胎心率：" + record.getMinBeat());
		max.setText("最高胎心率：" + record.getMaxBeat());
		//缩略图
		thumbnail = (ImageView) findViewById(R.id.thumbnail);
		Bitmap bitmap = BitmapFactory.decodeFile(record.getChart());
		Drawable drawable = new BitmapDrawable(bitmap);
		thumbnail.setBackgroundDrawable(drawable);
		int swidth = getWindowManager().getDefaultDisplay().getWidth();
		RelativeLayout.LayoutParams tparams = new RelativeLayout.LayoutParams(swidth, (swidth / 640) * 344);
		thumbnail.setLayoutParams(tparams);
		//监测结论
		report = (ImageView) findViewById(R.id.report);
		if (record.getReport() == 1) {
			report.setImageResource(R.drawable.img_report_good);
		} else {
			report.setImageResource(R.drawable.img_report_bad);
		}
		//波形图相关
		surface = (SurfaceView) findViewById(R.id.surface);
		bgsurface = (ImageView) findViewById(R.id.bgsurface);
		ViewTreeObserver vto = thumbnail.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				thumbnail.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				//背景图
				bgsurface.setBackgroundResource(R.drawable.img_report_bg);
				RelativeLayout.LayoutParams bparams = new RelativeLayout.LayoutParams(thumbnail.getWidth(), thumbnail.getHeight());
				bgsurface.setLayoutParams(bparams);
				bgsurface.setVisibility(View.GONE);
				//画布
				LayoutParams sparams = (LayoutParams) surface.getLayoutParams();
				sparams.width = thumbnail.getWidth();
				sparams.height = thumbnail.getHeight();
				surface.setLayoutParams(sparams);
				surface.setZOrderOnTop(true);
				surface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
				surface.setVisibility(View.GONE);
				point = record.getPoint().split(";");
				replayer = new BeatReplayer(getBaseContext(), record.getSound());
				replayer.setSurface(surface);
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
		dialog = new ProgressDialog(this);
		dialog.setMessage("请稍候...");
		shareHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				dialog.dismiss();
				Toast.makeText(getBaseContext(), "分享成功", Toast.LENGTH_SHORT).show();
			}
		};
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.menu) {
			int width = getWindowManager().getDefaultDisplay().getWidth();
			float x = event.getX();
			if (x < 0.21 * width) {
				if (cm.getActiveNetworkInfo().isAvailable()) {
					try {
						dialog.show();
						WebServer fserver = new WebServer();
						fserver.shareFile(record, shareHandler);
						WebServer server = new WebServer();
						server.share(this, record);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Toast.makeText(this, "无法连接网络", Toast.LENGTH_SHORT).show();
				}
			} else if (x < 0.36 * width) {
				if (isPlaying) {
					stopPlaying();
				} else {
					isPlaying = true;
					thumbnail.setVisibility(View.GONE);
					surface.setVisibility(View.VISIBLE);
					bgsurface.setVisibility(View.VISIBLE);
					menu_on.setVisibility(View.VISIBLE);
					menu_off.setVisibility(View.GONE);
					new Thread(waveRunnable).start();
					new Thread(playRunnable).start();
				}
			} else if (x < 0.59 * width) {
				startActivity(new Intent(this, MonitorActivity.class));
			} else if (x < 0.78 * width) {
				Intent i = new Intent(this, MixActivity.class);
				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
				i.putExtra("title", format.format(new Date(record.getDate())) + " " + calendar.getText());
				i.putExtra("file", record.getSound());
				i.putExtra("length", record.getTime());
				startActivity(i);
			} else {
				startActivity(new Intent(this, RecordsetActivity.class));
			}
		}
		return false;
	}
	
	/**
	 * 停止播放
	 */
	private void stopPlaying(){
		isPlaying = false;
		replayer.stopPlay();
		bgsurface.setVisibility(View.GONE);
		surface.setVisibility(View.GONE);
		thumbnail.setVisibility(View.VISIBLE);
		menu_off.setVisibility(View.VISIBLE);
		menu_on.setVisibility(View.GONE);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (isPlaying) {
			stopPlaying();
		}
	}
	
	@Override
	protected void onDestroy(){
//		ShareSDK.stopSDK(this);
		replayer.destroyReplayer();
		replayer = null;
		super.onDestroy();
	}
}
