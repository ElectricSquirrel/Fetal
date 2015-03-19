package com.fetal.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class VerticalScrollView extends ScrollView{
	public VerticalScrollView(Context context) {
	    super(context);
	}
	
	public VerticalScrollView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}
	
	public VerticalScrollView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
	
	public interface OnScrollChangedListener{
	    public void onScrollChanged(int x, int y, int oldxX, int oldY);
	}
	
	private OnScrollChangedListener onScrollChangedListener;
	
	/**
	* 
	* @param onScrollChangedListener
	*/
	public void setOnScrollListener(OnScrollChangedListener onScrollChangedListener){
	    this.onScrollChangedListener=onScrollChangedListener;
	}
	
	@Override
	protected void onScrollChanged(int x, int y, int oldX, int oldY){
	    super.onScrollChanged(x, y, oldX, oldY);
	    if(onScrollChangedListener!=null){
	        onScrollChangedListener.onScrollChanged(x, y, oldX, oldY);
	    }
	}
}
