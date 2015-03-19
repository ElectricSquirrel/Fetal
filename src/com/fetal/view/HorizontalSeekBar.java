package com.fetal.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class HorizontalSeekBar extends SeekBar{

	public HorizontalSeekBar(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public HorizontalSeekBar(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public HorizontalSeekBar(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
}
