package com.fetal.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fetal.R;
import com.fetal.base.ActivityBase;
import com.fetal.bean.OrderBean;
import com.fetal.util.DatabaseOperator;
import com.fetal.util.WebServer;

public class ConfirmActivity extends ActivityBase{
	
	private ImageView submit, cancle;
	private TextView name, mobile, addr;
	private Handler handler;
	private ProgressDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirm);
		initTopUI();
		initUI();
	}
	
	/**
	 * 初始化UI
	 */
	private void initUI() {
		top_title.setText("确认信息");
		top_left.setImageResource(R.drawable.btn_back);
		top_left.setVisibility(View.VISIBLE);
		top_left.setOnClickListener(this);
		submit = (ImageView) findViewById(R.id.submit);
		submit.setOnClickListener(this);
		cancle = (ImageView) findViewById(R.id.cancle);
		cancle.setOnClickListener(this);
		name = (TextView) findViewById(R.id.name);
		name.setText(getIntent().getStringExtra("name"));
		mobile = (TextView) findViewById(R.id.mobile);
		mobile.setText(getIntent().getStringExtra("mobile"));
		addr = (TextView) findViewById(R.id.addr);
		addr.setText(getIntent().getStringExtra("addr"));
		dialog = new ProgressDialog(this);
		dialog.setMessage("正在提交...");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				dialog.dismiss();
				switch (msg.what) {
				case 1:
					Toast.makeText(getBaseContext(), "订单提交成功！", Toast.LENGTH_SHORT).show();
					break;
				case 0:
					Toast.makeText(getBaseContext(), "订单提交失败，请重新尝试", Toast.LENGTH_LONG).show();
					break;
				}
			}
		};
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.top_left:
			case R.id.cancle:
				finish();
				break;
			case R.id.submit:
				dialog.show();
				WebServer server = new WebServer();
				OrderBean order = new OrderBean();
				DatabaseOperator db = new DatabaseOperator(this);
				SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
				int member = db.getMemberRemoteId(sp.getInt("id", 0));
				db.closeDatabase();
				order.setMember(member);
				order.setProduct(1);
				order.setName(name.getText().toString());
				order.setMobile(mobile.getText().toString());
				order.setAddr(addr.getText().toString());
				server.order(order, handler);
				break;
		}
	}
}
