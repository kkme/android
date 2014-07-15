package com.link.bianmi.bean;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Id;

import org.json.JSONException;
import org.json.JSONObject;

public class Secret implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	private String caption;
	private String images_small;
	private String images_normal;
	private String images_large;
	private String link;
	private String next;
	private String resourceId;
	private String where;// 1、朋友：朋友，朋友的朋友 2、热门：地点 3、附近：距离
	private String content;// 内容
	private int likeCount;// 点赞数
	private int replyCount;// 回复数
	private String audioUrl;// 音频url
	private String imageUrl;// 图片url

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

	public Secret(JSONObject jsonObject) {
		try {
			this.id = jsonObject.getString("id");
			this.link = jsonObject.getString("link");
			this.caption = jsonObject.getString("caption");
			// this.likeCount = jsonObject.getJSONObject("votes").getString(
			// "count");
			// this.replyCount = jsonObject.getJSONObject("votes").getString(
			// "count");
			JSONObject imageJsonObject = jsonObject.getJSONObject("images");
			this.images_small = imageJsonObject.getString("small");
			this.images_normal = imageJsonObject.getString("normal");
			this.images_large = imageJsonObject.getString("large");

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Secret() {
	}

	public String getId() {
		return id;
	}

	public String getCaption() {
		return caption;
	}

	public String getImages_small() {
		return images_small;
	}

	public String getImages_normal() {
		return images_normal;
	}

	public String getImages_large() {
		return images_large;
	}

	public String getLink() {
		return link;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public void setImages_small(String images_small) {
		this.images_small = images_small;
	}

	public void setImages_normal(String images_normal) {
		this.images_normal = images_normal;
	}

	public void setImages_large(String images_large) {
		this.images_large = images_large;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
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
