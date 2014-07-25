package com.example.vknewslist;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.model.VKApiComment;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKCommentArray;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentsAdapter extends BaseAdapter {
	Context mContext;
	LayoutInflater mInflater;
	VKCommentArray mComments;
	VKApiPost mPost;
	Map<Integer, VKApiUser> mUsers;
	Map<Integer, VKApiCommunity> mGroups;
	Integer mCount;
	Integer mOffset;
	ViewHolder mViewHolder;
	CustomView mCustomView;

	boolean k = true;
	
	public CommentsAdapter(Context context, VKCommentArray comments,
			Map<Integer, VKApiUser> users, Map<Integer, VKApiCommunity> groups,
			Integer count, Integer offset, VKApiPost post) {
		this.mContext = context;
		this.mComments = comments;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mOffset = offset;
		this.mCount = count;
		this.mGroups = groups;
		this.mUsers = users;
		this.mPost = post;
		mCustomView = new CustomView();
		mCustomView.mImageLoader.init(ImageLoaderConfiguration
				.createDefault(mContext));
	}

	@Override
	public int getCount() {
		return mComments.size();
	}

	@Override
	public Object getItem(int position) {
		return mComments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final VKApiComment comment = mComments.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.comment, parent, false);
			mViewHolder = new ViewHolder(convertView, comment);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		VKAttachments attachments = null;
		mCustomView.setGroups(mGroups);
		mCustomView.setUsers(mUsers);
		mCustomView.setInflater(mInflater);

		if (mViewHolder.commentImageContainer.getChildCount() > 0) {
			mViewHolder.commentImageContainer.removeAllViews();
		}

		mViewHolder.commentCountOfLike.setText(String.valueOf(comment.likes));
		mViewHolder.commentDate.setText(mCustomView.translateDate(comment.date));
		mViewHolder.commentUserName.setText(mCustomView.getName(comment.from_id));

		if (comment.text != null && comment.text.length() > 0) {
			mViewHolder.commentText.setText(comment.text);
		} else {
			mViewHolder.commentText.setVisibility(View.GONE);
		}

		mCustomView.mImageLoader.displayImage(mCustomView.getPhoto100(comment.from_id),
				mViewHolder.commentUserImage, mCustomView.mOptions,mCustomView.mAnimateFirstListener);

		if (comment.attachments != null && comment.attachments.size() > 0) {
			attachments = comment.attachments;
			mCustomView.showImage(mViewHolder.commentImageContainer, attachments);
		}

		return convertView;
	}
	
	static class ViewHolder {
		
		VKApiComment mComment;
		
		@InjectView(R.id.comment_like_count)
		TextView commentCountOfLike;
		@InjectView(R.id.comment_like_button)
		Button commentLikeButton;
		@InjectView(R.id.comment_date)
		TextView commentDate;
		@InjectView(R.id.comment_user_name)
		TextView commentUserName;
		@InjectView(R.id.comment_text)
		TextView commentText;
		@InjectView(R.id.comment_user_image)
		ImageView commentUserImage;
		@InjectView(R.id.comment_image_container)
		LinearLayout commentImageContainer;
		
		@OnClick(R.id.comment_like_button) void like(){
			Log.d("---", "comment_click");
			if (mComment.user_likes) {
				VKRequest request = VKApi.likes().delete(
						VKParameters.from("type", "comment",
								VKApiConst.OWNER_ID,
								String.valueOf(mComment.from_id), "item_id",
								String.valueOf(mComment.id)));
				request.useSystemLanguage = false;

				request = VKRequest.getRegisteredRequest(request
						.registerObject());
				if (request != null) {
					request.unregisterObject();
				}
				request.executeWithListener(new VKRequestListener() {
					@Override
					public void onComplete(VKResponse response) {
						mComment.likes -= 1;
						commentCountOfLike.setText(String
								.valueOf(mComment.likes));

						mComment.user_likes = false;
					}

					@Override
					public void onError(VKError error) {
						Log.d("Error", error.toString());
					}
				});

			} else {
				VKRequest request = VKApi.likes().add(
						VKParameters.from("type", "comment",
								VKApiConst.OWNER_ID, mComment.from_id,
								"item_id", mComment.id));

				if (request != null) {
					request.unregisterObject();
				}
				request.executeWithListener(new VKRequestListener() {
					@Override
					public void onComplete(VKResponse response) {
						mComment.likes += 1;
						commentCountOfLike.setText(String
								.valueOf(mComment.likes));

						mComment.can_like = true;
					}

					@Override
					public void onError(VKError error) {
						Log.d("Error", error.toString());
					}
				});
			}
		}
		
		public ViewHolder(View view, VKApiComment comment) {
			mComment = comment;
			ButterKnife.inject(this, view);
		}
	}
}
