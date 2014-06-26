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

import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKAttachments.VKApiAttachment;

public class PostView extends CustomView {

	static class ViewHolder {
		public TextView postUserComment;
		public TextView postCountOfShare;
		public TextView postCountOfLike;
		public Button postShareButton;
		public Button postLikeButton;
		public ImageView postImage;
		public TextView postDate;
		public TextView postUserName;
		public TextView postText;
		public ImageView postUserImage;
		public LinearLayout imageContainer;
		public LinearLayout postHeader;
	}

	VKApiPost post;
	ViewHolder holder;

	public PostView(VKApiPost post, Map<Integer, VKApiUser> users,
			Map<Integer, VKApiCommunity> groups, View view,
			LayoutInflater inflater, Context ctx) {
		this.post = post;
		this.users = users;
		this.groups = groups;
		this.view = view;
		this.inflater = inflater;
		this.ctx = ctx;
	}

	public View setupPostView(ViewGroup parent) {

		if (view == null) {
			view = inflater
					.inflate(R.layout.wall_post_list_item, parent, false);
		}

		VKAttachments attachments = null;
		RelativeLayout header = (RelativeLayout) inflater.inflate(
				R.layout.wall_post_list_item_header, null);

		holder = new ViewHolder();

		imageLoader.init(ImageLoaderConfiguration.createDefault(ctx));

		holder.postCountOfLike = (TextView) view
				.findViewById(R.id.post_like_count);
		holder.postCountOfShare = (TextView) view
				.findViewById(R.id.post_share_count);
		holder.postLikeButton = (Button) view
				.findViewById(R.id.post_like_button);
		holder.postShareButton = (Button) view
				.findViewById(R.id.post_share_button);

		holder.postDate = (TextView) header.findViewById(R.id.post_date);
		holder.postUserName = (TextView) header
				.findViewById(R.id.post_user_name);
		holder.postText = (TextView) header.findViewById(R.id.post_text);
		holder.postUserComment = (TextView) header
				.findViewById(R.id.post_comment);
		holder.postUserImage = (ImageView) header
				.findViewById(R.id.post_user_image);

		// Linear layout that conteins all headers;
		holder.postHeader = (LinearLayout) view.findViewById(R.id.post_header);

		// Linear Layout that contains all photo attachments
		holder.imageContainer = (LinearLayout) view
				.findViewById(R.id.image_container);

		if (holder.imageContainer.getChildCount() > 0) {
			holder.imageContainer.removeAllViews();
		}

		if (holder.postHeader.getChildCount() > 0) {
			holder.postHeader.removeAllViews();
		}
		if (post.text != null && post.text.length() > 0) {
			holder.postUserComment.setText(post.text);
		} else {
			holder.postUserComment.setVisibility(View.GONE);
		}

		holder.postHeader.addView(header);

		holder.postDate.setText(translateDate(post.date));
		if (post.source_id != 0) {
			holder.postUserName.setText(getName(post.source_id));
			imageLoader.displayImage(getPhoto100(post.source_id),
					holder.postUserImage, options, animateFirstListener);
		} else {
			holder.postUserName.setText(getName(post.owner_id));
			imageLoader.displayImage(getPhoto100(post.owner_id),
					holder.postUserImage, options, animateFirstListener);
		}

		// Show author list if our wall post reposted from another wall;
		for (VKApiPost p : post.copy_history) {

			RelativeLayout h = (RelativeLayout) inflater.inflate(
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
				((TextView) h.findViewById(R.id.post_user_name)).setText(users
						.get(p.owner_id).first_name
						+ " "
						+ users.get(p.owner_id).last_name);
				imageLoader.displayImage(users.get(p.owner_id).photo_100,
						(ImageView) h.findViewById(R.id.post_user_image),
						options, animateFirstListener);
			} else {
				((TextView) h.findViewById(R.id.post_user_name)).setText(groups
						.get(p.owner_id * (-1)).name);
				imageLoader.displayImage(
						groups.get(p.owner_id * (-1)).photo_100,
						(ImageView) h.findViewById(R.id.post_user_image),
						options, animateFirstListener);
			}
			if (p.attachments.size() > 0) {
				attachments = p.attachments;
			}

			holder.postHeader.addView(h);
		}
		if (attachments == null && post.attachments.size() > 0) {
			attachments = post.attachments;
			showImage(holder.imageContainer, attachments);
		} else {
			if (attachments != null) {
				showImage(holder.imageContainer, attachments);
			}
		}

		holder.postCountOfLike.setText(Integer.toString(post.likes_count));
		holder.postCountOfShare.setText(Integer.toString(post.reposts_count));

		// set listener to LIKE BUTTON
		holder.postLikeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int post_id = post.post_id != 0 ? post.post_id : post.id;
				int source_id = post.source_id != 0 ? post.source_id
						: post.from_id;
				if (post.user_likes) {
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
							post.likes_count -= 1;
							holder.postCountOfLike.setText(String
									.valueOf(post.likes_count));

							post.user_likes = false;
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
							post.likes_count += 1;
							holder.postCountOfLike.setText(String
									.valueOf(post.likes_count));

							post.user_likes = true;
						}

						@Override
						public void onError(VKError error) {
							Log.d("Error", error.toString());
						}
					});
				}
			}
		});

		if (post.can_publish) {
			// set listener to REPOST BUTTON
			holder.postShareButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					VKRequest request = VKApi.wall().repost(
							VKParameters.from(VKApiConst.OBJECT, "wall"
									+ String.valueOf(post.source_id) + "_"
									+ String.valueOf(post.post_id)));

					request.useSystemLanguage = false;

					request = VKRequest.getRegisteredRequest(request
							.registerObject());
					if (request != null) {
						request.unregisterObject();
					}
					request.executeWithListener(new VKRequestListener() {
						@Override
						public void onComplete(VKResponse response) {
							post.reposts_count += 1;
							holder.postCountOfShare.setText(String
									.valueOf(post.reposts_count));

							post.user_reposted = true;
						}

						@Override
						public void onError(VKError error) {
							Log.d("Error", error.toString());
						}
					});
				}
			});
		} else {
			holder.postShareButton.setVisibility(View.GONE);
		}
		return view;
	}

}
