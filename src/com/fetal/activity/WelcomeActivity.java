package com.fetal.activity;

import com.fetal.R;
import com.fetal.base.ActivityBase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WelcomeActivity extends ActivityBase implements AnimationListener{
	
	private ImageView simulate, monitor, buy, line, arrow;
	private AnimationSet simulateSet, monitorSet, buySet, pointerSet;
	private LinearLayout pointer;
	private SharedPreferences sp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		simulate = (ImageView) findViewById(R.id.simulate);
		monitor = (ImageView) findViewById(R.id.monitor);
		buy = (ImageView) findViewById(R.id.buy);
		pointer = (LinearLayout) findViewById(R.id.pointer);
		line = (ImageView) findViewById(R.id.line);
		arrow = (ImageView) findViewById(R.id.arrow);
		
		simulateSet = new AnimationSet(true);
		simulateSet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_btn_first));
		simulateSet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_btn_second));
		simulateSet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_btn_third));
		simulateSet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_btn_forth));
		simulateSet.setStartOffset(1100);
		simulate.setAnimation(simulateSet);
		simulate.setOnClickListener(this);
		
		monitorSet = new AnimationSet(true);
		monitorSet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_btn_first));
		monitorSet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_btn_second));
		monitorSet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_btn_third));
		monitorSet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_btn_forth));
		monitorSet.setStartOffset(1000);
		monitor.setAnimation(monitorSet);
		monitor.setOnClickListener(this);
		
		buySet = new AnimationSet(true);
		buySet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_btn_first));
		buySet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_btn_second));
		buySet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_btn_third));
		buySet.addAnimation(AnimationUtils.loadAnimation(this, R.anim.welcome_btn_forth));
		buySet.setStartOffset(1200);
		buySet.setAnimationListener(this);
		buy.setAnimation(buySet);
		buy.setOnClickListener(this);
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		pointer.setVisibility(View.VISIBLE);
		pointerSet = new AnimationSet(false);
		pointerSet.addAnimation(new AlphaAnimation(0, 1));
		pointerSet.addAnimation(new TranslateAnimation(0, 0, -100, 0));
		pointerSet.setDuration(500);
		pointerSet.setStartOffset(500);
		line.setAnimation(pointerSet);
		arrow.setAnimation(pointerSet);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.simulate:
				startActivity(new Intent(this, SimulateActivity.class));
				break;
			case R.id.monitor:
				sp = getSharedPreferences("user", MODE_PRIVATE);
				if (sp.contains("nickname")) {
					startActivity(new Intent(this, MonitorActivity.class));
				} else {
					startActivity(new Intent(this, LoginActivity.class));
				}
				finish();
				break;
			case R.id.buy:
				startActivity(new Intent(this, ShopActivity.class));
				finish();
				break;
		}
	}
}
