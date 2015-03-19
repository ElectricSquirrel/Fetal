package com.fetal.activity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.fetal.R;
import com.fetal.base.ActivityBase;
import com.fetal.util.BeatMonitor;
import com.fetal.util.SimulatePainter;
import com.fetal.activity.ReportActivity;
import com.fetal.broadcaster.HeadsetBroadcaster;

public class MonitorActivity extends ActivityBase implements AnimationListener{
	
	private ImageView play, stop, heart;
	private TextView week, day, timer, counter;
	private long time = 0;
	private int vweek, vday, stopwatch = 0, last = 0, cbeat = 0, rate = 0, bigger = 0;
	private SharedPreferences sp;
	private Runnable waveCounter, updateClock;
	private BeatMonitor monitor;
	private Thread waveThread, recordThread, clockThread;
	private Handler waveHandler, saveHandler, clockHandler;
	private ProgressDialog dialog;
	private List<Integer> beating;
	private SimulatePainter spainter;
	private AnimationSet animationSet;
	private HeadsetBroadcaster headsetBroadcaster;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AudioManager audoManager = (AudioManager)getSystemService(AUDIO_SERVICE);
		if (audoManager.isWiredHeadsetOn()) {
			setContentView(R.layout.activity_monitor);
		} else {
			setContentView(R.layout.activity_monitor_off);
		}
		initTopUI();
		initBottomUI();
		initUI();
		if (audoManager.isWiredHeadsetOn()) {
			initOnUI();
			getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, 
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				);
		} else {
			initOffUI();
		}
		registerHeadsetReceiver();
	}
	
	/**
	 * ��̬����豸״̬
	 */
    private void registerHeadsetReceiver(){
    	headsetBroadcaster  = new HeadsetBroadcaster();
        IntentFilter  filter = new IntentFilter();
        filter.addAction("android.intent.action.HEADSET_PLUG");
        registerReceiver(headsetBroadcaster, filter);
    }
	
	/**
	 * ��ʼ��UI
	 */
	private void initUI() {
		top_title.setText("ʵʱ�໤");
		sp = getSharedPreferences("user", MODE_PRIVATE);
		long now = System.currentTimeMillis();
		vweek = Integer.valueOf(
				new BigDecimal(
						(now - ((sp.getLong("birthday", 0) - 25920000000L)))
					).divide(new BigDecimal(604800000), 0, BigDecimal.ROUND_DOWN).toString()
			);
		week = (TextView) findViewById(R.id.week);
		week.setText("��" + vweek + "��");
		vday = Integer.valueOf(
				new BigDecimal(
						(now - ((sp.getLong("birthday", 0) - 25920000000L)))
					).divide(new BigDecimal(86400000), 0, BigDecimal.ROUND_DOWN).toString()
			);
		day = (TextView) findViewById(R.id.day);
		day.setText(vday + "��");
		timer = (TextView) findViewById(R.id.timer);
		counter = (TextView) findViewById(R.id.counter);
	}
	
	/**
	 * ��ʼ���໤UI
	 */
	private void initOnUI() {
		heart = (ImageView) findViewById(R.id.heart);
		dialog = new ProgressDialog(this);
		dialog.setMessage("���ڱ���...");
		play = (ImageView) findViewById(R.id.play);
		play.setOnClickListener(this);
		stop = (ImageView) findViewById(R.id.stop);
		stop.setOnClickListener(this);
		final SurfaceView surface = (SurfaceView) findViewById(R.id.surface);
		final ImageView bg = (ImageView) findViewById(R.id.bg);
		monitor = new BeatMonitor(this);
		beating = new ArrayList<Integer>();
		//����ͼ���
		ViewTreeObserver vto = bg.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				bg.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				LayoutParams params = (LayoutParams) surface.getLayoutParams();
				params.width = bg.getWidth();
				params.height = bg.getHeight();
				surface.setLayoutParams(params);
				surface.setZOrderOnTop(true);                         
				surface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
				bg.setVisibility(View.GONE);
				spainter = new SimulatePainter(getBaseContext(), surface);
				spainter.initCurve();
				recordThread.start();
				waveThread.start();
				clockThread.start();
			}
		});
		animationSet = new AnimationSet(true);
		animationSet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.monitor));
		animationSet.setAnimationListener(this);
//		heart.setAnimation(animationSet);
		//��ʱ����
		recordThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				monitor.readRecording();
			}
		});
		
		//����̥��
		saveHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if (msg.what == 1) {
					dialog.dismiss();
					Intent intent = new Intent(getBaseContext(), ReportActivity.class);
					intent.putExtra("from", 1);
					startActivity(intent);
					finish();
				}
			}
		};
		
		//ʵʱ����
		waveHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if (monitor.isMonitoring()) {
					if (!"00".equals(counter.getText())) {
						spainter.drawCurve();
					}
					//��¼̥����
					Date timer = new Date();
					int tmpB = 0;
					if (msg.what >= last) {//���ݲ��������������̥����
						tmpB = msg.what;
						if (bigger > tmpB * 2) {
							if (cbeat == 0) {//��ʱ������
								time = timer.getTime();
							}
							if (rate == 0) {//�״βɼ�5��������
								if (cbeat >= 5) {
									if (time != 0) {
										rate = (int) (5 * (60 * 1000) / (timer.getTime() - time));
										counter.setText(String.valueOf(rate));
									}
									cbeat = 0;
								} else {
									cbeat++;
								}
							} else {//�����ɼ�10��������
								if (cbeat >= 10) {
									if (time != 0) {
										rate = (int) (10 * (60 * 1000) / (timer.getTime() - time));
										counter.setText(String.valueOf(rate));
									}
									cbeat = 0;
								} else {
									cbeat++;
								}
							}
						}
						bigger = tmpB;
					}
					last = msg.what;
					if (!"00".equals(counter.getText()) && time > 0 && (timer.getTime() - time) > 15000) {//��ʱ������������ʾ����
						counter.setText("00");
						cbeat = 0;
						spainter.clean();
//						heart.clearAnimation();
					}
				}
			}
		};
		
		//����������
		waveCounter = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				int max = monitor.getMaxAmplitude();
				if (max > 0) {
					Message msg = Message.obtain();
					msg.what = max;
					waveHandler.sendMessage(msg);
				}
				waveHandler.postDelayed(waveCounter, 1);
			}
		};
		waveThread = new Thread(waveCounter);
		
		//���¼�ʱ��
		clockHandler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				if (monitor.isWriting()) {
					stopwatch++;
					Date clock = new Date();
					int minute = (int) Math.floor(stopwatch / 60);
					int second = stopwatch - 60 * minute;
					timer.setText((String.valueOf(minute).length() == 1 ? "0" + minute : minute) + ":" + (String.valueOf(second).length() == 1 ? "0" + second : second));
					beating.add(rate);
				}
			}
		};
		updateClock = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = Message.obtain();
				clockHandler.sendMessage(msg);
				clockHandler.postDelayed(updateClock, 1000);
			}
		};
		clockThread = new Thread(updateClock);
	}
	
	/**
	 * ��ʼ�����UI
	 */
	private void initOffUI() {
		
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.play:
				play.setVisibility(View.GONE);
				stop.setVisibility(View.VISIBLE);
				monitor.goWriting(saveHandler);
				break;
			case R.id.stop:
				stop.setVisibility(View.GONE);
				play.setVisibility(View.VISIBLE);
				monitor.stopMonitor(stopwatch, beating, vweek);
				waveThread.interrupt();
				waveThread = null;
				clockThread.interrupt();
				clockThread = null;
				dialog.show();
				break;
		}
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if (monitor instanceof BeatMonitor && monitor.isMonitoring()) {
			monitor.stopMonitor();
			waveThread.interrupt();
			waveThread = null;
		}
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		heart.startAnimation(animation);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		spainter = null;
		unregisterReceiver(headsetBroadcaster);
	}
}
