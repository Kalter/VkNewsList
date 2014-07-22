package com.example.vknewslist;

import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKAttachments.VKApiAttachment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentView extends CustomView {

	VKApiComment comment;
	Integer count;
	Integer offset;
	ViewHolder holder;

	static class ViewHolder {
		public TextView commentCountOfLike;
		public Button commentLikeButton;
		public TextView commentDate;
		public TextView commentUserName;
		public TextView commentText;
		public ImageView commentUserImage;
		public LinearLayout commentImageContainer;
	}

	public CommentView(VKApiComment comment, Map<Integer, VKApiUser> users,
			Map<Integer, VKApiCommunity> groups, View view,
			LayoutInflater inflater, Context ctx) {
		this.comment = comment;
		this.mUsers = users;
		this.mGroups = groups;
		this.mView = view;
		this.mInflater = inflater;
		this.mContext = ctx;
	}

	public View setupComment(ViewGroup parent) {
		if (mView == null) {
			mView = mInflater.inflate(R.layout.comment, parent, false);
		}

		VKAttachments attachments = null;
		mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));

		holder = new ViewHolder();

		holder.commentCountOfLike = (TextView) mView
				.findViewById(R.id.comment_like_count);
		holder.commentLikeButton = (Button) mView
				.findViewById(R.id.comment_like_button);
		holder.commentDate = (TextView) mView.findViewById(R.id.comment_date);
		holder.commentUserName = (TextView) mView
				.findViewById(R.id.comment_user_name);
		holder.commentText = (TextView) mView.findViewById(R.id.comment_text);
		holder.commentUserImage = (ImageView) mView
				.findViewById(R.id.comment_user_image);
		holder.commentImageContainer = (LinearLayout) mView
				.findViewById(R.id.comment_image_container);

		if (holder.commentImageContainer.getChildCount() > 0) {
			holder.commentImageContainer.removeAllViews();
		}
		
		holder.commentCountOfLike.setText(String.valueOf(comment.likes));
		holder.commentDate.setText(translateDate(comment.date));
		holder.commentUserName.setText(getName(comment.from_id));
		
		if(comment.text != null && comment.text.length() > 0){
			holder.commentText.setText(comment.text);
		} else {
			holder.commentText.setVisibility(View.GONE);
		}
		
		mImageLoader.displayImage(getPhoto100(comment.from_id),
				holder.commentUserImage, mOptions, mAnimateFirstListener);
		
		if (comment.attachments != null && comment.attachments.size() > 0) {
			attachments = comment.attachments;
			showImage(holder.commentImageContainer, attachments);
		}
		holder.commentLikeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (comment.user_likes) {
					VKRequest request = VKApi.likes().delete(
							VKParameters.from("type", "comment",
									VKApiConst.OWNER_ID,
									String.valueOf(comment.from_id), "item_id",
									String.valueOf(comment.id)));
					request.useSystemLanguage = false;

					request = VKRequest.getRegisteredRequest(request
							.registerObject());
					if (request != null) {
						request.unregisterObject();
					}
					request.executeWithListener(new VKRequestListener() {
						@Override
						public void onComplete(VKResponse response) {
							comment.likes -= 1;
							holder.commentCountOfLike.setText(String
									.valueOf(comment.likes));

							comment.user_likes = false;
						}

						@Override
						public void onError(VKError error) {
							Log.d("Error", error.toString());
						}
					});

				} else {
					VKRequest request = VKApi.likes().add(
							VKParameters.from("type", "comment",
									VKApiConst.OWNER_ID,
									comment.from_id, "item_id",
									comment.id));

					if (request != null) {
						request.unregisterObject();
					}
					request.executeWithListener(new VKRequestListener() {
						@Override
						public void onComplete(VKResponse response) {
							comment.likes+= 1;
							holder.commentCountOfLike.setText(String
									.valueOf(comment.likes));

							comment.can_like = true;
						}

						@Override
						public void onError(VKError error) {
							Log.d("Error", error.toString());
						}
					});
				}
			}
		});
		return mView;
	}

	
	
}
