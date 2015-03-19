package com.fetal.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.fetal.R;
import com.fetal.base.ActivityBase;
import com.fetal.util.InputChecker;

public class ShopActivity extends ActivityBase implements OnEditorActionListener{

	private InputMethodManager imm;
	private EditText name, mobile, addr;
	private Handler handler;
	private ImageView confirm;
	private RelativeLayout body;
	private LinearLayout bottom;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);
		initTopUI();
		initBottomUI();
		initUI();
	}
	
	/**
	 * 初始化UI
	 */
	private void initUI() {
		imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		top_title.setText("购买");
		name = (EditText) findViewById(R.id.name);
		name.setOnEditorActionListener(this);
		mobile = (EditText) findViewById(R.id.mobile);
		mobile.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		mobile.setOnEditorActionListener(this);
		addr = (EditText) findViewById(R.id.addr);
		addr.setOnEditorActionListener(this);
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				startActivity(new Intent(getBaseContext(), ConfirmActivity.class));
			}
		};
		confirm = (ImageView) findViewById(R.id.confirm);
		confirm.setOnClickListener(this);
		bottom = (LinearLayout) findViewById(R.id.bottom);
		body = (RelativeLayout) findViewById(R.id.body);
		body.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				if (body.getRootView().getHeight() - body.getHeight() > 100) {
					bottom.setVisibility(View.GONE);
				} else {
					bottom.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
	@Override  
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null) {
                if (getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
		if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
		return false;
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.confirm:
				//获取各值
				final String vname = name.getText().toString().trim();
				final String vmobile = mobile.getText().toString().trim();
				final String vaddr = addr.getText().toString().trim();
				String notice = "";
				//各项检测
				switch (InputChecker.checkNickname(vname)) {
					case 1:
						if (!"".equals(notice)) notice += "\n\n";
						notice += "请填写收货人名称";
						break;
					case 2:
						if (!"".equals(notice)) notice += "\n\n";
						notice += "收货人名称长度不能超过16个字符";
						break;
				}
				switch (InputChecker.checkMobile(vmobile)) {
					case 1:
						if (!"".equals(notice)) notice += "\n\n";
						notice += "请填写手机号码";
						break;
					case 2:
						if (!"".equals(notice)) notice += "\n\n";
						notice += "请输入有效的手机号码";
						break;
				}
				switch (InputChecker.checkAddr(vaddr)) {
					case 1:
						if (!"".equals(notice)) notice += "\n\n";
						notice += "请填写收货地址";
						break;
					case 2:
						if (!"".equals(notice)) notice += "\n\n";
						notice += "昵称长度不能超过100个字符";
						break;
				}
				//执行检测结果
				if ("".equals(notice)) {
					Intent intent = new Intent(this, ConfirmActivity.class);
					intent.putExtra("name", vname);
					intent.putExtra("mobile", vmobile);
					intent.putExtra("addr", vaddr);
					startActivity(intent);
				} else {
					Toast.makeText(this, notice, Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
}
