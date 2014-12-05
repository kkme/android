package com.link.bianmi.entity.manager;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.link.bianmi.SysConfig;
import com.link.bianmi.UserConfig;
import com.link.bianmi.asynctask.BaseAsyncTask;
import com.link.bianmi.asynctask.TaskParams;
import com.link.bianmi.asynctask.TaskResult;
import com.link.bianmi.asynctask.TaskResult.TaskStatus;
import com.link.bianmi.asynctask.listener.ITaskOverListener;
import com.link.bianmi.asynctask.listener.OnSimpleTaskOverListener;
import com.link.bianmi.asynctask.listener.OnTaskOverListener;
import com.link.bianmi.entity.Comment;
import com.link.bianmi.entity.ListResult;
import com.link.bianmi.entity.Result;
import com.link.bianmi.entity.Status_;
import com.link.bianmi.entity.builder.CommentBuilder;
import com.link.bianmi.entity.builder.StatusBuilder;
import com.link.bianmi.http.HttpClient;
import com.link.bianmi.http.Response;
import com.link.bianmi.http.ResponseException;

public class CommentManager {

	public static enum TaskType {
		PUBLISH, // 发表评论
		GET_COMMENTS, // 获取评论列表
		LIKE// 评论点赞
	}

	/** 单页数量 **/
	private static final int BATCH = 20;

	public static class API {

		private static Status_ publishComment(Comment comment) {
			if (comment == null)
				return null;
			Status_ status = null;
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", comment.userid));
			params.add(new BasicNameValuePair("token", UserConfig.getInstance()
					.getToken()));
			params.add(new BasicNameValuePair("secretid", comment.secretid));
			params.add(new BasicNameValuePair("content", comment.content));
			params.add(new BasicNameValuePair("audio_url", comment.audioUrl));
			params.add(new BasicNameValuePair("audio_length", String
					.valueOf(comment.audioLength)));

			Response response = HttpClient.doPost(params, SysConfig
					.getInstance().getPublishCommentUrl());
			if (response != null) {
				try {
					// 解析Status
					JSONObject jsonObj = response.asJSONObject();
					status = StatusBuilder.getInstance().buildEntity(jsonObj);
				} catch (ResponseException e) {
					e.printStackTrace();
				}
			}

			return status;
		}

		private static Result<ListResult<Comment>> getComments(String userid,
				String secretid, String lastid) {
			Result<ListResult<Comment>> result = null;

			Response response = HttpClient.doGet(String.format(
					"%s?userid=%s&token=%s&secretid=%s&lastid=%s&batch=%d",
					SysConfig.getInstance().getCommentsUrl(), userid,
					UserConfig.getInstance().getToken(), secretid, lastid,
					BATCH));

			if (response != null) {
				try {
					// 解析Status
					JSONObject jsonObj = response.asJSONObject();
					result = new Result<ListResult<Comment>>();
					result.status = StatusBuilder.getInstance().buildEntity(
							jsonObj);
					if (result.status != null
							&& result.status.code == Status_.OK) {
						result.t = CommentBuilder.getInstance().buildEntity(
								jsonObj);
					}
				} catch (ResponseException e) {
					e.printStackTrace();
				}
			}

			return result;
		}

		private static Result<Boolean> likeOrDislike(String secretId,
				boolean isLiked) {
			if (secretId == null)
				return null;
			Result<Boolean> result = null;
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", UserConfig
					.getInstance().getUserId()));
			params.add(new BasicNameValuePair("token", UserConfig.getInstance()
					.getToken()));
			params.add(new BasicNameValuePair("commentid", secretId));
			params.add(new BasicNameValuePair("isliked", String
					.valueOf(isLiked)));

			Response response = HttpClient.doPost(params, SysConfig
					.getInstance().getCommentLikeUrl());
			if (response != null) {
				try {
					// 解析Status
					JSONObject jsonObj = response.asJSONObject();
					result = new Result<Boolean>();
					result.t = !isLiked;
					result.status = StatusBuilder.getInstance().buildEntity(
							jsonObj);
					if (result.status != null
							&& result.status.code == Status_.OK) {
						result.t = isLiked;
					}
				} catch (ResponseException e) {
					e.printStackTrace();
				}
			}

			return result;
		}

	}

	public static class Task {
		public static void publishComment(Comment comment,
				OnSimpleTaskOverListener listener) {
			TaskParams taskParams = new TaskParams();
			taskParams.put("comment", comment);
			CommentTask userTask = new CommentTask(TaskType.PUBLISH, listener);
			userTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);
		}

		public static void getComments(String secretId, String lastId,
				OnTaskOverListener<ListResult<Comment>> listener) {
			TaskParams taskParams = new TaskParams();
			taskParams.put("userid", UserConfig.getInstance().getUserId());
			taskParams.put("secretid", secretId);
			taskParams.put("lastid", lastId);
			CommentTask userTask = new CommentTask(TaskType.GET_COMMENTS,
					listener);
			userTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);
		}

		public static void likeOrDislike(String commentId, boolean isLiked,
				OnTaskOverListener<Boolean> listener) {
			TaskParams taskParams = new TaskParams();
			taskParams.put("commentid", commentId);
			taskParams.put("isliked", isLiked);
			CommentTask likeTask = new CommentTask(TaskType.LIKE, listener);
			likeTask.executeOnExecutor(Executors.newCachedThreadPool(),
					taskParams);
		}

	}

	static class CommentTask extends BaseAsyncTask {

		TaskType taskType;
		ITaskOverListener<?> listener;

		public CommentTask(TaskType taskType, ITaskOverListener<?> listener) {
			this.taskType = taskType;
			this.listener = listener;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected TaskResult<?> doInBackground(TaskParams... params) {
			Status_ resultStatus = null;
			TaskResult<?> taskResult = new TaskResult<Status_>(
					TaskStatus.FAILED, resultStatus);
			// 发表评论
			if (taskType == TaskType.PUBLISH) {

				Comment comment = (Comment) params[0].get("comment");
				taskResult = new TaskResult<Status_>(TaskStatus.OK,
						API.publishComment(comment));

				// 评论列表
			} else if (taskType == TaskType.GET_COMMENTS) {
				String userid = params[0].getString("userid");
				String secretid = params[0].getString("secretid");
				String lastid = params[0].getString("lastid");
				Result<ListResult<Comment>> result = API.getComments(userid,
						secretid, lastid);
				if (result != null && result.status != null
						&& result.status.code == Status_.OK) {
					taskResult = new TaskResult<ListResult<Comment>>(
							TaskStatus.OK, result.t);
				} else if (result != null && result.status != null) {
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							result.status);
				} else {
					resultStatus = new Status_();
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							resultStatus);
				}
				// 评论点赞
			} else if (taskType == TaskType.LIKE) {
				String commentId = (String) params[0].get("commentid");
				boolean isLiked = (Boolean) params[0].get("isliked");
				Result<Boolean> result = API.likeOrDislike(commentId, isLiked);
				if (result != null && result.status != null
						&& result.status.code == Status_.OK) {
					taskResult = new TaskResult<Boolean>(TaskStatus.OK,
							result.t);
				} else if (result != null && result.status != null) {
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							result.status);
				} else {
					resultStatus = new Status_();
					resultStatus.msg = "操作失败!";
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							resultStatus);
				}
			}
			return taskResult;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(TaskResult<?> taskResult) {
			super.onPostExecute(taskResult);
			Status_ status = null;
			// 发表评论
			if (taskType == TaskType.PUBLISH) {
				status = (Status_) taskResult.getEntity();
				if (status == null)
					status = new Status_();
				listener.onResult(status.code, status.msg);
				// 评论列表
			} else if (taskType == TaskType.GET_COMMENTS) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					((OnTaskOverListener<ListResult<Comment>>) listener)
							.onSuccess((ListResult<Comment>) taskResult
									.getEntity());
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					Status_ result = (Status_) taskResult.getEntity();
					((OnTaskOverListener<ListResult<Comment>>) listener)
							.onFailure(result.code, result.msg);
				}
				// 评论点赞
			} else if (taskType == TaskType.LIKE) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					((OnTaskOverListener<Boolean>) listener)
							.onSuccess((Boolean) taskResult.getEntity());
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					Status_ result = (Status_) taskResult.getEntity();
					((OnTaskOverListener<Boolean>) listener).onFailure(
							result.code, result.msg);
				}
			}
		}
	}
}