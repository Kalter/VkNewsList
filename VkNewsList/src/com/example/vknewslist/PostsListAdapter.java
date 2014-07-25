package com.example.vknewslist;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKPostArray;

public class PostsListAdapter extends BaseAdapter {

	Context mContext;
	LayoutInflater mInflater;
	VKPostArray mPosts;
	Map<Integer, VKApiUser> mUsers;
	Map<Integer, VKApiCommunity> mGroups;
	CustomView mCustomView;
	ViewHolder mViewHolder;

	/**
	 * Number of post that start downloading
	 */
	Integer startFrom;

	/**
	 * Count of loaded posts
	 */
	Integer count;

	public PostsListAdapter(Context context, VKPostArray posts,
			Map<Integer, VKApiUser> users, Map<Integer, VKApiCommunity> groups,
			Integer startFrom, Integer count) {
		this.mContext = context;
		this.mPosts = posts;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.startFrom = startFrom;
		this.count = count;
		this.mGroups = groups;
		this.mUsers = users;
		mCustomView = new CustomView();
		mCustomView.mImageLoader.init(ImageLoaderConfiguration
				.createDefault(mContext));
	}

	@Override
	public int getCount() {
		return mPosts.size();
	}

	@Override
	public Object getItem(int position) {
		return mPosts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final VKApiPost post = (VKApiPost) getItem(position);

		HeaderViewHolder headerViewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.wall_post_list_item,
					parent, false);
			mViewHolder = new ViewHolder(convertView, post);
			convertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}

		VKAttachments attachments = null;
		mCustomView.setGroups(mGroups);
		mCustomView.setUsers(mUsers);
		mCustomView.setInflater(mInflater);
		

		RelativeLayout header = (RelativeLayout) mInflater.inflate(
				R.layout.wall_post_list_item_header, null);

		headerViewHolder = new HeaderViewHolder(header);

		if (mViewHolder.imageContainer.getChildCount() > 0) {
			mViewHolder.imageContainer.removeAllViews();
		}

		if (mViewHolder.postHeader.getChildCount() > 0) {
			mViewHolder.postHeader.removeAllViews();
		}
		if (post.text != null && post.text.length() > 0) {
			headerViewHolder.postUserComment.setText(post.text);
		} else {
			headerViewHolder.postUserComment.setVisibility(View.GONE);
		}

		mViewHolder.postHeader.addView(header);

		headerViewHolder.postDate.setText(mCustomView.translateDate(post.date));
		if (post.source_id != 0) {
			headerViewHolder.postUserName.setText(mCustomView
					.getName(post.source_id));
			mCustomView.mImageLoader.displayImage(
					mCustomView.getPhoto100(post.source_id),
					headerViewHolder.postUserImage, mCustomView.mOptions,
					mCustomView.mAnimateFirstListener);
		} else {
			headerViewHolder.postUserName.setText(mCustomView
					.getName(post.owner_id));
			mCustomView.mImageLoader.displayImage(
					mCustomView.getPhoto100(post.owner_id),
					headerViewHolder.postUserImage, mCustomView.mOptions,
					mCustomView.mAnimateFirstListener);
		}

		// Show author list if our wall post reposted from another wall;
		for (VKApiPost p : post.copy_history) {

			RelativeLayout h = (RelativeLayout) mInflater.inflate(
					R.layout.wall_post_list_item_header, null);

			((TextView) h.findViewById(R.id.post_date)).setText(mCustomView
					.translateDate(p.date));

			if (p.text != null && p.text.length() > 0) {
				((TextView) h.findViewById(R.id.post_comment)).setText(p.text);

			} else {
				((TextView) h.findViewById(R.id.post_comment))
						.setVisibility(View.GONE);
			}

			if (p.owner_id > 0) {
				((TextView) h.findViewById(R.id.post_user_name)).setText(mUsers
						.get(p.owner_id).first_name
						+ " "
						+ mUsers.get(p.owner_id).last_name);
				mCustomView.mImageLoader
						.displayImage(mUsers.get(p.owner_id).photo_100,
								(ImageView) h
										.findViewById(R.id.post_user_image),
								mCustomView.mOptions,
								mCustomView.mAnimateFirstListener);
			} else {
				((TextView) h.findViewById(R.id.post_user_name))
						.setText(mGroups.get(p.owner_id * (-1)).name);
				mCustomView.mImageLoader
						.displayImage(mGroups.get(p.owner_id * (-1)).photo_100,
								(ImageView) h
										.findViewById(R.id.post_user_image),
								mCustomView.mOptions,
								mCustomView.mAnimateFirstListener);
			}
			if (p.attachments.size() > 0) {
				attachments = p.attachments;
			}

			mViewHolder.postHeader.addView(h);
		}
		if (attachments == null && post.attachments.size() > 0) {
			attachments = post.attachments;
			mCustomView.showImage(mViewHolder.imageContainer, attachments);
		} else {
			if (attachments != null) {
				mCustomView.showImage(mViewHolder.imageContainer, attachments);
			}
		}

		mViewHolder.postCountOfLike.setText(Integer.toString(post.likes_count));
		mViewHolder.postCountOfShare.setText(Integer
				.toString(post.reposts_count));

		if (!post.can_publish) {
			mViewHolder.postShareButton.setVisibility(View.GONE);
		}
		return convertView;
	}

	static class ViewHolder {
		
		VKApiPost mPost;

		@InjectView(R.id.post_share_count)
		TextView postCountOfShare;
		@InjectView(R.id.post_like_count)
		TextView postCountOfLike;
		@InjectView(R.id.post_share_button)
		Button postShareButton;
		@InjectView(R.id.post_like_button)
		Button postLikeButton;
		@InjectView(R.id.post_text)
		TextView postText;
		// Linear layout that conteins all headers;
		@InjectView(R.id.post_header)
		LinearLayout postHeader;
		// Linear Layout that contains all photo attachments
		@InjectView(R.id.image_container)
		LinearLayout imageContainer;
		
		@OnClick (R.id.post_like_button) void like(){
			int post_id = mPost.post_id != 0 ? mPost.post_id : mPost.id;
			int source_id = mPost.source_id != 0 ? mPost.source_id
					: mPost.from_id;
			if (mPost.user_likes) {
				VKRequest request = VKApi.likes().delete(
						VKParameters.from("type", "post",
								VKApiConst.OWNER_ID,
								String.valueOf(source_id), "item_id",
								String.valueOf(post_id)));
				request.useSystemLanguage = false;

				request = VKRequest.getRegisteredRequest(request
						.registerObject());
				if (request != null) {
					request.unregisterObject();
				}
				request.executeWithListener(new VKRequestListener() {

					@Override
					public void onComplete(VKResponse response) {
						mPost.likes_count -= 1;
						postCountOfLike.setText(String
								.valueOf(mPost.likes_count));

						mPost.user_likes = false;
					}

					@Override
					public void onError(VKError error) {
						Log.d("Error", error.toString());
					}
				});

			} else {
				VKRequest request = VKApi.likes().add(
						VKParameters.from("type", "post",
								VKApiConst.OWNER_ID,
								String.valueOf(source_id), "item_id",
								String.valueOf(post_id)));

				request.useSystemLanguage = false;

				request = VKRequest.getRegisteredRequest(request
						.registerObject());
				if (request != null) {
					request.unregisterObject();
				}
				request.executeWithListener(new VKRequestListener() {
					@Override
					public void onComplete(VKResponse response) {
						mPost.likes_count += 1;
						postCountOfLike.setText(String
								.valueOf(mPost.likes_count));

						mPost.user_likes = true;
					}

					@Override
					public void onError(VKError error) {
						Log.d("Error", error.toString());
					}
				});
			}

		}
		@OnClick (R.id.post_share_button) void share(){

			VKRequest request = VKApi
					.wall()
					.repost(VKParameters.from(
							VKApiConst.OBJECT,
							"wall"
									+ String.valueOf(mPost.source_id)
									+ "_"
									+ String.valueOf(mPost.post_id)));

			request.useSystemLanguage = false;

			request = VKRequest.getRegisteredRequest(request
					.registerObject());
			if (request != null) {
				request.unregisterObject();
			}
			request.executeWithListener(new VKRequestListener() {
				@Override
				public void onComplete(VKResponse response) {
					mPost.reposts_count += 1;
					postCountOfShare.setText(String
							.valueOf(mPost.reposts_count));

					mPost.user_reposted = true;
				}

				@Override
				public void onError(VKError error) {
					Log.d("Error", error.toString());
				}
			});
		
		}
		
		public ViewHolder(View view, VKApiPost post) {
			this.mPost = post;
			ButterKnife.inject(this, view);
		}
	}

	static class HeaderViewHolder {
		@InjectView(R.id.post_comment)
		TextView postUserComment;
		@InjectView(R.id.post_date)
		TextView postDate;
		@InjectView(R.id.post_user_name)
		TextView postUserName;

		@InjectView(R.id.post_user_image)
		ImageView postUserImage;

		public HeaderViewHolder(View header) {
			ButterKnife.inject(this, header);
		}
	}
}