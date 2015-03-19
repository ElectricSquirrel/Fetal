package com.fetal.activity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fetal.R;
import com.fetal.base.ActivityBase;
import com.fetal.util.VersionUpdater;

public class SettingActivity extends ActivityBase{
	
	private RelativeLayout member, notice, pcenter, about, weixin, feedback,
							score, help, update;
	private SharedPreferences sp;
	private ImageView thumbnail;
	private TextView nickname, calendar;
	private ProgressDialog dialog;
	private Handler handler;
	private static final int NOTIFICATION = 0x0001;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initTopUI();
		initBottomUI();
		initUI();
	}
	
	/**
	 * 初始化UI
	 */
	private void initUI() {
		top_title.setText("更多");
		member = (RelativeLayout) findViewById(R.id.member);
		member.setOnClickListener(this);
		notice = (RelativeLayout) findViewById(R.id.notice);
		notice.setOnClickListener(this);
		pcenter = (RelativeLayout) findViewById(R.id.pcenter);
		pcenter.setOnClickListener(this);
		about = (RelativeLayout) findViewById(R.id.about);
		about.setOnClickListener(this);
		feedback = (RelativeLayout) findViewById(R.id.feedback);
		feedback.setOnClickListener(this);
		score = (RelativeLayout) findViewById(R.id.score);
		score.setOnClickListener(this);
		help = (RelativeLayout) findViewById(R.id.help);
		help.setOnClickListener(this);
		update = (RelativeLayout) findViewById(R.id.update);
		update.setOnClickListener(this);
		thumbnail = (ImageView) findViewById(R.id.thumbnail);
		nickname = (TextView) findViewById(R.id.nickname);
		calendar = (TextView) findViewById(R.id.calendar);
		sp = getSharedPreferences("user", MODE_PRIVATE);
		if (sp.contains("nickname")) {
			long now = System.currentTimeMillis();
			Date curDate = new Date(now);
			SimpleDateFormat format = new SimpleDateFormat("时间：yyyy/MM/dd");
			int week = Integer.valueOf(
					new BigDecimal(
							(now - ((sp.getLong("birthday", 0) - 25920000000L)))
						).divide(new BigDecimal(604800000), 0, BigDecimal.ROUND_DOWN).toString()
				);
			calendar = (TextView) findViewById(R.id.calendar);
			calendar.setText("孕期：孕 " + week + " 周");
			nickname.setText(sp.getString("nickname", ""));
			thumbnail.setImageBitmap(BitmapFactory.decodeFile("/sdcard/Fetal/" + sp.getInt("id", 0) + ".jpg"));
		}
		dialog = new ProgressDialog(this);
		dialog.setMessage("请稍候...");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				dialog.dismiss();
				switch (msg.what) {
					case 1:
						Toast.makeText(getBaseContext(), "更新完毕", Toast.LENGTH_SHORT).show();
						break;
					case 2:
						Toast.makeText(getBaseContext(), "暂无更新", Toast.LENGTH_SHORT).show();
						break;
					case 0:
						Toast.makeText(getBaseContext(), "更新失败", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.notice:
				ImageView on = (ImageView) findViewById(R.id.notice_on);
				ImageView off = (ImageView) findViewById(R.id.notice_off);
				Notification notification = new Notification(R.drawable.ic_launcher, "定时监护胎心，了解宝宝最新状况喔~", 3 * 24 * 60 * 60 * 1000);
				notification.defaults = Notification.DEFAULT_SOUND;
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				if (on.getVisibility() == View.GONE) {
					on.setVisibility(View.VISIBLE);
					off.setVisibility(View.GONE);
					Intent nIntent = new Intent(this, WelcomeActivity.class);
					nIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, nIntent, 0);
					notification.setLatestEventInfo(this, "胎心监护", "定时监护胎心，了解宝宝最新状况喔~", pendingIntent);
					notificationManager.notify(NOTIFICATION, notification);
					nIntent = null;
					pendingIntent = null;
				} else {
					off.setVisibility(View.VISIBLE);
					on.setVisibility(View.GONE);
					notificationManager.cancel(NOTIFICATION);
				}
				notification = null;
				notificationManager = null;
				break;
			case R.id.help:        
				startActivity(new Intent(this, HelpActivity.class));
				break;
			case R.id.about:
				startActivity(new Intent(this, AboutActivity.class));
				break;
			case R.id.feedback:
				startActivity(new Intent(this, FeedbackActivity.class));
				break;
			case R.id.member:
				startActivity(new Intent(this, MemberActivity.class));
				break;
			case R.id.update:
				dialog.show();
				VersionUpdater updater = new VersionUpdater(this);
				updater.getNewVersion(true, handler);
				break;
			case R.id.pcenter:
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.babyfun.cc/?gallery-101-grid.html")));
				break;
		}
	}
}
