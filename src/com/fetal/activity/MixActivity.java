package com.fetal.activity;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import com.fetal.R;
import com.fetal.base.ActivityBase;
import com.fetal.bean.MixBean;
import com.fetal.service.MixService;
import com.fetal.util.DatabaseOperator;
import com.fetal.view.HorizontalSeekBar;
import com.fetal.view.VerticalSeekBar;

public class MixActivity extends ActivityBase implements OnTouchListener{
	
	private ImageView rvol, mvol, rplay, rstop, mplay, mstop, record, music, demo, save;
	private TextView rname, mname, rlength, mlength;
	private VerticalSeekBar rvbar, mvbar;
	private HorizontalSeekBar rbar, mbar;
	private static final int MUSIC = 1, RECORD = 0;
	private Thread mixThread;
	private ProgressDialog dialog;
	private Handler handler, rclock, mclock;
	private Runnable rclockRunnable, mclockRunnable;
	private String rfile, mfile;
	private int time, duration;
	private MediaPlayer rplayer, mplayer;
	private boolean mplaying = false, rplaying = false, getMusic = false;
	private AudioManager manager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mix);
		initTopUI();
		initUI();
	}
	
	/**
	 * ≥ı ºªØUI
	 */
	private void initUI() {
		manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		manager.setMicrophoneMute(false);
		manager.setSpeakerphoneOn(true);
		manager.setMode(AudioManager.STREAM_MUSIC);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		top_title.setText("÷∆◊˜–ƒ¿÷");
		top_left.setImageResource(R.drawable.btn_back);
		top_left.setVisibility(View.VISIBLE);
		top_left.setOnClickListener(this);
		rvol = (ImageView) findViewById(R.id.rvol);
		rvol.setOnClickListener(this);
		mvol = (ImageView) findViewById(R.id.mvol);
		mvol.setOnClickListener(this);
		rvbar = (VerticalSeekBar) findViewById(R.id.rvbar);
		rvbar.setOnTouchListener(this);
		mvbar = (VerticalSeekBar) findViewById(R.id.mvbar);
		mvbar.setOnTouchListener(this);
		rbar = (HorizontalSeekBar) findViewById(R.id.rbar);
		mbar = (HorizontalSeekBar) findViewById(R.id.mbar);
		rplay = (ImageView) findViewById(R.id.rplay);
		rplay.setOnClickListener(this);
		rstop = (ImageView) findViewById(R.id.rstop);
		rstop.setOnClickListener(this);
		mplay = (ImageView) findViewById(R.id.mplay);
		mplay.setOnClickListener(this);
		mstop = (ImageView) findViewById(R.id.mstop);
		mstop.setOnClickListener(this);
		record = (ImageView) findViewById(R.id.record);
		record.setOnClickListener(this);
		music = (ImageView) findViewById(R.id.music);
		music.setOnClickListener(this);
		rname = (TextView) findViewById(R.id.rname);
		rname.setText(getIntent().getStringExtra("title"));
		mname = (TextView) findViewById(R.id.mname);
		rlength = (TextView) findViewById(R.id.rlenght);
		time = getIntent().getIntExtra("length", 0);
		int minute = (int) Math.floor(time / 60);
		int second = (int) (time - 60 * minute);
		rlength.setText(minute + ":" + second);
		mlength = (TextView) findViewById(R.id.mlenght);
		demo = (ImageView) findViewById(R.id.demo);
		demo.setOnClickListener(this);
		save = (ImageView) findViewById(R.id.save);
		save.setOnClickListener(this);
		dialog = new ProgressDialog(this);
		dialog.setMessage("«Î…‘∫Ú...");
		rfile = getIntent().getStringExtra("file");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				dialog.dismiss();
				switch (msg.what) {
					case 1:
						break;
					case 2:
						startActivity(new Intent(getBaseContext(), MusicActivity.class));
						finish();
						break;
				}
			}
		};
		mixThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
				Intent intent = new Intent(getBaseContext(), MixService.class);
				String output = "/sdcard/Fetal/" + sp.getInt("id", 0) + "/mix_" + System.currentTimeMillis() + ".wav";
				intent.putExtra("input1", rfile);
				intent.putExtra("input2", mfile);
				intent.putExtra("output", output);
				startService(intent);
				//ÃÌº”º«¬º
				DatabaseOperator db = new DatabaseOperator(getBaseContext());
				MixBean mix = new MixBean();
				mix.setDate(System.currentTimeMillis());
				mix.setMid(sp.getInt("id", 0));
				mix.setMfile(mfile);
				mix.setRfile(rfile);
				String rtmp[] = rname.getText().toString().split(" ");
				mix.setRecord(rtmp[1] + " " + rtmp[2]);
				mix.setMusic(mname.getText().toString().replace(".mp3", ""));
				if (time > duration) {
					mix.setTime(duration);
				} else {
					mix.setTime(time);
				}
				mix.setSound(output);
				db.addMix(mix);
				db.addRecordset(mix.getMid(), 2, db.getLastMixId());
				db.closeDatabase();
				new Timer(true).schedule(new TimerTask() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Message msg = Message.obtain();
						msg.what = 2;
						handler.sendMessage(msg);
					}
				}, 5000);
			}
		});
		mclock = new Handler();
		rclock = new Handler();
		rclockRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (rplayer instanceof MediaPlayer) {
					rbar.setMax(rplayer.getDuration());
					rbar.setProgress(rplayer.getCurrentPosition());
					if (rplaying) {
						rclock.postDelayed(rclockRunnable, 100);
					}
				} else {
					rbar.setProgress(0);
				}
			}
		};
		mclockRunnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mplayer instanceof MediaPlayer) {
					mbar.setMax(mplayer.getDuration());
					mbar.setProgress(mplayer.getCurrentPosition());
					if (mplaying) {
						mclock.postDelayed(mclockRunnable, 100);
					}
				} else {
					mbar.setProgress(0);
				}
			}
		};
	}
	
	/**
	 * ≤•∑≈
	 * @param media
	 */
	private void playMedia(int media) {
		switch (media) {
		case MUSIC:
			mplay.setVisibility(View.GONE);
			mstop.setVisibility(View.VISIBLE);
			mplayer = new MediaPlayer();
			mplayer.setVolume(0.5f, 0.5f);
			try {
				mplayer.setDataSource(mfile);
				mplayer.prepare();
				mplayer.start();
				mplayer.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						stopMedia(MUSIC);
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
			mclock.post(mclockRunnable);
			mplaying = true;
			break;
		case RECORD:
			rplay.setVisibility(View.GONE);
			rstop.setVisibility(View.VISIBLE);
			rplayer = new MediaPlayer();
			rplayer.setVolume(0.5f, 0.5f);
			try {
				rplayer.setDataSource(rfile);
				rplayer.prepare();
				rplayer.start();
				rplayer.setOnCompletionListener(new OnCompletionListener() {
					
					@Override
					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						stopMedia(RECORD);
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
			rclock.post(rclockRunnable);
			rplaying = true;
			break;
		}
	}
	
	/**
	 * Õ£÷π
	 * @param media
	 */
	private void stopMedia(int media) {
		switch (media) {
		case MUSIC:
			mplay.setVisibility(View.VISIBLE);
			mstop.setVisibility(View.GONE);
			mplayer.stop();
			mplayer.release();
			mplayer = null;
			mplaying = false;
			mvbar.setVisibility(View.INVISIBLE);
			break;
		case RECORD:
			rplay.setVisibility(View.VISIBLE);
			rstop.setVisibility(View.GONE);
			rplayer.stop();
			rplayer.release();
			rplayer = null;
			rplaying = false;
			rvbar.setVisibility(View.INVISIBLE);
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.top_left:
				finish();
				break;
			case R.id.rvol:
				if (rplaying) {
					if (rvbar.getVisibility() == View.VISIBLE) {
						rvbar.setVisibility(View.INVISIBLE);
					} else {
						rvbar.setVisibility(View.VISIBLE);
					}
				}
				break;
			case R.id.mvol:
				if (mplaying) {
					if (mvbar.getVisibility() == View.VISIBLE) {
						mvbar.setVisibility(View.INVISIBLE);
					} else {
						mvbar.setVisibility(View.VISIBLE);
					}
				}
				break;
			case R.id.rplay:
				playMedia(RECORD);
				break;
			case R.id.rstop:
				stopMedia(RECORD);
				break;
			case R.id.mplay:
				if (getMusic) {
					playMedia(MUSIC);
				} else {
					Toast.makeText(this, "«Î—°‘Ò“Ù¿÷", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.mstop:
				stopMedia(MUSIC);
				break;
			case R.id.music:
				Intent i = new Intent(Intent.ACTION_GET_CONTENT);
				i.setType("audio/mp3");
				i.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(i, MUSIC);
				break;
			case R.id.demo:
				if (getMusic) {
					if (rplaying || mplaying) {
						if (rplaying) {
							stopMedia(RECORD);
						}
						if (mplaying) {
							stopMedia(MUSIC);
						}
						break;
					}
					playMedia(MUSIC);
					playMedia(RECORD);
				} else {
					Toast.makeText(this, "«Î—°‘Ò“Ù¿÷", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.save:
				if (getMusic) {
					dialog.show();
					mixThread.start();
				} else {
					Toast.makeText(this, "«Î—°‘Ò“Ù¿÷", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case MUSIC:
				if (resultCode == Activity.RESULT_OK) {
					Uri uri = data.getData();
					Cursor cursor = getContentResolver().query(uri, null, null, null, null);
					cursor.moveToFirst();
					mname.setText(cursor.getString(2));
					duration = Integer.valueOf(cursor.getString(10)) / 1000;
					int minute = (int) Math.floor(duration / 60);
					int second = (int) (duration - 60 * minute);
					mlength.setText(minute + ":" + second);
					mfile = cursor.getString(1);
			        cursor.close();
			        getMusic = true;
				}
				break;
		}
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.rvbar:
				BigDecimal rvol = new BigDecimal(rvbar.getProgress()).divide(new BigDecimal(rvbar.getMax()), 1, BigDecimal.ROUND_DOWN);
				rplayer.setVolume(rvol.floatValue(), rvol.floatValue());
				break;
			case R.id.mvbar:
				BigDecimal mvol = new BigDecimal(mvbar.getProgress()).divide(new BigDecimal(mvbar.getMax()), 1, BigDecimal.ROUND_DOWN);
				mplayer.setVolume(mvol.floatValue(), mvol.floatValue());
				break;
		}
		return false;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (rplayer instanceof MediaPlayer) {
			stopMedia(RECORD);
		}
		if (mplayer instanceof MediaPlayer) {
			stopMedia(MUSIC);
		}
	}
	
	@Override
	protected void onDestroy() {
		manager.setSpeakerphoneOn(false);
		super.onDestroy();
	}
}
