package com.link.bianmi.entity.manager;

import java.util.ArrayList;
import java.util.List;
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
		ADD, GET_COMMENTS
	}

	/** 单页数量 **/
	private static final int BATCH = 8;

	public static class API {

		private static Result<Comment> addComment(Comment comment) {
			if (comment == null)
				return null;
			Result<Comment> result = null;
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("secretid", comment.secretid));
			params.add(new BasicNameValuePair("userid", comment.userid));
			params.add(new BasicNameValuePair("content", comment.content));
			params.add(new BasicNameValuePair("audio_url", comment.audioUrl));
			params.add(new BasicNameValuePair("audio_length", String.valueOf(comment.audioLength)));
			params.add(new BasicNameValuePair("created_time", String
					.valueOf(comment.createdTime)));

			Response response = HttpClient.doPost(params, SysConfig
					.getInstance().getAddCommentUrl());
			if (response != null) {
				try {
					// 解析Status
					JSONObject jsonObj = response.asJSONObject();
					result = new Result<Comment>();
					result.status = StatusBuilder.getInstance().buildEntity(
							jsonObj);
				} catch (ResponseException e) {
					e.printStackTrace();
				}
			}

			return result;
		}

		public static Result<ListResult<Comment>> getComments(String userid,
				String secretid, String lastid) {
			Result<ListResult<Comment>> result = null;

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userid", userid));
			params.add(new BasicNameValuePair("secretid", secretid));
			params.add(new BasicNameValuePair("lastid", lastid));
			params.add(new BasicNameValuePair("batch", String.valueOf(BATCH)));
			Response response = HttpClient.doPost(params, SysConfig
					.getInstance().getCommentsUrl());

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

	}

	public static class Task {
		public static void addComment(Comment comment,
				OnTaskOverListener<Comment> listener) {
			TaskParams taskParams = new TaskParams();
			taskParams.put("comment", comment);
			CommentTask userTask = new CommentTask(TaskType.ADD, listener);
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
			TaskResult<?> taskResult = null;
			// 发表
			if (taskType == TaskType.ADD) {
				Comment comment = (Comment) params[0].get("comment");
				Result<Comment> result = API.addComment(comment);
				if (result != null && result.status != null
						&& result.status.code == Status_.OK) {
					taskResult = new TaskResult<Comment>(TaskStatus.OK,
							result.t);
				} else if (result != null && result.status != null) {
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							result.status);
				} else {
					resultStatus = new Status_();
					resultStatus.msg = "获取数据失败!";
					taskResult = new TaskResult<Status_>(TaskStatus.FAILED,
							resultStatus);
				}

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
					resultStatus.msg = "获取数据失败!";
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

			// 发表
			if (taskType == TaskType.ADD) {
				if (taskResult.getStatus() == TaskStatus.OK) {
					((OnTaskOverListener<Comment>) listener)
							.onSuccess((Comment) taskResult.getEntity());
				} else if (taskResult.getStatus() == TaskStatus.FAILED) {
					Status_ result = (Status_) taskResult.getEntity();
					((OnTaskOverListener<Comment>) listener).onFailure(
							result.code, result.msg);
				}
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

			}
		}
	}
}