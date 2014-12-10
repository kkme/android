package com.link.bianmi.entity;

import java.io.Serializable;

public class Secret implements Serializable {

	private static final long serialVersionUID = -8612795583682429650L;

	public String id;
	public String resourceId = "";
	public String userId = "";
	public String from = "";// 1、朋友：朋友，朋友的朋友 2、热门：地点 3、附近：距离
	public String content = "";// 内容
	public int likes;// 点赞数
	public boolean isLiked = false;// 是否已赞
	public int comments;// 评论数
	public String audioUrl = "";// 语音url
	public int audioLength;// 语音长度
	public String imageUrl = "";// 图片url
	public long createdTime = 0;// 创建时间
	public long repliedTime = 0;// 回复时间
	/** 发表秘密时，需要定位 **/
	public double longitude;// 经度
	public double latitude;// 纬度
	public int type;// 类型：1--->热门 2--->朋友圈 3--->附近
	public String city;// 城市

}
