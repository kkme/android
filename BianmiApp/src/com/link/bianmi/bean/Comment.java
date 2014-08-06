package com.link.bianmi.bean;

import java.io.Serializable;

/**
 * 一条评论
 * 
 * @author pangfq
 * @date 2014年8月4日 下午5:13:11
 */
public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String resourceId = "";
	private String avatarImageUrl = "";// 头像图片url
	private String content = "";// 内容
	private int likeCount;// 点赞数
	private boolean isLiked = false;// 是否已赞
	private String audioUrl = "";// 音频url
	private int audioLength = 0;// 语音长度
	private long createdAt = 0;// 创建时间

	public String getAvatarImageUrl() {
		return avatarImageUrl;
	}

	public void setAvatarImageUrl(String avatarImageUrl) {
		this.avatarImageUrl = avatarImageUrl;
	}

	public int getAudioLength() {
		return audioLength;
	}

	public void setAudioLength(int audioLength) {
		this.audioLength = audioLength;
	}

	public boolean isLiked() {
		return isLiked;
	}

	public void setLiked(boolean isLiked) {
		this.isLiked = isLiked;
	}

	public long getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(long createdAt) {
		this.createdAt = createdAt;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
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

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}

	public String getAudioUrl() {
		return audioUrl;
	}

	public void setAudioUrl(String audioUrl) {
		this.audioUrl = audioUrl;
	}

}
