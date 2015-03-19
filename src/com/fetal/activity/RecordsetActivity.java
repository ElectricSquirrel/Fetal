package com.fetal.activity;

import android.os.Bundle;
import android.widget.ListView;

import com.fetal.R;
import com.fetal.base.ActivityBase;
import com.fetal.adapter.RecordsetAdapter;

public class RecordsetActivity extends ActivityBase{
	
	private ListView list;
	private RecordsetAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recordset);
		initTopUI();
		initBottomUI();
		initUI();
	}
	
	/**
	 * 初始化UI
	 */
	private void initUI() {
		top_title.setText("监护记录");
		list = (ListView) findViewById(R.id.list);
		adapter = new RecordsetAdapter(this);
		list.setAdapter(adapter);
		list.setDivider(null);
		list.setDividerHeight(10);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
}
