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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

import com.fetal.R;
import com.fetal.base.ActivityBase;
import com.fetal.bean.MixBean;
import com.fetal.bean.TopicBean;
import com.fetal.util.BeatReplayer;
import com.fetal.util.DatabaseOperator;
import com.fetal.util.WebServer;
import com.fetal.view.HorizontalSeekBar;

public class TmusicActivity extends ActivityBase{
	
	private ImageView play, stop, share;
	private TextView music, calendar, end;
	private Handler handler, clock;
	private MediaPlayer player;
	private boolean playing = false;
	private AudioManager manager;
	private Runnable clockRunnable;
	private HorizontalSeekBar bar;
	private MixBean mixBean;
	private ProgressDialog dialog;
	private ConnectivityManager cm;
	private TopicBean topic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);
		initTopUI();
		initUI();
	}
	
	/**
	 * ≥ı ºªØUI
	 */
	private void initUI() {
		cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		manager.setSpeakerphoneOn(true);
		manager.setMicrophoneMute(false);
		manager.setMicrophoneMute(false);
		manager.setMode(AudioManager.STREAM_MUSIC);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		top_title.setText("–ƒ¿÷∑÷œÌ");
		top_left.setImageResource(R.drawable.btn_back);
		top_left.setVisibility(View.VISIBLE);
		top_left.setOnClickListener(this);
		play = (ImageView) findViewById(R.id.play);
		play.setOnClickListener(this);
		stop = (ImageView) findViewById(R.id.stop);
		stop.setOnClickListener(this);
		share = (ImageView) findViewById(R.id.share);
		share.setVisibility(View.INVISIBLE);
		music = (TextView) findViewById(R.id.music);
		calendar = (TextView) findViewById(R.id.calendar);
		end = (TextView) findViewById(R.id.end);
		bar = (HorizontalSeekBar) findViewById(R.id.bar);
		clock = new Handler();
		clockRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (player instanceof MediaPlayer) {
					bar.setMax(player.getDuration());
					bar.setProgress(player.getCurrentPosition());
					if (playing) {
						clock.postDelayed(clockRunnable, 100);
					}
				} else {
					bar.setProgress(0);
				}
			}
		};
		dialog = new ProgressDialog(this);
		dialog.setMessage("«Î…‘∫Ú...");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				dialog.dismiss();
				topic = (TopicBean) msg.obj;
				music.setText(topic.getTape());
				calendar.setText(topic.getChart());
				int minute = (int) Math.floor(topic.getTime() / 60);
				int second = (int) (topic.getTime() - 60 * minute);
				end.setText(minute + ":" + second);
			}
		};
		if (cm.getActiveNetworkInfo().isAvailable()) {
			dialog.show();
			WebServer server = new WebServer();
			server.tmusic(getIntent().getIntExtra("id", 0), handler);
		} else {
			Toast.makeText(this, "«Î¡¥Ω”Õ¯¬Á", Toast.LENGTH_SHORT).show();
			play.setEnabled(false);
		}
	}
	
	/**
	 * ≤•∑≈
	 * @param media
	 */
	private void playMedia() {
		play.setVisibility(View.GONE);
		stop.setVisibility(View.VISIBLE);
		player = new MediaPlayer();
		player.setVolume(1.0f, 1.0f);
		try {
			player.setDataSource(topic.getSound());
			player.prepare();
			player.start();
			player.setOnCompletionListener(new OnCompletionListener() {
				
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
	 * Õ£÷π
	 * @param media
	 */
	private void stopMedia() {
		player.stop();
		player.release();
		player = null;
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
