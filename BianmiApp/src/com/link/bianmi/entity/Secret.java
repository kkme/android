package com.link.bianmi.entity;

import java.io.Serializable;

public class Secret implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String resourceId = "";
	private String wherefrom = "";// 1、朋友：朋友，朋友的朋友 2、热门：地点 3、附近：距离
	private String content = "";// 内容
	private int likeCount;// 点赞数
	private int replyCount;// 回复数
	private String audioUrl = "";// 语音url
	private int audioLength;// 语音长度
	private String imageUrl = "";// 图片url
	private long createdAt = 0;// 创建时间
	private long repliedAt = 0;// 回复时间

	public int getAudioLength() {
		return audioLength;
	}

	public void setAudioLength(int audioLength) {
		this.audioLength = audioLength;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public long getRepliedAt() {
		return repliedAt;
	}

	public void setRepliedAt(long repliedAt) {
		this.repliedAt = repliedAt;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public String getWherefrom() {
		return wherefrom;
	}

	public void setWherefrom(String wherefrom) {
		this.wherefrom = wherefrom;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public int getReplyCount() {
		return replyCount;
	}

	public void setReplyCount(int replyCount) {
		this.replyCount = replyCount;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

}
