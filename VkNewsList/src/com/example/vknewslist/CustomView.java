package com.example.vknewslist;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKAttachments.VKApiAttachment;

public class CustomView {
	protected DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheOnDisc(true).considerExifParams(true)
			// .displayer(new RoundedBitmapDisplayer(20))
			.build();;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	protected ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

	protected Context ctx;
	protected LayoutInflater inflater;
	protected View view;
	protected Map<Integer, VKApiUser> users;
	protected Map<Integer, VKApiCommunity> groups;

	protected String getName(int id) {
		if (id > 0) {
			return users.get(id).first_name + " " + users.get(id).last_name;
		} else {
			return groups.get(id * (-1)).name;
		}
	}

	protected String getPhoto100(int id) {
		if (id > 0) {
			return users.get(id).photo_100;
		} else {
			return groups.get(id * (-1)).photo_100;
		}
	}

	protected String translateDate(long date) {
		return (new SimpleDateFormat("HH:mm   dd.MM.yyyy"))
				.format(new java.util.Date((long) date * 1000));
	}

	protected void showImage(LinearLayout layout, VKAttachments attachments) {
		for (VKApiAttachment a : attachments) {
			if (a.getClass() == VKApiPhoto.class) {
				ImageView photo = (ImageView) inflater.inflate(
						R.layout.post_image, null);
				VKApiPhoto p = (VKApiPhoto) a;
				if (p.photo_807 != null && p.photo_807.length() > 0) {
					imageLoader.displayImage(p.photo_807, photo, options,
							animateFirstListener);
				} else {
					if (p.photo_604 != null && p.photo_604.length() > 0) {
						imageLoader.displayImage(p.photo_604, photo, options,
								animateFirstListener);
					} else {
						if (p.photo_130 != null && p.photo_130.length() > 0) {
							imageLoader.displayImage(p.photo_130, photo,
									options, animateFirstListener);
						} else {
							if (p.photo_75 != null && p.photo_75.length() > 0) {
								imageLoader.displayImage(p.photo_75, photo,
										options, animateFirstListener);
							}
						}
					}
				}
				layout.addView(photo);
			}
		}
	}

	protected static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
