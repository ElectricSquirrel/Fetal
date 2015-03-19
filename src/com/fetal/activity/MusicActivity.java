package com.fetal.activity;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fetal.R;
import com.fetal.base.ActivityBase;
import com.fetal.bean.MixBean;
import com.fetal.util.DatabaseOperator;
import com.fetal.util.WebServer;
import com.fetal.view.HorizontalSeekBar;

public class MusicActivity extends ActivityBase{
	
	private ImageView play, stop, share;
	private TextView music, calendar, date, end;
	private Handler handler, clock;
	private MediaPlayer rplayer, mplayer;
	private boolean playing = false;
	private AudioManager manager;
	private Runnable clockRunnable;
	private HorizontalSeekBar bar;
	private static final int MUSIC = 1, RECORD = 0;
	private MixBean mixBean;
	private ProgressDialog dialog;
	private ConnectivityManager cm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);
		initTopUI();
		initUI();
	}
	
	/**
	 * 初始化UI
	 */
	private void initUI() {
		cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		manager.setMicrophoneMute(false);
		manager.setSpeakerphoneOn(true);
		manager.setMode(AudioManager.STREAM_MUSIC);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		top_title.setText("我的心乐");
		top_left.setImageResource(R.drawable.btn_back);
		top_left.setVisibility(View.VISIBLE);
		top_left.setOnClickListener(this);
		play = (ImageView) findViewById(R.id.play);
		play.setOnClickListener(this);
		stop = (ImageView) findViewById(R.id.stop);
		stop.setOnClickListener(this);
		share = (ImageView) findViewById(R.id.share);
		share.setOnClickListener(this);
		music = (TextView) findViewById(R.id.music);
		calendar = (TextView) findViewById(R.id.calendar);
		end = (TextView) findViewById(R.id.end);
		bar = (HorizontalSeekBar) findViewById(R.id.bar);
		date = (TextView) findViewById(R.id.date);
		clock = new Handler();
		clockRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mplayer instanceof MediaPlayer) {
					if (rplayer.getDuration() > mplayer.getDuration()) {
						bar.setMax(mplayer.getDuration());
						bar.setProgress(mplayer.getCurrentPosition());
					} else {
						bar.setMax(rplayer.getDuration());
						bar.setProgress(rplayer.getCurrentPosition());
					}
					if (playing) {
						clock.postDelayed(clockRunnable, 100);
					}
				} else {
					bar.setProgress(0);
				}
			}
		};
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				dialog.dismiss();
				switch (msg.what) {
					case 1:
						Toast.makeText(getBaseContext(), "分享成功", Toast.LENGTH_SHORT).show();
						break;
					case -1:
						Toast.makeText(getBaseContext(), "分享失败", Toast.LENGTH_SHORT).show();
						break;
				}
			}
		};
		dialog = new ProgressDialog(this);
		dialog.setMessage("请稍候...");
		dialog.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				DatabaseOperator db = new DatabaseOperator(getBaseContext());
				if (getIntent().hasExtra("id")) {
					mixBean = db.getOneMix(getIntent().getIntExtra("id", 0));
				} else {
					mixBean = db.getLastMix();
				}
				mixBean.setRemoteMid(db.getMemberRemoteId(mixBean.getMid()));
				music.setText(mixBean.getMusic());
				calendar.setText(mixBean.getRecord());
				int minute = (int) Math.floor(mixBean.getTime() / 60);
				int second = (int) (mixBean.getTime() - 60 * minute);
				end.setText(minute + ":" + second);
				db.closeDatabase();
				Message msg = Message.obtain();
				handler.sendMessage(msg);
			}
		}).start();
	}
	
	/**
	 * 播放
	 * @param media
	 */
	private void playMedia() {
		play.setVisibility(View.GONE);
		stop.setVisibility(View.VISIBLE);
		mplayer = new MediaPlayer();
		mplayer.setVolume(0.5f, 0.5f);
		rplayer = new MediaPlayer();
		rplayer.setVolume(0.5f, 0.5f);
		try {
			mplayer.setDataSource(mixBean.getMfile());
			mplayer.prepare();
			mplayer.start();
			mplayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					stopMedia();
				}
			});
			rplayer.setDataSource(mixBean.getRfile());
			rplayer.prepare();
			rplayer.start();
			rplayer.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					// TODO Auto-generated method stub
					stopMedia();
				}
			});
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		clock.post(clockRunnable);
		playing = true;
	}
	
	/**
	 * 停止
	 * @param media
	 */
	private void stopMedia() {
		mplayer.stop();
		mplayer.release();
		mplayer = null;
		rplayer.stop();
		rplayer.release();
		rplayer = null;
		play.setVisibility(View.VISIBLE);
		stop.setVisibility(View.GONE);
		playing = false;
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.top_left:
				finish();
				break;
			case R.id.play:
				playMedia();
				break;
			case R.id.stop:
				stopMedia();
				break;
			case R.id.share:
				if (cm.getActiveNetworkInfo().isAvailable()) {
					try {
						dialog.show();
						WebServer webServer = new WebServer();
						webServer.shareMix(this, mixBean, handler);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Toast.makeText(this, "无法连接网络", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (playing) {
			stopMedia();
		}
	}
	
	@Override
	protected void onDestroy() {
		manager.setSpeakerphoneOn(false);
		super.onDestroy();
	}
}
