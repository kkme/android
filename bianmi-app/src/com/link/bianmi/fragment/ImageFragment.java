package com.link.bianmi.fragment;

import lib.widget.imageviewex.ImageViewEx;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.link.bianmi.R;
import com.link.bianmi.activity.HomeActivity;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.widget.ScaleImageView;
import com.link.bianmi.widget.ScaleImageView.ImageViewListener;
import com.link.bianmi.widget.BlurView;

public class ImageFragment extends Fragment {

	private ScaleImageView mScaleImageView;
	private RelativeLayout rootView;
	private Secret mCurrentFeedItem;
	private BlurView blurView;

	private ImageViewEx mImageViewEx;
	private TextView mImageTitleTextView;
	private ObjectAnimator fadeInAnimator, fadeOutAnimator;
	private boolean isClose;

	public void setmCurrentFeedItem(Secret mCurrentFeedItem) {
		this.mCurrentFeedItem = mCurrentFeedItem;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_image,
				null);
		rootView.setVisibility(View.INVISIBLE);
		mImageTitleTextView = (TextView) rootView
				.findViewById(R.id.fragment_image_title_textview);
		((View) mImageTitleTextView.getParent()).setAlpha(0);
		mImageViewEx = (ImageViewEx) rootView
				.findViewById(R.id.fragment_image_imageViewex);
		mImageViewEx.setFillDirection(ImageViewEx.FillDirection.HORIZONTAL);
		blurView = (BlurView) rootView
				.findViewById(R.id.fragment_image_blurview);

		this.mScaleImageView = (ScaleImageView) rootView
				.findViewById(R.id.fragment_image_scaleimageview);
		mScaleImageView.setBlurView(blurView);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		isClose = false;
		fadeInAnimator = ObjectAnimator.ofFloat(
				((View) mImageTitleTextView.getParent()), "alpha", 0f, 1f);
		fadeInAnimator.setDuration(ScaleImageView.anim_duration / 2);
		fadeInAnimator.setStartDelay(ScaleImageView.anim_duration / 2);
		fadeInAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
			}

			@Override
			public void onAnimationRepeat(Animator animation) {
			}

			@Override
			public void onAnimationEnd(Animator animation) {

			}

			@Override
			public void onAnimationCancel(Animator animation) {
			}
		});
		fadeOutAnimator = ObjectAnimator.ofFloat(
				((View) mImageTitleTextView.getParent()), "alpha", 1f, 0f);
		fadeOutAnimator.setDuration(ScaleImageView.anim_duration / 2);

		mScaleImageView.setImageViewListener(new ImageViewListener() {

			@Override
			public void onSingleTap() {
				isClose = true;
				mScaleImageView.startCloseScaleAnimation();
				fadeOutAnimator.start();
			}

			@Override
			public void onScaleEnd() {
				if (isClose) {
					mScaleImageView.setImageDrawable(null);
					rootView.setVisibility(View.GONE);
					((HomeActivity) getActivity()).showImageFragment(null,
							false, null);
					getActivity().supportInvalidateOptionsMenu();
					isClose = false;
				} else {
					mScaleImageView.setTopCrop(false);
					mScaleImageView.initAttacher();
				}
			}
		});
	}

	public void startScaleAnimation(ImageView smallImageView, Secret feedItem) {
		mImageTitleTextView.setText(feedItem.content);
		rootView.setVisibility(View.VISIBLE);
		fadeInAnimator.start();
		blurView.drawBlurOnce();
		mScaleImageView.startScaleAnimation(smallImageView);
		getActivity().supportInvalidateOptionsMenu();
		mCurrentFeedItem = feedItem;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (menu.findItem(R.id.fragment_image_action_weblink) != null
				&& rootView != null) {
			menu.findItem(R.id.fragment_image_action_weblink).setVisible(
					rootView.getVisibility() == View.VISIBLE);
			menu.findItem(R.id.fragment_image_action_share).setVisible(
					rootView.getVisibility() == View.VISIBLE);
			menu.findItem(R.id.fragment_image_action_save).setVisible(
					rootView.getVisibility() == View.VISIBLE);
		}
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.image, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.fragment_image_action_weblink) {
			if (mCurrentFeedItem != null) {
			}
			return true;
		} else if (item.getItemId() == R.id.fragment_image_action_save) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean canBack() {
		return rootView.getVisibility() == View.VISIBLE;
	}

	public void goBack() {
		if (!isClose) {
			isClose = true;
			mScaleImageView.startCloseScaleAnimation();
		}
	}
}