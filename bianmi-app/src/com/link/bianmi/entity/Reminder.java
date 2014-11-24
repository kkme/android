package com.link.bianmi.entity;

import java.io.Serializable;

/**
 * 提醒
 * 
 * @author pangfq
 * @date 2014-11-23 下午10:07:23
 */
public class Reminder implements Serializable {

	private static final long serialVersionUID = -1720137246341076561L;

	public boolean hasReminder = false;// 是否有提醒

	/**
	 * 我的
	 */
	public static class Person implements Serializable {

		private static final long serialVersionUID = -1035314106838589279L;

		public String secretid = "";// 秘密ID
		public int likes;// 赞数
		public int comments;// 评论数
		public int imageUrl;// 图片URL

	}

	/**
	 * 系统
	 */
	public static class System implements Serializable {

		private static final long serialVersionUID = 8671646455045620022L;

		public String imageUrl = "";// 图片URL
		public String title = "";// 主标题
		public String subtitle = "";// 副标题

	}

}
