/*
 * 瀹樼綉鍦扮珯:http://www.ShareSDK.cn
 * 鎶�湳鏀寔QQ: 4006852216
 * 瀹樻柟寰俊:ShareSDK   锛堝鏋滃彂甯冩柊鐗堟湰鐨勮瘽锛屾垜浠皢浼氱涓�椂闂撮�杩囧井淇″皢鐗堟湰鏇存柊鍐呭鎺ㄩ�缁欐偍銆傚鏋滀娇鐢ㄨ繃绋嬩腑鏈変换浣曢棶棰橈紝涔熷彲浠ラ�杩囧井淇′笌鎴戜滑鍙栧緱鑱旂郴锛屾垜浠皢浼氬湪24灏忔椂鍐呯粰浜堝洖澶嶏級
 *
 * Copyright (c) 2013骞�ShareSDK.cn. All rights reserved.
 */

package cn.sharesdk.demo;

import java.util.ArrayList;
import java.util.HashMap;

import com.fetal.R;

import m.framework.ui.widget.slidingmenu.SlidingMenu;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import cn.sharesdk.framework.CustomPlatform;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.TitleLayout;
import cn.sharesdk.framework.utils.UIHandler;
import cn.sharesdk.onekeyshare.ShareCore;

/**
 * 鎺堟潈鍜屽彇娑堟巿鏉冩紨绀洪〉闈� * <p>
 * 鐢变簬UI鏄剧ず鐨勯渶瑕佹巿鏉冭繃鐨勫钩鍙版樉绀鸿处鎴风殑鍚嶇О锛� *鍥犳姝ら〉闈簨瀹炰笂灞曠ず鐨勬槸鈥滆幏鍙栫敤鎴疯祫鏂欌�鍜屸�鍙栨秷
 *鎺堟潈鈥濅袱涓姛鑳姐�濡傛灉鎯崇湅绾补鐨勨�鎺堟潈鈥濇搷浣滐紝璇峰弬
 *鑰儃@link GetTokenPage}椤甸潰鐨勭浉鍏充唬鐮併�
 */
public class AuthPage extends SlidingMenuPage implements
		OnClickListener, PlatformActionListener {
	private View pageView;
	private TitleLayout llTitle;
	private AuthAdapter adapter;

	public AuthPage(SlidingMenu menu) {
		super(menu);
		pageView = getPage();

		llTitle = (TitleLayout) pageView.findViewById(R.id.llTitle);
		llTitle.getBtnBack().setOnClickListener(this);
		llTitle.getTvTitle().setText(R.string.sm_item_auth);

		ListView lvPlats = (ListView) pageView.findViewById(R.id.lvPlats);
		lvPlats.setSelector(new ColorDrawable());
		adapter = new AuthAdapter(this);
		lvPlats.setAdapter(adapter);
		lvPlats.setOnItemClickListener(adapter);
	}

	protected View initPage() {
		return LayoutInflater.from(getContext()).inflate(R.layout.page_auth, null);
	}

	public void onClick(View v) {
		if (v.equals(llTitle.getBtnBack())) {
			if (isMenuShown()) {
				hideMenu();
			}
			else {
				showMenu();
			}
		}
	}

	public void onComplete(Platform plat, int action,
			HashMap<String, Object> res) {
		Message msg = new Message();
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	public void onError(Platform plat, int action, Throwable t) {
		t.printStackTrace();

		Message msg = new Message();
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	public void onCancel(Platform plat, int action) {
		Message msg = new Message();
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	/**
	 * 澶勭悊鎿嶄綔缁撴灉
	 * <p>
	 * 濡傛灉鑾峰彇鍒扮敤鎴风殑鍚嶇О锛屽垯鏄剧ず鍚嶇О锛涘惁鍒欏鏋滃凡缁忔巿鏉冿紝鍒欐樉绀�	 *骞冲彴鍚嶇О
	 */
	public boolean handleMessage(Message msg) {
		Platform plat = (Platform) msg.obj;
		String text = MainActivity.actionToString(msg.arg2);
		switch (msg.arg1) {
			case 1: {
				// 鎴愬姛
				text = plat.getName() + " completed at " + text;
				Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
			}
			break;
			case 2: {
				// 澶辫触
				text = plat.getName() + " caught error at " + text;
				Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
				return false;
			}
			case 3: {
				// 鍙栨秷
				text = plat.getName() + " canceled at " + text;
				Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
				return false;
			}
		}

		adapter.notifyDataSetChanged();
		return false;
	}

	private static class AuthAdapter extends BaseAdapter implements OnItemClickListener {
		private AuthPage page;
		private ArrayList<Platform> platforms;

		public AuthAdapter(AuthPage page) {
			this.page = page;

			// 鑾峰彇骞冲彴鍒楄〃
			Platform[] tmp = ShareSDK.getPlatformList(page.getContext());
			platforms = new ArrayList<Platform>();
			if (tmp == null) {
				return;
			}

			for (Platform p : tmp) {
				String name = p.getName();
				if ((p instanceof CustomPlatform)
						|| !ShareCore.canAuthorize(p.getContext(), name)) {
					continue;
				}
				platforms.add(p);
			}
		}

		public int getCount() {
			return platforms == null ? 0 : platforms.size();
		}

		public Platform getItem(int position) {
			return platforms.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(page.getContext(), R.layout.auth_page_item, null);
			}

			int count = getCount();
			View llItem = convertView.findViewById(R.id.llItem);
			int dp_10 = cn.sharesdk.framework.utils.R.dipToPx(parent.getContext(), 10);
			if (count == 1) {
				llItem.setBackgroundResource(R.drawable.list_item_single_normal);
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, dp_10, dp_10, dp_10);
			}
			else if (position == 0) {
				llItem.setBackgroundResource(R.drawable.list_item_first_normal);
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, dp_10, dp_10, 0);
			}
			else if (position == count - 1) {
				llItem.setBackgroundResource(R.drawable.list_item_last_normal);
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, 0, dp_10, dp_10);
			}
			else {
				llItem.setBackgroundResource(R.drawable.list_item_middle_normal);
				llItem.setPadding(0, 0, 0, 0);
				convertView.setPadding(dp_10, 0, dp_10, 0);
			}

			Platform plat = getItem(position);
			ImageView ivLogo = (ImageView) convertView.findViewById(R.id.ivLogo);
			Bitmap logo = getIcon(plat);
			if (logo != null && !logo.isRecycled()) {
				ivLogo.setImageBitmap(logo);
			}
			CheckedTextView ctvName = (CheckedTextView) convertView.findViewById(R.id.ctvName);
			ctvName.setChecked(plat.isValid());
			if (plat.isValid()) {
				String userName = plat.getDb().get("nickname");
				if (userName == null || userName.length() <= 0
						|| "null".equals(userName)) {
					// 濡傛灉骞冲彴宸茬粡鎺堟潈鍗存病鏈夋嬁鍒板笎鍙峰悕绉帮紝鍒欒嚜鍔ㄨ幏鍙栫敤鎴疯祫鏂欙紝浠ヨ幏鍙栧悕绉�					userName = getName(plat);
					plat.setPlatformActionListener(page);
					plat.showUser(null);
				}
				ctvName.setText(userName);
			}
			else {
				ctvName.setText(R.string.not_yet_authorized);
			}
			return convertView;
		}

		private Bitmap getIcon(Platform plat) {
			if (plat == null) {
				return null;
			}

			String name = plat.getName();
			if (name == null) {
				return null;
			}

			String resName = "logo_" + plat.getName();
			int resId = cn.sharesdk.framework.utils.R.getResId(R.drawable.class, resName);
			return BitmapFactory.decodeResource(page.getResources(), resId);
		}

		private String getName(Platform plat) {
			if (plat == null) {
				return "";
			}

			String name = plat.getName();
			if (name == null) {
				return "";
			}

			int resId = cn.sharesdk.framework.utils.R.getStringRes(page.getContext(), plat.getName());
			return page.getContext().getString(resId);
		}

		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Platform plat = getItem(position);
			CheckedTextView ctvName = (CheckedTextView) view.findViewById(R.id.ctvName);
			if (plat == null) {
				ctvName.setChecked(false);
				ctvName.setText(R.string.not_yet_authorized);
				return;
			}

			if (plat.isValid()) {
				plat.removeAccount();
				ctvName.setChecked(false);
				ctvName.setText(R.string.not_yet_authorized);
				return;
			}

			plat.setPlatformActionListener(page);
			plat.showUser(null);
		}

	}

}
