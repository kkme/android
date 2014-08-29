package com.link.bianmi.fragment;

import java.io.File;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
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
import android.widget.Toast;

import com.link.bianmi.R;
import com.link.bianmi.activity.MainActivity;
import com.link.bianmi.entity.Secret;
import com.link.bianmi.utility.BitmapUtil;
import com.link.bianmi.utility.FileUtil;
import com.link.bianmi.utility.SystemBarTintUtil;
import com.link.bianmi.utility.TextUtil;
import com.link.bianmi.widget.ProgressWheel;
import com.link.bianmi.widget.ScaleImageView;
import com.link.bianmi.widget.ScaleImageView.ImageViewListener;
import com.link.bianmi.widget.blur.BlurView;
import com.link.bianmi.widget.imageviewex.ImageViewEx;

public class ImageFragment extends Fragment {

	private ScaleImageView mScaleImageView;
	private RelativeLayout rootView;
	private Secret mCurrentFeedItem;
	private BlurView blurView;

	private ProgressWheel mProgressWheel;
	private ImageViewEx mImageViewEx;
	private TextView mImageTitleTextView;
	private ObjectAnimator fadeInAnimator, fadeOutAnimator;
	private boolean isClose;

	public void setmCurrentFeedItem(Secret mCurrentFeedItem) {
		this.mCurrentFeedItem = mCurrentFeedItem;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		rootView = (RelativeLayout) inflater.inflate(R.layout.fragment_image,
				null);
		rootView.setVisibility(View.INVISIBLE);
		mImageTitleTextView = (TextView) rootView
				.findViewById(R.id.fragment_image_title_textview);
		((View) mImageTitleTextView.getParent()).setAlpha(0);
		mProgressWheel = (ProgressWheel) rootView
				.findViewById(R.id.fragment_image_progresswheel);
		mImageViewEx = (ImageViewEx) rootView
				.findViewById(R.id.fragment_image_imageViewex);
		mImageViewEx.setFillDirection(ImageViewEx.FillDirection.HORIZONTAL);
		blurView = (BlurView) rootView
				.findViewById(R.id.fragment_image_blurview);

		this.mScaleImageView = (ScaleImageView) rootView
				.findViewById(R.id.fragment_image_scaleimageview);
		mScaleImageView.setBlurView(blurView);
		SystemBarTintUtil manager = new SystemBarTintUtil(getActivity());
		View view = (View) mScaleImageView.getParent();
		view.setPadding(0, manager.getConfig().getPixelInsetTop(true), 0, 0);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		isClose = false;
		// mTranslateManager = new MxxTranslateManager(getActivity());
		// LayoutTransition transition =new LayoutTransition();
		fadeInAnimator = ObjectAnimator.ofFloat(
				((View) mImageTitleTextView.getParent()), "alpha", 0f, 1f);
		// fadeInAnimator=ObjectAnimator.ofFloat(mImageTitleTextView,
		// "translationY", -mImageTitleTextView.getHeight(), 0f);
		fadeInAnimator.setDuration(ScaleImageView.anim_duration / 2);
		fadeInAnimator.setStartDelay(ScaleImageView.anim_duration / 2);
		fadeInAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {
				// mTranslateManager.translateNoDialog(mCurrentFeedItem.getCaption(),
				// new MxxTranslateManager.TranslateListener() {
				//
				// @Override
				// public void onSuccess(String content, String result) {
				// mImageTitleTextView.setText(content + "\n" + result);
				// }
				//
				// @Override
				// public void onError() {
				// MxxToastUtil.showToast(getActivity(), "Translate error.");
				// }
				// });
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

		// fadeOutAnimator.addListener(new AnimatorListener() {
		//
		// @Override
		// public void onAnimationStart(Animator animation) {}
		// @Override
		// public void onAnimationRepeat(Animator animation) {}
		//
		// @Override
		// public void onAnimationEnd(Animator animation) {
		// mImageTitleTextView.setVisibility(View.GONE);
		// }
		//
		// @Override
		// public void onAnimationCancel(Animator animation) {}
		// });
		// transition.setAnimator(LayoutTransition.APPEARING, fadeInAnimator );
		// transition.setAnimator(LayoutTransition.DISAPPEARING,
		// fadeOutAnimator);
		// ((ViewGroup)mImageTitleTextView.getParent()).setLayoutTransition(transition);
		mScaleImageView.setImageViewListener(new ImageViewListener() {

			@Override
			public void onSingleTap() {
				// TODO Auto-generated method stub
				// mScaleImageView.resetScale();
				isClose = true;
				mScaleImageView.startCloseScaleAnimation();
				fadeOutAnimator.start();
			}

			@Override
			public void onScaleEnd() {
				// TODO Auto-generated method stub
				if (isClose) {
					mScaleImageView.setImageDrawable(null);
					rootView.setVisibility(View.GONE);
					((MainActivity) getActivity()).showImageFragment(null,
							false, null);
					getActivity().supportInvalidateOptionsMenu();
					isClose = false;
				} else {
					mScaleImageView.setTopCrop(false);
					mScaleImageView.initAttacher();
					// checkGif();
				}
			}
		});
	}

	public void startScaleAnimation(ImageView smallImageView, Secret feedItem) {
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
		if (item.getItemId() == R.id.fragment_image_action_share_all) {
			return true;
		} else if (item.getItemId() == R.id.fragment_image_action_share_image) {
			Bitmap bitmap = BitmapUtil.drawableToBitmap(mScaleImageView
					.getDrawable());
			String chooserDialogTitleString = getResources().getString(
					R.string.add_action_title);
			return true;
		} else if (item.getItemId() == R.id.fragment_image_action_share_text) {
			return true;
		} else if (item.getItemId() == R.id.fragment_image_action_copy) {
			TextUtil.copyTextViewString(mImageTitleTextView);
			return true;
		} else if (item.getItemId() == R.id.fragment_image_action_save) {
			Bitmap bitmap = BitmapUtil.drawableToBitmap(mScaleImageView
					.getDrawable());
			String path = FileUtil.getImagePath() + "/"
					+ mCurrentFeedItem.resourceId + ".jpg";
			if (new File(path).exists()) {
				Toast.makeText(getActivity(),
						"This image has already been saved.",
						Toast.LENGTH_SHORT).show();
			} else {
			}
			return true;
		} else if (item.getItemId() == R.id.fragment_image_action_translate) {
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

	public void checkGif() {
		mProgressWheel.setVisibility(View.VISIBLE);
		mProgressWheel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
	}
}
