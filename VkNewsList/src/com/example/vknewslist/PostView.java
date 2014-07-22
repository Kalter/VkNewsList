package com.example.vknewslist;

import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;

public class PostView extends CustomView {

	static class ViewHolder {

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

		public ViewHolder(View view) {
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

	VKApiPost mPost;
	ViewHolder mViewHolder;
	HeaderViewHolder mHeaderViewHolder;

	public PostView(VKApiPost post, Map<Integer, VKApiUser> users,
			Map<Integer, VKApiCommunity> groups, View view,
			LayoutInflater inflater, Context ctx) {
		this.mPost = post;
		this.mUsers = users;
		this.mGroups = groups;
		this.mView = view;
		this.mInflater = inflater;
		this.mContext = ctx;
	}

	public View setupPostView(ViewGroup parent) {

		if (mView == null) {
			mView = mInflater.inflate(R.layout.wall_post_list_item, parent,
					false);
		} 

		VKAttachments attachments = null;
		RelativeLayout header = (RelativeLayout) mInflater.inflate(
				R.layout.wall_post_list_item_header, null);

		mViewHolder = new ViewHolder(mView);
		mHeaderViewHolder = new HeaderViewHolder(header);

		mImageLoader.init(ImageLoaderConfiguration.createDefault(mContext));

		if (mViewHolder.imageContainer.getChildCount() > 0) {
			mViewHolder.imageContainer.removeAllViews();
		}

		if (mViewHolder.postHeader.getChildCount() > 0) {
			mViewHolder.postHeader.removeAllViews();
		}
		if (mPost.text != null && mPost.text.length() > 0) {
			mHeaderViewHolder.postUserComment.setText(mPost.text);
		} else {
			mHeaderViewHolder.postUserComment.setVisibility(View.GONE);
		}

		mViewHolder.postHeader.addView(header);

		mHeaderViewHolder.postDate.setText(translateDate(mPost.date));
		if (mPost.source_id != 0) {
			mHeaderViewHolder.postUserName.setText(getName(mPost.source_id));
			mImageLoader.displayImage(getPhoto100(mPost.source_id),
					mHeaderViewHolder.postUserImage, mOptions,
					mAnimateFirstListener);
		} else {
			mHeaderViewHolder.postUserName.setText(getName(mPost.owner_id));
			mImageLoader.displayImage(getPhoto100(mPost.owner_id),
					mHeaderViewHolder.postUserImage, mOptions,
					mAnimateFirstListener);
		}

		// Show author list if our wall post reposted from another wall;
		for (VKApiPost p : mPost.copy_history) {

			RelativeLayout h = (RelativeLayout) mInflater.inflate(
					R.layout.wall_post_list_item_header, null);

			((TextView) h.findViewById(R.id.post_date))
					.setText(translateDate(p.date));

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
				mImageLoader.displayImage(mUsers.get(p.owner_id).photo_100,
						(ImageView) h.findViewById(R.id.post_user_image),
						mOptions, mAnimateFirstListener);
			} else {
				((TextView) h.findViewById(R.id.post_user_name))
						.setText(mGroups.get(p.owner_id * (-1)).name);
				mImageLoader.displayImage(
						mGroups.get(p.owner_id * (-1)).photo_100,
						(ImageView) h.findViewById(R.id.post_user_image),
						mOptions, mAnimateFirstListener);
			}
			if (p.attachments.size() > 0) {
				attachments = p.attachments;
			}

			mViewHolder.postHeader.addView(h);
		}
		if (attachments == null && mPost.attachments.size() > 0) {
			attachments = mPost.attachments;
			showImage(mViewHolder.imageContainer, attachments);
		} else {
			if (attachments != null) {
				showImage(mViewHolder.imageContainer, attachments);
			}
		}

		mViewHolder.postCountOfLike
				.setText(Integer.toString(mPost.likes_count));
		mViewHolder.postCountOfShare.setText(Integer
				.toString(mPost.reposts_count));

		// set listener to LIKE BUTTON
		mViewHolder.postLikeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
							mViewHolder.postCountOfLike.setText(String
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
							mViewHolder.postCountOfLike.setText(String
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
		});

		if (mPost.can_publish) {
			// set listener to REPOST BUTTON
			mViewHolder.postShareButton
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
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
									mViewHolder.postCountOfShare.setText(String
											.valueOf(mPost.reposts_count));

									mPost.user_reposted = true;
								}

								@Override
								public void onError(VKError error) {
									Log.d("Error", error.toString());
								}
							});
						}
					});
		} else {
			mViewHolder.postShareButton.setVisibility(View.GONE);
		}
		return mView;
	}

}
