package com.link.bianmi.fragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.link.bianmi.bean.Secret;
import com.link.bianmi.utility.BitmapUtil;
import com.link.bianmi.utility.IntentUtil;
import com.link.bianmi.utility.DialogUtil;
import com.link.bianmi.utility.FileUtil;
import com.link.bianmi.utility.TextUtil;
import com.link.bianmi.utility.SystemBarTintUtil;
import com.link.bianmi.utility.ToastUtil;
import com.link.bianmi.widget.ProgressWheel;
import com.link.bianmi.widget.ScaleImageView;
import com.link.bianmi.widget.ScaleImageView.ImageViewListener;
import com.link.bianmi.widget.blur.BlurView;
import com.link.bianmi.widget.imageviewex.Converters;
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
		mImageTitleTextView.setText(feedItem.getCaption());
		rootView.setVisibility(View.VISIBLE);
		// mImageTitleTextView.setVisibility(View.VISIBLE);
		fadeInAnimator.start();
		// rootView.setHasBlured(false);
		// rootView.postInvalidate();
		blurView.drawBlurOnce();
		mScaleImageView.startScaleAnimation(smallImageView);
		getActivity().supportInvalidateOptionsMenu();
		mCurrentFeedItem = feedItem;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		if (menu.findItem(R.id.fragment_image_action_weblink) != null
				&& rootView != null) {
			menu.findItem(R.id.fragment_image_action_weblink).setVisible(
					rootView.getVisibility() == View.VISIBLE);
			menu.findItem(R.id.fragment_image_action_share).setVisible(
					rootView.getVisibility() == View.VISIBLE);
			// menu.findItem(R.id.fragment_image_action_copy).setVisible(rootView.getVisibility()
			// == View.VISIBLE);
			menu.findItem(R.id.fragment_image_action_save).setVisible(
					rootView.getVisibility() == View.VISIBLE);
			// menu.findItem(R.id.fragment_image_action_translate).setVisible(rootView.getVisibility()
			// == View.VISIBLE);
		}
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		inflater.inflate(R.menu.image_fragment, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub]
		if (item.getItemId() == R.id.fragment_image_action_share_all) {
			Bitmap bitmap = BitmapUtil.drawableToBitmap(mScaleImageView
					.getDrawable());
			String chooserDialogTitleString = getResources().getString(
					R.string.menu_action_share);
			IntentUtil.shareBitmapWithText(getActivity(), bitmap,
					mCurrentFeedItem.getCaption(), chooserDialogTitleString);
			return true;
		} else if (item.getItemId() == R.id.fragment_image_action_share_image) {
			Bitmap bitmap = BitmapUtil.drawableToBitmap(mScaleImageView
					.getDrawable());
			String chooserDialogTitleString = getResources().getString(
					R.string.menu_action_share);
			IntentUtil.shareBitmap(getActivity(), bitmap,
					chooserDialogTitleString);
			return true;
		} else if (item.getItemId() == R.id.fragment_image_action_share_text) {
			String chooserDialogTitleString = getResources().getString(
					R.string.menu_action_share);
			IntentUtil.shareText(getActivity(), mCurrentFeedItem.getCaption(),
					chooserDialogTitleString);
			return true;
		} else if (item.getItemId() == R.id.fragment_image_action_copy) {
			TextUtil.copyTextViewString(mImageTitleTextView);
			return true;
		} else if (item.getItemId() == R.id.fragment_image_action_save) {
			Bitmap bitmap = BitmapUtil.drawableToBitmap(mScaleImageView
					.getDrawable());
			String path = FileUtil.getImagePath() + "/"
					+ mCurrentFeedItem.getId() + ".jpg";
			if (new File(path).exists()) {
				ToastUtil.showToast(getActivity(),
						"This image has already been saved.");
			} else {
				// ProgressDialog dialog = ProgressDialog.show(getActivity(),
				// null, MxxTextUtil.getTypefaceSpannableString(getActivity(),
				// "Saving...", MxxTextUtil.Roboto_Light, false));
				Dialog dialog = DialogUtil.creatPorgressDialog(
						getActivity(), null, "Saving...", false, true, null);
				new BitmapUtil.SaveBitampTask(path, dialog).execute(bitmap);
			}
			return true;
		} else if (item.getItemId() == R.id.fragment_image_action_translate) {
			// mTranslateManager.showTranslationResultInDialog(
			// mCurrentFeedItem.getCaption());
			//
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// private void showTranslationResult(String content ,String result){
	// String message = content + "\n\n" + result;
	// Dialog dialog = MxxDialogUtil.creatConfirmDialog(getActivity(),
	// "Translation result",message,
	// "OK", null, true, true, new MxxDialogUtil.MxxDialogListener() {
	// @Override
	// public void onRightBtnClick() {
	// }
	//
	// @Override
	// public void onLeftBtnClick() {
	// }
	// @Override
	// public void onCancel() {
	// }
	//
	// @Override
	// public void onListItemClick(int position, String string) {
	// }
	//
	// @Override
	// public void onListItemLongClick(int position, String string) {
	// // TODO Auto-generated method stub
	//
	// }
	// });
	// dialog.show();
	// }

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
		if (!mCurrentFeedItem.getImages_large().endsWith(".gif")) {
			return;
		}
		mProgressWheel.setVisibility(View.VISIBLE);
		mProgressWheel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				File gifFile = new File(getDownloadPath(mCurrentFeedItem
						.getImages_large()));
				if (gifFile.exists()) {
					loadGifInImageViewEx(mCurrentFeedItem.getImages_large());
				} else {
					downloadGif(mCurrentFeedItem.getImages_large());
				}

			}
		});
	}

	private void downloadGif(final String image_url) {

		FinalHttp fh = new FinalHttp();
		// ����download������ʼ����
		HttpHandler handler = fh.download(image_url, // ���������ص�·��
				getDownloadPathTmp(image_url), // ���Ǳ��浽���ص�·��
				true,// true:�ϵ����� false:���ϵ�������ȫ�����أ�
				new AjaxCallBack<File>() {
					@Override
					public void onLoading(long count, long current) {
						// textView.setText("���ؽ��ȣ�" + current + "/" + count);
						// squareProgressBar.setProgress(current * 100 / count);

						mProgressWheel
								.setProgress((int) (current * 360 / count));
					}

					@Override
					public void onSuccess(File t) {
						Toast.makeText(getActivity(), "�������",
								Toast.LENGTH_SHORT).show();
						new ReNameTask(image_url).execute(t);
					}

					@Override
					public void onFailure(Throwable t, int errorNo,
							String strMsg) {
						// TODO Auto-generated method stub
						super.onFailure(t, errorNo, strMsg);
						Toast.makeText(getActivity(),
								"����ʧ��:" + t.toString() + "\n" + strMsg,
								Toast.LENGTH_SHORT).show();
					}

				});
	}

	private class ReNameTask extends AsyncTask<File, Void, Boolean> {

		private String image_url;

		public ReNameTask(String image_url) {
			super();
			this.image_url = image_url;
		}

		@Override
		protected Boolean doInBackground(File... params) {
			// TODO Auto-generated method stub
			String oldFilePath = params[0].getAbsolutePath();
			String newFilePath = oldFilePath.substring(0,
					oldFilePath.length() - 4);
			return params[0].renameTo(new File(newFilePath));
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (result) {
				if (!canBack())
					return;
				loadGifInImageViewEx(image_url);
			}
		}
	}

	private void loadGifInImageViewEx(String image_url) {
		try {
			File f = new File(getDownloadPath(image_url));
			FileInputStream is = new FileInputStream(f);
			mImageViewEx.setVisibility(View.VISIBLE);
			mImageViewEx.setSource(Converters.inputStreamToByteArray(is,
					(int) f.length()));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Toast.makeText(getActivity(),
					"loadGifInImageViewEx FileNotFoundException!!",
					Toast.LENGTH_SHORT).show();
		}
	}

	private static String getDownloadPath(String image_url) {
		int start = image_url.lastIndexOf("/") + 1;
		return FileUtil.getDownloadPath() + "/" + image_url.substring(start);
	}

	private static String getDownloadPathTmp(String image_url) {
		int start = image_url.lastIndexOf("/") + 1;
		return FileUtil.getDownloadPath() + "/" + image_url.substring(start)
				+ ".tmp";
	}

}
