package com.link.bianmi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.link.bianmi.R;

/**
 * 无数据
 * 
 * @author pangfq
 * @date 2014年11月26日 下午3:48:23
 */
public class NoDataView extends RelativeLayout {

	private TextView mTipText;// 无数据

	private Context mContext;

	private String mTip;

	public NoDataView(Context context) {

		this(context, null);

	}

	public NoDataView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		View view = LayoutInflater.from(context).inflate(R.layout.no_data_view,
				this, true);

		mTipText = (TextView) view.findViewById(R.id.tip_textview);
		mTipText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null)
					mListener.onClick();
			}
		});
	}

	// ---------------------Public------------------------------

	/**
	 * 显示无数据
	 */
	public void show(String tip) {
		if (mTipText != null && tip != null) {
			this.setVisibility(View.VISIBLE);
			mTipText.setVisibility(View.VISIBLE);
			mTipText.setText(tip);
		}
	}

	/**
	 * 显示无数据
	 */
	public void show(int tipResId) {
		show(mContext.getString(tipResId));
	}

	public void show() {
		if (mTip != null && !mTip.isEmpty()) {
			mTipText.setVisibility(View.VISIBLE);
			mTipText.setText(mTip);

		}
	}

	public void setTip(String tip) {
		mTip = tip;
	}

	public void setTip(int tipResId) {
		setTip(mContext.getString(tipResId));
	}

	/**
	 * 消失
	 */
	public void dismiss() {
		this.setVisibility(View.GONE);
	}

	private OnListener mListener;

	public interface OnListener {
		void onClick();
	}

	public void setOnListener(OnListener listener) {
		mListener = listener;
	}
}