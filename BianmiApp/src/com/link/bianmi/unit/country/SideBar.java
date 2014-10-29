package com.link.bianmi.unit.country;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.utility.Tools;

public class SideBar extends View {
	public static String[] b = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
			"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
			"W", "X", "Y", "Z" };
	private int choose = -1;
	private Paint paint = new Paint();

	private TextView mTextDialog;

	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SideBar(Context context) {
		super(context);
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / b.length;
		for (int i = 0; i < b.length; i++) {
			paint.setColor(Color.parseColor("#576B95"));
			paint.setAntiAlias(true);
			paint.setTextSize(Tools.sp2px(getContext(), 14));
			if (i == choose) {
				paint.setColor(Color.parseColor("#40AA53"));
				paint.setFakeBoldText(true);
			}
			float xPos = width / 2 - paint.measureText(b[i]) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(b[i], xPos, yPos, paint);
			paint.reset();
		}
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final int c = (int) (y / getHeight() * b.length);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mOnTouchListener != null) {
				mOnTouchListener.onActionDown();
			}
			break;
		case MotionEvent.ACTION_UP:
			setBackgroundResource(R.color.transparent);
			choose = -1;
			invalidate();
			if (mTextDialog != null) {
				mTextDialog.setVisibility(View.INVISIBLE);
			}

			if (mOnTouchListener != null) {
				mOnTouchListener.onActionUp();
			}

			break;

		default:
			setBackgroundResource(R.drawable.btn_red_pressed);
			if (oldChoose != c) {
				if (c >= 0 && c < b.length) {
					if (mOnTouchListener != null) {
						mOnTouchListener.onActionMove(b[c]);
					}
					if (mTextDialog != null) {
						mTextDialog.setText(b[c]);
						mTextDialog.setVisibility(View.VISIBLE);
					}
					choose = c;
					invalidate();
				}
			}
			break;
		}
		return true;
	}

	private OnTouchListener mOnTouchListener = null;

	public void setOnTouchListener(OnTouchListener listener) {
		this.mOnTouchListener = listener;
	}

	public interface OnTouchListener {
		public void onActionDown();

		public void onActionMove(String letter);

		public void onActionUp();
	}
}