package com.link.bianmi.entity.builder;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.link.bianmi.entity.Comment;
import com.link.bianmi.entity.ListResult;

public class CommentBuilder implements BaseEntityBuilder<ListResult<Comment>> {

	private static CommentBuilder mInstance = null;

	private CommentBuilder() {

	}

	public static CommentBuilder getInstance() {
		if (mInstance == null) {
			mInstance = new CommentBuilder();
		}

		return mInstance;
	}

	@Override
	public ListResult<Comment> buildEntity(JSONObject jsonObj) {
		ListResult<Comment> listResult = new ListResult<Comment>();
		listResult.list = new ArrayList<Comment>();
		try {
			if (jsonObj != null && jsonObj.has("list")) {
				JSONObject listJson = jsonObj.getJSONObject("list");
				if (listJson != null && listJson.has("has_more")) {
					listResult.hasMore = listJson.getBoolean("has_more");
				}

				if (listJson != null && listJson.has("comments")) {
					JSONArray jsonArr = listJson.getJSONArray("comments");
					for (int i = 0; i < jsonArr.length(); i++) {
						JSONObject jsonArrObj = jsonArr.getJSONObject(i);

						Comment comment = new Comment();
						comment.resourceId = jsonArrObj.getString("id");
						comment.userid = jsonArrObj.getString("userid");
						comment.content = jsonArrObj.getString("content");
						comment.avatarUrl = jsonArrObj.getString("avatar_url");
						comment.audioUrl = jsonArrObj.getString("audio_url");
						comment.audioLength = jsonArrObj.getInt("audio_length");
						comment.likes = jsonArrObj.getInt("likes");
						comment.isLiked = jsonArrObj.getBoolean("isliked");
						comment.createdTime = jsonArrObj.getLong("created_time");
						listResult.list.add(comment);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return listResult;
	}
}