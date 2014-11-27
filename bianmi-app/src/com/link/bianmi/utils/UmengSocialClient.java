package com.link.bianmi.utils;

import android.app.Activity;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

public class UmengSocialClient {
	public static final String DESCRIPTOR = "com.umeng.share";

	private static final String TIPS = "请移步官方网站 ";
	private static final String END_TIPS = ", 查看相关说明.";
	public static final String TENCENT_OPEN_URL = TIPS
			+ "http://wiki.connect.qq.com/android_sdk使用说明" + END_TIPS;
	public static final String PERMISSION_URL = TIPS
			+ "http://wiki.connect.qq.com/openapi权限申请" + END_TIPS;

	public static final String SOCIAL_LINK = "http://www.umeng.com/social";
	public static final String SOCIAL_TITLE = "友盟社会化组件帮助应用快速整合分享功能";
	public static final String SOCIAL_IMAGE = "http://www.umeng.com/images/pic/banner_module_social.png";

	public static final String SOCIAL_CONTENT = "友盟社会化组件（SDK）让移动应用快速整合社交分享功能，我们简化了社交平台的接入，为开发者提供坚实的基础服务：（一）支持各大主流社交平台，"
			+ "（二）支持图片、文字、gif动图、音频、视频；@好友，关注官方微博等功能"
			+ "（三）提供详尽的后台用户社交行为分析。http://www.umeng.com/social";

	private final static UMSocialService mController = UMServiceFactory
			.getUMSocialService(DESCRIPTOR);
	private SHARE_MEDIA mPlatform = SHARE_MEDIA.SINA;

	private static Activity mActivity;

	public static void showShareDialog(Activity activity) {
		mActivity = activity;
		configPlatforms();
		mController.getConfig().setPlatforms(SHARE_MEDIA.WEIXIN,
				SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA);
		mController.openShare(activity, false);
	}

	/**
	 * 配置分享平台参数</br>
	 */
	private static void configPlatforms() {
		// 添加新浪SSO授权
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
		// 添加腾讯微博SSO授权
		// mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
		// // 添加人人网SSO授权
		// RenrenSsoHandler renrenSsoHandler = new RenrenSsoHandler(mActivity,
		// "201874", "28401c0964f04a72a14c812d6132fcef",
		// "3bf66e42db1e4fa9829b955cc300b737");
		// mController.getConfig().setSsoHandler(renrenSsoHandler);

		// 添加QQ、QZone平台
		addQQQZonePlatform();

		// 添加微信、微信朋友圈平台
		addWXPlatform();
	}

	/**
	 * @功能描述 : 添加微信平台分享
	 * @return
	 */
	private static void addWXPlatform() {
		// 注意：在微信授权的时候，必须传递appSecret
		// wx967daebe835fbeac是你在微信开发平台注册应用的AppID, 这里需要替换成你注册的AppID
		String appId = "wx614cc943a3fafcb5";
		String appSecret = "8828223ec371b21a8e74412892e61d75";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(mActivity, appId, appSecret);
		wxHandler.addToSocialSDK();
		// 设置微信好友分享内容
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		// 设置分享文字
		weixinContent.setShareContent("并不是所有的秘密都叫变秘！");
		// 设置title
		weixinContent.setTitle("点我，点我，点我");
		// 设置分享内容跳转URL
		weixinContent.setTargetUrl("http://www.baidu.com");
		// 设置分享图片
		// weixinContent.setShareImage(localImage);
		mController.setShareMedia(weixinContent);
		// 设置微信朋友圈分享内容
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent("并不是所有的秘密都叫变秘！");
		// 设置朋友圈title
		circleMedia.setTitle("点我，点我，点我");
		// circleMedia.setShareImage(localImage);
		circleMedia.setTargetUrl("http://www.baidu.com");
		mController.setShareMedia(circleMedia);
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(mActivity, appId,
				appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
	}

	/**
	 * @功能描述 : 添加QQ平台支持 QQ分享的内容， 包含四种类型， 即单纯的文字、图片、音乐、视频. 参数说明 : title, summary,
	 *       image url中必须至少设置一个, targetUrl必须设置,网页地址必须以"http://"开头 . title :
	 *       要分享标题 summary : 要分享的文字概述 image url : 图片地址 [以上三个参数至少填写一个] targetUrl
	 *       : 用户点击该分享时跳转到的目标地址 [必填] ( 若不填写则默认设置为友盟主页 )
	 * @return
	 */
	private static void addQQQZonePlatform() {
		String appId = "1103523557";
		String appKey = "7SxettN2m8FA2beG";
		// 添加QQ支持, 并且设置QQ分享内容的target url
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mActivity, appId,
				appKey);
		qqSsoHandler.setTargetUrl("http://www.umeng.com");
		qqSsoHandler.addToSocialSDK();

		// 添加QZone平台
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(mActivity, appId,
				appKey);
		qZoneSsoHandler.addToSocialSDK();
	}
}