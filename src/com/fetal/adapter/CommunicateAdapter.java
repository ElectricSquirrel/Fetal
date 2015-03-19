package com.fetal.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.fetal.R;
import com.fetal.activity.MusicActivity;
import com.fetal.activity.TmusicActivity;
import com.fetal.activity.TopicActivity;
import com.fetal.bean.TopicBean;
import com.fetal.util.WebServer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CommunicateAdapter extends BaseAdapter{
	
	private Context context;
	private LayoutInflater inflater;
	private EditText input;
	private LinearLayout bottom;
	private InputMethodManager imm;
	private List<TopicBean> collection;
	private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	public CommunicateAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}
	
	public void setInput(EditText input) {
		this.input = input;
	}
	
	public void setBottom(LinearLayout bottom) {
		this.bottom = bottom;
	}
	
	public void setIMM(InputMethodManager imm) {
		this.imm = imm;
	}
	
	public void setCollection(List<TopicBean> collection) {
		this.collection = collection;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return collection.size();
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
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.adapter_communicate_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
			holder.calendar = (TextView) convertView.findViewById(R.id.calendar);
			holder.chart = (ImageView) convertView.findViewById(R.id.chart);
			holder.nickname = (TextView) convertView.findViewById(R.id.nickname);
			holder.txt = (TextView) convertView.findViewById(R.id.txt);
			holder.date = (TextView) convertView.findViewById(R.id.date);
			holder.comments = (LinearLayout) convertView.findViewById(R.id.comments);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final TopicBean topic = collection.get(position);
		if (topic.getPregnancy() == 0) {
			holder.title.setText("分享了一个混音记录");
			holder.nickname.setText(topic.getNickname());
			holder.date.setText("分享于" + format.format(new Date(topic.getDate())));
			holder.txt.setVisibility(View.GONE);
			try {
				holder.thumbnail.setImageDrawable(Drawable.createFromStream((InputStream) new URL(topic.getThumbnail()).getContent(), "src"));
				holder.chart.setImageResource(R.drawable.img_topic_mix);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			holder.title.setText("分享了一个胎心记录");
			holder.calendar.setText("孕" + topic.getPregnancy() + "周 " + topic.getPregnancy() * 7 + "天");
			holder.nickname.setText(topic.getNickname());
			holder.date.setText("分享于" + format.format(new Date(topic.getDate())));
			holder.txt.setVisibility(View.VISIBLE);
			switch (topic.getReport()) {
				case 1:
					holder.txt.setText("孕妈，本次监测结果显示  宝宝心率正常  注：请坚持定期监护胎心状况；可发给医生做专业分析；也可分享给您身边的其他孕妈。");
					break;
				case 2:
					holder.txt.setText("孕妈，本次监测结果显示  宝宝心率偏高  注：请坚持定期监护胎心状况；可发给医生做专业分析；也可分享给您身边的其他孕妈。");
					break;
				case 3:
					holder.txt.setText("孕妈，本次监测结果显示  宝宝心率偏低  注：请坚持定期监护胎心状况；可发给医生做专业分析；也可分享给您身边的其他孕妈。");
					break;
			}
			try {
				holder.thumbnail.setImageDrawable(Drawable.createFromStream((InputStream) new URL(topic.getThumbnail()).getContent(), "src"));
				holder.chart.setImageDrawable(Drawable.createFromStream((InputStream) new URL(topic.getChart()).getContent(), "src"));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (topic.getPregnancy() == 0) {
					Intent intent = new Intent(context, TmusicActivity.class);
					intent.putExtra("id", collection.get(position).getId());
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				} else {
					Intent intent = new Intent(context, TopicActivity.class);
					intent.putExtra("id",  collection.get(position).getId());
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			}
		});
		return convertView;
	}
	
	static class ViewHolder {
		private TextView calendar, nickname, txt, date, title;
		private ImageView chart, thumbnail;
		private LinearLayout comments;
	}

}
