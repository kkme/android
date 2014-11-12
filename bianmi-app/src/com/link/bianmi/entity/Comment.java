package com.link.bianmi.entity;

import java.io.Serializable;

/**
 * 一条评论
 * 
 * @author pangfq
 * @date 2014年8月4日 下午5:13:11
 */
public class Comment implements Serializable {

	public static final long serialVersionUID = 1L;

	public String id;
	public String resourceId = "";// 唯一标识一条评论
	public String secretid = "";// 评论所属的秘密ID
	public String userid = "";// 评论所属的用户ID
	public String avatarUrl = "";// 头像url
	public String content = "";// 内容
	public int likes;// 点赞数
	public boolean isLiked = false;// 是否已赞
	public String audioUrl = "";// 音频url
	public int audioLength = 0;// 语音长度
	public long createdTime = 0;// 创建时间

}
