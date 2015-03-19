package com.fetal.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fetal.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;

public class WavePainter {
	
	/**
	 * 获取画布对象
	 */
	private SurfaceView surface;
	
	/**
	 * 获取画布句柄
	 */
	private SurfaceHolder holder;
	
	/**
	 * 画笔
	 */
	private Paint paint;
	
	private int width, height;
	private float oldX, oldY, y50step = 0, y120step = 0, y140step = 0, y160step = 0, y210step = 0;
	private float cy50step = 0, cy120step = 0, cy140step = 0, cy160step = 0, cy210step = 0;
	private Canvas canvas;
	private List<Integer> beating;
	private List<Float> tmpbeat;
	private Context context;
	
	public WavePainter() {
		initWidget();
	}
	
	public WavePainter(Context context) {
		this.context = context;
		paint = new Paint();
		paint.setColor(Color.rgb(254, 144, 169));
		paint.setStrokeWidth(4);
		paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
	}
	
	public WavePainter(Context context, SurfaceView surface) {
		this.context = context;
		this.surface = surface;
		this.holder = surface.getHolder();
		initWidget();
	}
	
	/**
	 * 初始化控件
	 */
	private void initWidget() {
		paint = new Paint();
		paint.setColor(Color.rgb(254, 144, 169));
		paint.setStrokeWidth(4);
		LayoutParams params = (LayoutParams) surface.getLayoutParams();
		width = params.width;
		height = params.height;
		oldX = 0;
		oldY = 0;
		beating = new ArrayList<Integer>();
		tmpbeat = new ArrayList<Float>();
	}
	
	/**
	 * 绘制指定区域
	 */
	public void drawWave(short[] data) {
		canvas = holder.lockCanvas(
			new Rect(0, 0, width, height)
		);
		canvas.drawColor(Color.BLACK, Mode.CLEAR);
		oldX = 0;
		oldY = 0;
		BigDecimal xrate = new BigDecimal(width).divide(new BigDecimal(data.length), 2, BigDecimal.ROUND_DOWN);
		float step = xrate.floatValue();
		canvas.drawLine(0, (height), width, (height), paint);
		for (int i = 0; i < data.length; i++) {
			float tmpX = oldX + step;
			canvas.drawLine(oldX, (height) - (oldY / 100), tmpX, (height) - (data[i] / 100), paint);
			oldX = tmpX;
			oldY = data[i];
		}
		holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
		canvas = null;
	}
	
	/**
	 * 绘制胎心率曲线
	 * @param beat
	 */
	public void drawBeat(int beat, boolean isWriting) {
		canvas = holder.lockCanvas(new Rect(0, 0, width, height));
		canvas.drawColor(Color.BLACK, Mode.CLEAR);
		if (oldX >= width) {//清空画布
			tmpbeat.clear();
		}
		canvas.drawColor(Color.TRANSPARENT);
		float ypoint = getYpoint(beat);
		tmpbeat.add(ypoint);
		//绘制
		float xstep = 10;
		for (int i = 0; i < tmpbeat.size(); i++) {
			if (i == 0) {
				canvas.drawPoint(0, tmpbeat.get(0), paint);
				oldX = 55 * width / 583;
			} else {
				float tmpX = oldX + xstep;
				canvas.drawLine(oldX, tmpbeat.get(i - 1), tmpX, tmpbeat.get(i), paint);
				oldX = tmpX;
			}
		}
		if (isWriting) {
			beating.add(beat);
		}
		holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
		canvas = null;
	}
	
	/**
	 * 绘制胎心率曲线
	 * @param beat
	 */
	public void drawBeat(String point, boolean isRepeat) {
		canvas = holder.lockCanvas(new Rect(0, 0, width, height));
		canvas.drawColor(Color.BLACK, Mode.CLEAR);
		if (oldX >= width || isRepeat) {//清空画布
			tmpbeat.clear();
		}
		canvas.drawColor(Color.TRANSPARENT);
		float ypoint = getYpoint(Integer.valueOf(point));
		tmpbeat.add(ypoint);
		//绘制
		float xstep = 10;
		for (int i = 0; i < tmpbeat.size(); i++) {
			if (i == 0) {
				canvas.drawPoint(0, tmpbeat.get(0), paint);
				oldX = 55 * width / 583;
			} else {
				float tmpX = oldX + xstep;
				canvas.drawLine(oldX, tmpbeat.get(i - 1), tmpX, tmpbeat.get(i), paint);
				oldX = tmpX;
			}
		}
		holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
		canvas = null;
	}
	
	/**
	 * 获取Y坐标
	 * @param beat
	 * @return
	 */
	private float getYpoint(int beat){
		float ystep = 0;
		if (beat < 50) {
			if (y50step == 0) {
				BigDecimal yrate = new BigDecimal(height).divide(new BigDecimal(431), 2, BigDecimal.ROUND_DOWN);
				y50step = yrate.floatValue();
			}
			ystep = y50step;
		} else if (beat > 50 && beat < 120) {
			if (y120step == 0) {
				BigDecimal yrate = new BigDecimal(height).divide(new BigDecimal(390), 2, BigDecimal.ROUND_DOWN);
				y120step = yrate.floatValue();
			}
			ystep = y120step;
		} else if (beat > 120 && beat < 140) {
			if (y140step == 0) {
				BigDecimal yrate = new BigDecimal(height).divide(new BigDecimal(285), 2, BigDecimal.ROUND_DOWN);
				y140step = yrate.floatValue();
			}
			ystep = y140step;
		} else if (beat > 140 && beat < 160) {
			if (y160step == 0) {
				BigDecimal yrate = new BigDecimal(height).divide(new BigDecimal(236), 2, BigDecimal.ROUND_DOWN);
				y160step = yrate.floatValue();
			}
			ystep = y160step;
		} else if (beat > 160 && beat < 210) {
			if (y210step == 0) {
				BigDecimal yrate = new BigDecimal(height).divide(new BigDecimal(243), 2, BigDecimal.ROUND_DOWN);
				y210step = yrate.floatValue();
			}
			ystep = y210step;
		} else if (beat > 210) {
			ystep = 1;
		}
		return height - beat * ystep;
	}
	
	/**
	 * 获取Y坐标
	 * @param beat
	 * @return
	 */
	private float getYpoint(int beat, int length){
		float ystep = 0;
		if (beat < 50) {
			if (cy50step == 0) {
				BigDecimal yrate = new BigDecimal(length).divide(new BigDecimal(431), 2, BigDecimal.ROUND_DOWN);
				cy50step = yrate.floatValue();
			}
			ystep = cy50step;
		} else if (beat > 50 && beat < 120) {
			if (cy120step == 0) {
				BigDecimal yrate = new BigDecimal(length).divide(new BigDecimal(390), 2, BigDecimal.ROUND_DOWN);
				cy120step = yrate.floatValue();
			}
			ystep = cy120step;
		} else if (beat > 120 && beat < 140) {
			if (cy140step == 0) {
				BigDecimal yrate = new BigDecimal(length).divide(new BigDecimal(285), 2, BigDecimal.ROUND_DOWN);
				cy140step = yrate.floatValue();
			}
			ystep = cy140step;
		} else if (beat > 140 && beat < 160) {
			if (cy160step == 0) {
				BigDecimal yrate = new BigDecimal(length).divide(new BigDecimal(236), 2, BigDecimal.ROUND_DOWN);
				cy160step = yrate.floatValue();
			}
			ystep = cy160step;
		} else if (beat > 160 && beat < 210) {
			if (cy210step == 0) {
				BigDecimal yrate = new BigDecimal(length).divide(new BigDecimal(243), 2, BigDecimal.ROUND_DOWN);
				cy210step = yrate.floatValue();
			}
			ystep = cy210step;
		} else if (beat > 210) {
			ystep = 1;
		}
		return length - beat * ystep;
	}
	
	/**
	 * 画图
	 * @param data
	 */
	public void createBeatBitmap(String chart, List<Integer> beating) {
		int size = beating.size();
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		Bitmap vbg = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_report_bg, options);
		for (int i = 0; i < size; i++) {
			Bitmap bitmap = Bitmap.createBitmap(583, 317, Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			float bOldX = 55;
			float bOldY = 0;
			for (int j = 0; j <= i; j++) {
				float y = getYpoint(beating.get(j), 317);
				if (j == 0) {
					canvas.drawPoint(bOldX, y, paint);
				} else {
					float tmpX = bOldX + 10;
					canvas.drawLine(bOldX, bOldY, tmpX, y, paint);
					bOldX = tmpX;
				}
				bOldY = y;
			}
			try {
				saveBitmap(i, mergeBitmap(vbg, bitmap));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//生成缩略图
			if (i == (size - 1)) {
				try {
					Bitmap cbg = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_report_bg, options);
					Bitmap thumb = mergeBitmap(cbg, bitmap);
					File file = new File(chart);
					file.createNewFile();
					FileOutputStream out = null;
					out = new FileOutputStream(file);
					thumb.compress(Bitmap.CompressFormat.PNG, 100, out);
					out.flush();
					out.close();
					out = null;
					file = null;
					thumb = null;
					cbg = null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			bitmap = null;
			canvas = null;
		}
		vbg = null;
	}
	
	/**
	 * 合并图片
	 * @param first
	 * @param second
	 * @return
	 */
	public Bitmap mergeBitmap(Bitmap first, Bitmap second) {
		float left = (first.getWidth() - second.getWidth()) / 2;
		Bitmap bitmap = Bitmap.createBitmap(first.getWidth(), first.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawBitmap(first, new Matrix(), null);
		canvas.drawBitmap(second, left, 0, null);
		return bitmap;
	}
	
	/**
	 * 保存图片
	 * @param bitmap
	 * @throws IOException
	 */
	public void saveBitmap(long number, Bitmap bitmap) throws IOException {
		File file = new File("/sdcard/Fetal/tmp/" + number + ".jpg");
		file.createNewFile();
		FileOutputStream out = null;
		out = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
		out.flush();
		out.close();
		out = null;
		file = null;
		bitmap = null;
	}
	
	public void release() {
		holder.getSurface().release();
	}
}
