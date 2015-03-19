package com.fetal.activity;

import android.os.Bundle;
import android.view.View;

import com.fetal.R;
import com.fetal.base.ActivityBase;

public class HelpActivity extends ActivityBase{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		initTopUI();
		initUI();
	}
	
	/**
	 * ≥ı ºªØUI
	 */
	private void initUI() {
		top_title.setText("∞Ô÷˙");
		top_left.setImageResource(R.drawable.btn_back);
		top_left.setVisibility(View.VISIBLE);
		top_left.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.top_left:
				finish();
				break;
		}
	}
}
