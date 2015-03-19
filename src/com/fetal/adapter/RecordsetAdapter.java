package com.fetal.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fetal.R;
import com.fetal.activity.MusicActivity;
import com.fetal.activity.ReportActivity;
import com.fetal.bean.MixBean;
import com.fetal.bean.RecordBean;
import com.fetal.util.DatabaseOperator;

public class RecordsetAdapter extends BaseAdapter{
	
	private Context context;
	private LayoutInflater inflater;
	private Cursor cursor;
	private DatabaseOperator db;
	private int count;
	private static final int RECORD = 1, MIX = 2;
	private List<Object> rows;
	
	public RecordsetAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		loadRecordsetList();
	}
	
	private void loadRecordsetList() {
		db = new DatabaseOperator(context);
		SharedPreferences sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
		cursor = db.showAllRecordset(sp.getInt("id", 0));
		rows = new ArrayList<Object>();
		while (cursor.moveToNext()) {
			switch (cursor.getInt(cursor.getColumnIndex("type"))) {
				case RECORD:
					rows.add(db.getOneRecord(cursor.getInt(cursor.getColumnIndex("item"))));
					break;
				case MIX:
					rows.add(db.getOneMix(cursor.getInt(cursor.getColumnIndex("item"))));
					break;
			}
		}
		cursor.close();
		db.closeDatabase();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return rows.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (rows.get(position) instanceof RecordBean) {
			final RecordBean recordBean = (RecordBean) rows.get(position);
			RecordHolder recordHolder;
			convertView = inflater.inflate(R.layout.adapter_recordset_item, null);
			recordHolder = new RecordHolder();
			recordHolder.calendar = (TextView) convertView.findViewById(R.id.calendar);
			recordHolder.chart = (ImageView) convertView.findViewById(R.id.chart);
			recordHolder.average = (TextView) convertView.findViewById(R.id.average);
			recordHolder.txt1 = (TextView) convertView.findViewById(R.id.txt1);
			recordHolder.txt2 = (TextView) convertView.findViewById(R.id.txt2);
			recordHolder.date = (TextView) convertView.findViewById(R.id.date);
			convertView.setTag(recordHolder);
			int week = recordBean.getPregnancy();
			recordHolder.calendar.setText("孕" + week + "周   " + week * 7 + "天");
			recordHolder.average.setText("平均胎心率：" + recordBean.getAverageBeat());
			Date recordDate = new Date(recordBean.getDate());
			SimpleDateFormat recordFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm");
			recordHolder.date.setText(recordFormat.format(recordDate));
			switch (recordBean.getReport()) {
				case 1:
					recordHolder.txt1.setText("恭喜您！");
					recordHolder.txt2.setText("本次监护结果显示宝宝是非常健康的！");
					break;
				case 2:
					recordHolder.txt1.setText("很抱歉！");
					recordHolder.txt2.setText("本次监护结果显示宝宝的心率偏高!");
					break;
				case 3:
					recordHolder.txt1.setText("很抱歉！");
					recordHolder.txt2.setText("本次监护结果显示宝宝的心率偏低!");
					break;
			}
			//缩略图处理
			BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        // 获取这个图片的宽和高
	        Bitmap bitmap = BitmapFactory.decodeFile(recordBean.getChart(), options); //此时返回bm为空
	        options.inJustDecodeBounds = false;
	        options.inSampleSize = 2;
	        //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
	        bitmap = BitmapFactory.decodeFile(recordBean.getChart(), options);
	        recordHolder.chart.setImageBitmap(bitmap);
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, ReportActivity.class);
					intent.putExtra("id", recordBean.getId());
					context.startActivity(intent);
				}
			});
			recordHolder = null;
		}
		if (rows.get(position) instanceof MixBean) {
			MixHolder mixHolder;
			final MixBean mixBean = (MixBean) rows.get(position);
			convertView = inflater.inflate(R.layout.adapter_recordset_mix, null);
			mixHolder = new MixHolder();
			mixHolder.date = (TextView) convertView.findViewById(R.id.date);
			mixHolder.music = (TextView) convertView.findViewById(R.id.music);
			mixHolder.record = (TextView) convertView.findViewById(R.id.record);
			convertView.setTag(mixHolder);
			mixHolder.record.setText(mixBean.getRecord());
			mixHolder.music.setText(mixBean.getMusic());
			SimpleDateFormat mixFormat = new SimpleDateFormat("MM月dd日");
			Date mixDate = new Date(mixBean.getDate());
			mixHolder.date.setText(mixFormat.format(mixDate));
			
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(context, MusicActivity.class);
					intent.putExtra("id", mixBean.getId());
					context.startActivity(intent);
				}
			});
			mixHolder = null;
		}
		return convertView;
	}
	
	static class RecordHolder {
		private TextView calendar, average, txt1, txt2, date;
		private ImageView chart;
	}
	
	static class MixHolder {
		private TextView date, record, music;
	}
}
