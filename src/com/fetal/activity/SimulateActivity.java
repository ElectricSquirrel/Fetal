package com.fetal.activity;

import java.math.BigDecimal;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.fetal.R;
import com.fetal.base.ActivityBase;
import com.fetal.view.VerticalScrollView;
import com.fetal.view.VerticalScrollView.OnScrollChangedListener;

public class SimulateActivity extends ActivityBase{
	
	private ImageView bg, suishi1, suishi2, jianrong1, jianrong2, jianrong3, 
			yinyue1, yinyue2, fenxiang1, fenxiang2, tishi1, tishi2, start;
	private RelativeLayout suishi, jianrong, yinyue, fenxiang, tishi;
	private VerticalScrollView scroll;
	private int width, height, size, ycurrent;
	private LinearLayout.LayoutParams suishiParams, jianrongParams, yinyueParams, fenxiangParams, tishiParams, startParams;
	private boolean showSuishi = false, showJianrong = false, showYinyue = false, showFenxiang = false, showTishi = false;
	private int pSuishi, pJianrong, pYinyue, pFenxiang, pTishi;
	private float xsuishi, ysuishi, xjianrong, yjianrong, xyinyue, yyinyue, xfenxiang, yfenxiang, xtishi, ytishi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simulate);
		initUI();
	}
	
	/**
	 * 初始化UI
	 */
	private void initUI() {
		width = getWindowManager().getDefaultDisplay().getWidth();
		height = getWindowManager().getDefaultDisplay().getHeight();
		bg = (ImageView) findViewById(R.id.bg);
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(getResources(), R.drawable.simulate, opts);
		size = opts.outHeight * width / opts.outWidth;
		//随时
		suishi = (RelativeLayout) findViewById(R.id.suishi);
		suishiParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		suishiParams.setMargins(0, size * 993 / 3748, 0, 0);
		suishi.setLayoutParams(suishiParams);
		suishi1 = (ImageView) findViewById(R.id.suishi1);
		suishi2 = (ImageView) findViewById(R.id.suishi2);
		suishi.setVisibility(View.INVISIBLE);
		//兼容
		jianrong = (RelativeLayout) findViewById(R.id.jianrong);
		jianrongParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		jianrongParams.setMargins(0, size * 123 / 3748, 0, 0);
		jianrong.setLayoutParams(jianrongParams);
		jianrong1 = (ImageView) findViewById(R.id.jianrong1);
		jianrong2 = (ImageView) findViewById(R.id.jianrong2);
		jianrong3 = (ImageView) findViewById(R.id.jianrong3);
		jianrong.setVisibility(View.INVISIBLE);
		//音乐
		yinyue = (RelativeLayout) findViewById(R.id.yinyue);
		yinyueParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		yinyueParams.setMargins(0, size * 179 / 3748, 0, 0);
		yinyue.setLayoutParams(yinyueParams);
		yinyue1 = (ImageView) findViewById(R.id.yinyue1);
		yinyue2 = (ImageView) findViewById(R.id.yinyue2);
		yinyue.setVisibility(View.INVISIBLE);
		//分享
		fenxiang = (RelativeLayout) findViewById(R.id.fenxiang);
		fenxiangParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		fenxiangParams.setMargins(0, size * 187 / 3748, 0, 0);
		fenxiang.setLayoutParams(fenxiangParams);
		fenxiang1 = (ImageView) findViewById(R.id.fenxiang1);
		fenxiang2 = (ImageView) findViewById(R.id.fenxiang2);
		fenxiang.setVisibility(View.INVISIBLE);
		//提示
		tishi = (RelativeLayout) findViewById(R.id.tishi);
		tishiParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tishiParams.setMargins(0, size * 230 / 3748, 0, 0);
		tishi.setLayoutParams(tishiParams);
		tishi1 = (ImageView) findViewById(R.id.tishi1);
		tishi2 = (ImageView) findViewById(R.id.tishi2);
		tishi.setVisibility(View.INVISIBLE);
		
		start = (ImageView) findViewById(R.id.start);
		startParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		startParams.setMargins(0, size * 345 / 3748, 0, 0);
		start.setLayoutParams(startParams);
		start.setOnClickListener(this);
		//位置
		pSuishi = size * 994 / 3748;
		xsuishi = new BigDecimal(265).divide(new BigDecimal(640), 2, BigDecimal.ROUND_DOWN).floatValue();
		ysuishi = new BigDecimal(290).divide(new BigDecimal(343), 2, BigDecimal.ROUND_DOWN).floatValue();
		pJianrong = size * 1459 / 3748;
		xjianrong = new BigDecimal(300).divide(new BigDecimal(640), 2, BigDecimal.ROUND_DOWN).floatValue();
		yjianrong = new BigDecimal(230).divide(new BigDecimal(349), 2, BigDecimal.ROUND_DOWN).floatValue();
		pYinyue = size * 1986 / 3748;
		xyinyue = new BigDecimal(322).divide(new BigDecimal(640), 2, BigDecimal.ROUND_DOWN).floatValue();
		yyinyue = new BigDecimal(226).divide(new BigDecimal(304), 2, BigDecimal.ROUND_DOWN).floatValue();
		pFenxiang = size * 2478 / 3748;
		xfenxiang = new BigDecimal(327).divide(new BigDecimal(640), 2, BigDecimal.ROUND_DOWN).floatValue();
		yfenxiang = new BigDecimal(118).divide(new BigDecimal(271), 2, BigDecimal.ROUND_DOWN).floatValue();
		pTishi = size * 2980 / 3748;
		xtishi = new BigDecimal(286).divide(new BigDecimal(640), 2, BigDecimal.ROUND_DOWN).floatValue();
		ytishi = new BigDecimal(54).divide(new BigDecimal(220), 2, BigDecimal.ROUND_DOWN).floatValue();
		//滚动
		scroll = (VerticalScrollView) findViewById(R.id.scroll);
		scroll.setOnScrollListener(new OnScrollChangedListener() {
			
			@Override
			public void onScrollChanged(int x, int y, int oldX, int oldY) {
				// TODO Auto-generated method stub
				ycurrent = y + height / 2;
				if (y > oldY) {
					//随时
					if (ycurrent > pSuishi && !showSuishi) {
						suishi.setVisibility(View.VISIBLE);
						AnimationSet set1 = new AnimationSet(true);
						set1.addAnimation(new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, xsuishi, Animation.RELATIVE_TO_SELF, ysuishi));
						set1.addAnimation(new RotateAnimation(+180, 0, Animation.RELATIVE_TO_SELF, xsuishi, Animation.RELATIVE_TO_SELF, ysuishi));
						set1.setFillAfter(true);
						set1.setDuration(1000);
						suishi1.startAnimation(set1);
						Animation ani1 = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, xsuishi, Animation.RELATIVE_TO_SELF, ysuishi);
						ani1.setDuration(1000);
						ani1.setFillAfter(true);
						suishi2.startAnimation(ani1);
						set1 = null;
						ani1 = null;
						showSuishi = true;
					}
					//兼容
					if (ycurrent > pJianrong && !showJianrong) {
						jianrong.setVisibility(View.VISIBLE);
						Animation ani1 = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, xjianrong, Animation.RELATIVE_TO_SELF, yjianrong);
						ani1.setDuration(1000);
						ani1.setFillAfter(true);
						jianrong3.startAnimation(ani1);
						Animation ani2 = new TranslateAnimation(-width, 0, 0, 0);
						ani2.setDuration(1000);
						ani2.setFillAfter(true);
						jianrong2.startAnimation(ani2);
						Animation ani3 = new TranslateAnimation(width, 0, 0, 0);
						ani3.setDuration(1000);
						ani3.setFillAfter(true);
						jianrong1.startAnimation(ani3);
						showJianrong = true;
					}
					//音乐
					if (ycurrent > pYinyue && !showYinyue) {
						yinyue.setVisibility(View.VISIBLE);
						Animation ani1 = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, xyinyue, Animation.RELATIVE_TO_SELF, yyinyue);
						ani1.setDuration(1000);
						ani1.setFillAfter(true);
						yinyue2.startAnimation(ani1);
						Animation ani2 = new TranslateAnimation(width, 0, 0, 0);
						ani2.setDuration(1000);
						ani2.setFillAfter(true);
						yinyue1.startAnimation(ani2);
						showYinyue = true;
					}
					//分享
					if (ycurrent > pFenxiang && !showFenxiang) {
						fenxiang.setVisibility(View.VISIBLE);
						Animation ani1 = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, xfenxiang, Animation.RELATIVE_TO_SELF, yfenxiang);
						ani1.setDuration(1000);
						ani1.setFillAfter(true);
						fenxiang2.startAnimation(ani1);
						fenxiang1.startAnimation(ani1);
						showFenxiang = true;
					}
					//提示
					if (ycurrent > pTishi && !showTishi) {
						tishi.setVisibility(View.VISIBLE);
						Animation ani1 = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, xtishi, Animation.RELATIVE_TO_SELF, ytishi);
						ani1.setDuration(1000);
						ani1.setFillAfter(true);
						tishi2.startAnimation(ani1);
						Animation ani2 = new TranslateAnimation(width, 0, 0, 0);
						ani2.setDuration(1000);
						ani2.setFillAfter(true);
						tishi1.startAnimation(ani2);
						showTishi = true;
					}
				} else {
					//随时
					if (ycurrent <= pSuishi && showSuishi) {
						AnimationSet set1 = new AnimationSet(true);
						set1.addAnimation(new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, xsuishi, Animation.RELATIVE_TO_SELF, ysuishi));
						set1.addAnimation(new RotateAnimation(0, +180, Animation.RELATIVE_TO_SELF, xsuishi, Animation.RELATIVE_TO_SELF, ysuishi));
						set1.setFillAfter(true);
						set1.setDuration(1000);
						suishi1.startAnimation(set1);
						Animation ani1 = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, xsuishi, Animation.RELATIVE_TO_SELF, ysuishi);
						ani1.setDuration(1000);
						ani1.setFillAfter(true);
						suishi2.startAnimation(ani1);
						set1 = null;
						ani1 = null;
						showSuishi = false;
					}
					//兼容
					if (ycurrent <= pJianrong && showJianrong) {
						Animation ani1 = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, xjianrong, Animation.RELATIVE_TO_SELF, yjianrong);
						ani1.setDuration(1000);
						ani1.setFillAfter(true);
						jianrong3.startAnimation(ani1);
						Animation ani2 = new TranslateAnimation(0, -width, 0, 0);
						ani2.setDuration(1000);
						ani2.setFillAfter(true);
						jianrong2.startAnimation(ani2);
						Animation ani3 = new TranslateAnimation(0, width, 0, 0);
						ani3.setDuration(1000);
						ani3.setFillAfter(true);
						jianrong1.startAnimation(ani3);
						showJianrong = false;
					}
					//音乐
					if (ycurrent <= pYinyue && showYinyue) {
						Animation ani1 = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, xyinyue, Animation.RELATIVE_TO_SELF, yyinyue);
						ani1.setDuration(1000);
						ani1.setFillAfter(true);
						yinyue2.startAnimation(ani1);
						Animation ani2 = new TranslateAnimation(0, width, 0, 0);
						ani2.setDuration(1000);
						ani2.setFillAfter(true);
						yinyue1.startAnimation(ani2);
						showYinyue = false;
					}
					//分享
					if (ycurrent <= pFenxiang && showFenxiang) {
						Animation ani1 = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, xfenxiang, Animation.RELATIVE_TO_SELF, yfenxiang);
						ani1.setDuration(1000);
						ani1.setFillAfter(true);
						fenxiang2.startAnimation(ani1);
						fenxiang1.startAnimation(ani1);
						showFenxiang = false;
					}
					//提示
					if (ycurrent <= pTishi && showTishi) {
						Animation ani1 = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, xtishi, Animation.RELATIVE_TO_SELF, ytishi);
						ani1.setDuration(1000);
						ani1.setFillAfter(true);
						tishi2.startAnimation(ani1);
						Animation ani2 = new TranslateAnimation(0, width, 0, 0);
						ani2.setDuration(1000);
						ani2.setFillAfter(true);
						tishi1.startAnimation(ani2);
						showTishi = false;
					}
				}
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.start:
				startActivity(new Intent(this, MonitorActivity.class));
				finish();
				break;
		}
	}
}
