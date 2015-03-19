package com.fetal.util;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout.LayoutParams;

public class SimulatePainter {
	
	private int[][] dots = {
		{0, 3}, {10, 6}, {20, 9}, {30, 12}, {40, 15}, {50, 19}, {60, 22}, {70, 26}, {80, 30}, {90, 33},
		{101, 37}, {111, 39}, {121, 41}, {132, 43}, {143, 43}, {154, 42}, {164, 41}, {175, 39}, {185, 36}, {195, 33},
		{195, 33}, {206, 28}, {215, 23}, {224, 18}, {233, 12}, {243, 5}, {251, 1}, {252, 5}, {245, 13}, {239, 21}, 
		{232, 31}, {227, 40}, {221, 49}, {215, 58}, {210, 67}, {206, 77}, {201, 86}, {198, 96}, {195, 108}, {194, 118}, 
		{194, 129}, {196, 139}, {198, 150}, {203, 161}, {209, 169}, {216, 177}, {224, 184}, {234, 190}, {244, 194}, {254, 197},
		{265, 197}, {276, 196}, {287, 192}, {296, 188}, {305, 180}, {311, 172}, {317, 164}, {323, 167}, {329, 176}, {337, 184},
		{345, 190}, {356, 194}, {367, 196}, {377, 197}, {388, 197}, {399, 194}, {409, 190}, {418, 184}, {425, 176}, {432, 168},
		{436, 157}, {440, 147}, {442, 137}, {442, 125}, {443, 115}, {442, 103}, {438, 94}, {433, 84}, {429, 74}, {425, 65}, {419, 54},
		{413, 46}, {407, 37}, {401, 28}, {395, 20}, {388, 11}, {382, 4}, {383, 1}, {393, 5}, {402, 11}, {411, 17}, {418, 22}, {429, 27},
		{439, 31}, {449, 35}, {460, 37}, {471, 39}, {481, 41}, {492, 41}, {503, 41}, {513, 40}, {524, 37}, {534, 35}, {544, 29}, {554, 25},
		{563, 22}, {572, 17}, {585, 13}, {595, 10}, {604, 7}, {614, 5}, {625, 3}, {635, 2}
	};
	
	private int[][] points = {
			{0, 30}, {70, 9}, {195, 71}, {245, 49}, {275, 72}, {323, 9}, {375, 101}, {505, 11}, {640, 42}
	};
	
	private List<Integer> points_x, points_y;
	private List<Float> curve_x, curve_y;
	private List<Cubic> cubics, calculate_x, calculate_y;
	private int foot = 1, head = 1;
	private boolean after = false;
	private Context context;
	//获取画布对象
	private SurfaceView surface;
	//获取画布句柄
	private SurfaceHolder holder;
	//画笔
	private Paint paint;
	private int width, height;
	private Canvas canvas;
	
	public SimulatePainter(Context context, SurfaceView surface) {
		// TODO Auto-generated constructor stub
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
		paint.setAntiAlias(true);
		paint.setColor(Color.rgb(254, 144, 169));
        paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(4);
		LayoutParams params = (LayoutParams) surface.getLayoutParams();
		width = params.width;
		height = params.height;
	}
	
	/**
	 * 初始化曲线点
	 */
	public void initCurve() {
		points_x = new LinkedList<Integer>();
		points_y = new LinkedList<Integer>();
		curve_x = new LinkedList<Float>();
		curve_y = new LinkedList<Float>();
		for (int i = 0; i < points.length; i++) {
			points_x.add(points[i][0]);
			points_y.add(points[i][1]);
		}
		calculate_x = calculate(points_x);
		calculate_y = calculate(points_y);
		for (int i = 0; i < calculate_x.size(); i++) {
			for (int j = 1; j <= 15; j++) {
				float u = j / (float) 15;
				curve_x.add(width * calculate_x.get(i).eval(u) / 640);
				curve_y.add(height - height * calculate_y.get(i).eval(u) / 104);
			}
		}
	}
	
	/**
	 * 计算曲线.
	 * 
	 * @param x
	 * @return
	 */
	private List<Cubic> calculate(List<Integer> x) {
		int n = x.size() - 1;
		float[] gamma = new float[n + 1];
		float[] delta = new float[n + 1];
		float[] D = new float[n + 1];
		int i;
		gamma[0] = 1.0f / 2.0f;
		for (i = 1; i < n; i++) {
			gamma[i] = 1 / (4 - gamma[i - 1]);
		}
		gamma[n] = 1 / (2 - gamma[n - 1]);

		delta[0] = 3 * (x.get(1) - x.get(0)) * gamma[0];
		for (i = 1; i < n; i++) {
			delta[i] = (3 * (x.get(i + 1) - x.get(i - 1)) - delta[i - 1])
					* gamma[i];
		}
		delta[n] = (3 * (x.get(n) - x.get(n - 1)) - delta[n - 1]) * gamma[n];

		D[n] = delta[n];
		for (i = n - 1; i >= 0; i--) {
			D[i] = delta[i] - gamma[i] * D[i + 1];
		}

		/* now compute the coefficients of the cubics */
		cubics = new LinkedList<Cubic>();
		for (i = 0; i < n; i++) {
			Cubic c = new Cubic(x.get(i), D[i], 3 * (x.get(i + 1) - x.get(i))
					- 2 * D[i] - D[i + 1], 2 * (x.get(i) - x.get(i + 1)) + D[i]
					+ D[i + 1]);
			cubics.add(c);
		}
		return cubics;
	}

	/**
	 * 画曲线.
	 * 
	 * @param canvas
	 */
	public void drawCurve() {
		canvas = holder.lockCanvas(
				new Rect(0, 0, width, height)
			);
		canvas.drawColor(Color.BLACK, Mode.CLEAR);
		Path path = new Path();
		int size = curve_x.size();
		float space = size / 2;
		for (int i = 0; i < size; i++) {
			float x = curve_x.get(i);
			float y = curve_y.get(i);
			if (after) {
//				if (head > space) {
//					if (i == 0) {
//						path.moveTo(x, y);
//					} else if (head - i > space) {
//						path.lineTo(x, y);
//					}
//					canvas.drawPath(path, paint);
//				}
				if (i >= head) {
					if (i == head) {
						path.moveTo(x, y);
					} else {
						path.lineTo(x, y);
					}
					canvas.drawPath(path, paint);
				}
			} else {
				if (i == foot) {
					foot += 5;
					if (foot > size) {
						foot = 1;
						after = true;
					}
					break;
				}
				if (i == 0) {
					path.moveTo(x, y);
				} else {
					path.lineTo(x, y);
				}
				canvas.drawPath(path, paint);
			}
		}
		if (after) {
			head += 5;
			if (head >= size) {
				head = 1;
				after = false;
			}
		}
		holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
		canvas = null;
	}
	
	/**
	 * 绘制指定区域
	 */
	public void drawPoint() {
		canvas = holder.lockCanvas(
			new Rect(0, 0, width, height)
		);
		canvas.drawColor(Color.BLACK, Mode.CLEAR);
		for (int i = 0; i < foot; i++) {
			float x = width * dots[i][0] / 640;
			float y = height - height * dots[i][1] / 201;
			canvas.drawPoint(x, y, paint);
		}
		holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
		canvas = null;
		foot ++;
		if (foot > dots.length) {
			foot = 1;
		}
	}
	
	/**
	 * 清空绘制区域
	 */
	public void clean() {
		canvas = holder.lockCanvas(
			new Rect(0, 0, width, height)
		);
		canvas.drawColor(Color.BLACK, Mode.CLEAR);
		holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
		canvas = null;
		foot = 1;
	}
}

class Cubic {

  float a,b,c,d;         /* a + b*u + c*u^2 +d*u^3 */

  public Cubic(float a, float b, float c, float d){
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }
  
  /** evaluate cubic */
  public float eval(float u) {
    return (((d*u) + c)*u + b)*u + a;
  }
}
