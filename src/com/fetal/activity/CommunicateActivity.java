package com.fetal.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.fetal.R;
import com.fetal.adapter.CommunicateAdapter;
import com.fetal.base.ActivityBase;
import com.fetal.bean.TopicBean;
import com.fetal.util.WebServer;

public class CommunicateActivity extends ActivityBase implements OnEditorActionListener{
	
	private InputMethodManager imm;
	private ListView list;
	private LinearLayout common_bottom;
	private EditText input;
	private RelativeLayout body;
	private ProgressDialog dialog;
	private Handler handler;
	private List<TopicBean> collection;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_communicate);
		initTopUI();
		initBottomUI();
		initUI();
	}
	
	/**
	 * 初始化UI
	 */
	@SuppressLint("NewApi")
	private void initUI() {
		//设置网络连接
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
		imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		top_title.setText("孕心交流");
		common_bottom = (LinearLayout) findViewById(R.id.common_bottom);
		input = (EditText) findViewById(R.id.input);
		input.setOnEditorActionListener(this);
		list = (ListView) findViewById(R.id.list);
		body = (RelativeLayout) findViewById(R.id.body);
		body.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				int height = body.getRootView().getHeight() - body.getHeight();
				if (height > 100) {
					input.requestFocus();
		            input.setVisibility(View.VISIBLE);
		            common_bottom.setVisibility(View.GONE);
				} else {
					input.clearFocus();
		            input.setVisibility(View.GONE);
		            common_bottom.setVisibility(View.VISIBLE);
				}
			}
		});
		dialog = new ProgressDialog(this);
		dialog.setMessage("请稍候...");
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				dialog.dismiss();
				switch (msg.what) {
				case 1:
					CommunicateAdapter adapter = new CommunicateAdapter(getBaseContext());
					adapter.setInput(input);
					adapter.setBottom(common_bottom);
					adapter.setIMM(imm);
					adapter.setCollection(collection);
					list.setAdapter(adapter);
					list.setDivider(null);
					list.setDividerHeight(10);
					break;
				case 0:
					Toast.makeText(getBaseContext(), "加载交流信息失败", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		};
		dialog.show();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				WebServer server = new WebServer();
				collection = server.communicate(1, handler);
			}
		}).start();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		// TODO Auto-generated method stub
//		if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
//			imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
//            input.setVisibility(View.GONE);
//            common_bottom.setVisibility(View.VISIBLE);
//		}
		return false;
	}
}
