package com.link.bianmi.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GestureLockView extends View {

	public static final int SETPASS_RESULT_RECORDED = 1;// 图案已记录，请再次绘制确认
	public static final int SETPASS_RESULT_DIFFERENT = 2;// 与刚才绘制的图案不同
	public static final int SETPASS_RESULT_OK = 3;// 密码设置成功

	public static final int CHECKPASS_PASSWORD_OK = 11;// 密码匹配成功
	public static final int CHECKPASS_PASSWORD_ERROR = 22;// 密码匹配失败

	public enum LockType {

		CheckPass, // 验证密码
		SetPass // 设置密码

	}

	private LockType mLockType;

	private Paint paintNormal;
	private Paint paintOnTouch;
	private Paint paintInnerCycle;
	private Paint paintLines;
	private Paint paintKeyError;
	private MyCycle[] cycles;
	private Path linePath = new Path();
	private List<Integer> linedCycles = new ArrayList<Integer>();
	private OnGestureFinishListener onGestureFinishListener;
	private String key;
	private int eventX, eventY;
	private boolean canContinue = true;
	private Timer timer;
	private boolean result;

	private int OUT_CYCLE_NORMAL = Color.rgb(108, 119, 138); // ������Բ��ɫ
	private int OUT_CYCLE_ONTOUCH = Color.rgb(025, 066, 103); // ѡ����Բ��ɫ
	private int INNER_CYCLE_ONTOUCH = Color.rgb(002, 210, 255); // ѡ����Բ��ɫ
	private int LINE_COLOR = Color.argb(127, 002, 210, 255); // ��������ɫ
	private int ERROR_COLOR = Color.argb(127, 255, 000, 000); // ���Ӵ�����Ŀ��ʾ��ɫ

	public void setOnGestureFinishListener(
			OnGestureFinishListener onGestureFinishListener) {
		this.onGestureFinishListener = onGestureFinishListener;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}

	public interface OnGestureFinishListener {
		public void onGestureFinish(int resultCode);

		public void onGestureStart();

	}

	public GestureLockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public GestureLockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public GestureLockView(Context context) {
		super(context);
		init();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int perSize = 0;
		if (cycles == null && (perSize = getWidth() / 6) > 0) {
			cycles = new MyCycle[9];
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					MyCycle cycle = new MyCycle();
					cycle.setNum(i * 3 + j);
					cycle.setOx(perSize * (j * 2 + 1));
					cycle.setOy(perSize * (i * 2 + 1));
					cycle.setR(perSize * 0.5f);
					cycles[i * 3 + j] = cycle;
				}
			}
		}
	}

	private void init() {
		paintNormal = new Paint();
		paintNormal.setAntiAlias(true);
		paintNormal.setStrokeWidth(3);
		paintNormal.setStyle(Paint.Style.STROKE);

		paintOnTouch = new Paint();
		paintOnTouch.setAntiAlias(true);
		paintOnTouch.setStrokeWidth(3);
		paintOnTouch.setStyle(Paint.Style.STROKE);

		paintInnerCycle = new Paint();
		paintInnerCycle.setAntiAlias(true);
		paintInnerCycle.setStyle(Paint.Style.FILL);

		paintLines = new Paint();
		paintLines.setAntiAlias(true);
		paintLines.setStyle(Paint.Style.STROKE);
		paintLines.setStrokeWidth(6);

		paintKeyError = new Paint();
		paintKeyError.setAntiAlias(true);
		paintKeyError.setStyle(Paint.Style.STROKE);
		paintKeyError.setStrokeWidth(3);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (int i = 0; i < cycles.length; i++) {
			if (!canContinue && !result) {
				paintOnTouch.setColor(ERROR_COLOR);
				paintInnerCycle.setColor(ERROR_COLOR);
				paintLines.setColor(ERROR_COLOR);
			} else if (cycles[i].isOnTouch()) {
				paintOnTouch.setColor(OUT_CYCLE_ONTOUCH);
				paintInnerCycle.setColor(INNER_CYCLE_ONTOUCH);
				paintLines.setColor(LINE_COLOR);
			} else {
				paintNormal.setColor(OUT_CYCLE_NORMAL);
				paintInnerCycle.setColor(INNER_CYCLE_ONTOUCH);
				paintLines.setColor(LINE_COLOR);
			}
			if (cycles[i].isOnTouch()) {
				canvas.drawCircle(cycles[i].getOx(), cycles[i].getOy(),
						cycles[i].getR(), paintOnTouch);
				// drawInnerBlueCycle
				drawInnerBlueCycle(cycles[i], canvas);
			} else {
				canvas.drawCircle(cycles[i].getOx(), cycles[i].getOy(),
						cycles[i].getR(), paintNormal);
			}
		}
		// drawLine
		drawLine(canvas);
	}

	private void drawLine(Canvas canvas) {
		linePath.reset();
		if (linedCycles.size() > 0) {
			for (int i = 0; i < linedCycles.size(); i++) {
				int index = linedCycles.get(i);
				if (i == 0) {
					linePath.moveTo(cycles[index].getOx(),
							cycles[index].getOy());
				} else {
					linePath.lineTo(cycles[index].getOx(),
							cycles[index].getOy());
				}
			}
			linePath.lineTo(eventX, eventY);
			canvas.drawPath(linePath, paintLines);
		}
	}

	private void drawInnerBlueCycle(MyCycle myCycle, Canvas canvas) {
		canvas.drawCircle(myCycle.getOx(), myCycle.getOy(), myCycle.getR() / 3,
				paintInnerCycle);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (canContinue) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (onGestureFinishListener != null) {
					onGestureFinishListener.onGestureStart();
				}
				break;
			case MotionEvent.ACTION_MOVE: {
				eventX = (int) event.getX();
				eventY = (int) event.getY();
				for (int i = 0; i < cycles.length; i++) {
					if (cycles[i].isPointIn(eventX, eventY)) {
						cycles[i].setOnTouch(true);
						if (!linedCycles.contains(cycles[i].getNum())) {
							linedCycles.add(cycles[i].getNum());
						}
					}
				}
				break;
			}
			case MotionEvent.ACTION_UP: {
				// ��ͣ����
				canContinue = false;
				// �����
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < linedCycles.size(); i++) {
					sb.append(linedCycles.get(i));
				}

				int witch = 0;
				switch (mLockType) {
				case SetPass: {

					if (mInit) {
						result = true;
						key = sb.toString();
						mInit = false;
						witch = SETPASS_RESULT_RECORDED;
					} else {
						result = key.equals(sb.toString());
						if (!result) {
							witch = SETPASS_RESULT_DIFFERENT;
						} else {
							witch = SETPASS_RESULT_OK;
						}
					}
					break;
				}
				case CheckPass: {

					result = key.equals(sb.toString());
					if (result) {
						witch = CHECKPASS_PASSWORD_OK;
					} else {
						witch = CHECKPASS_PASSWORD_ERROR;
					}
					break;
				}
				}

				if (onGestureFinishListener != null) {
					onGestureFinishListener.onGestureFinish(witch);
				}

				timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						// ��ԭ
						eventX = eventY = 0;
						for (int i = 0; i < cycles.length; i++) {
							cycles[i].setOnTouch(false);
						}
						linedCycles.clear();
						linePath.reset();
						canContinue = true;
						postInvalidate();
					}
				}, 1000);
				break;
			}
			}
			invalidate();
		}
		return true;
	}

	private boolean mInit = false;

	public void clean() {
		mInit = true;
		mLockType = LockType.SetPass;
		key = "";
	}

	public void setLockType(LockType lockType) {
		mLockType = lockType;
	}

	class MyCycle {
		private int ox; // Բ�ĺ�����
		private int oy; // Բ��������
		private float r; // �뾶����
		private Integer num; // ������ֵ
		private boolean onTouch; // false=δѡ��

		public int getOx() {
			return ox;
		}

		public void setOx(int ox) {
			this.ox = ox;
		}

		public int getOy() {
			return oy;
		}

		public void setOy(int oy) {
			this.oy = oy;
		}

		public float getR() {
			return r;
		}

		public void setR(float r) {
			this.r = r;
		}

		public Integer getNum() {
			return num;
		}

		public void setNum(Integer num) {
			this.num = num;
		}

		public boolean isOnTouch() {
			return onTouch;
		}

		public void setOnTouch(boolean onTouch) {
			this.onTouch = onTouch;
		}

		public boolean isPointIn(int x, int y) {
			double distance = Math.sqrt((x - ox) * (x - ox) + (y - oy)
					* (y - oy));
			return distance < r;
		}
	}
}