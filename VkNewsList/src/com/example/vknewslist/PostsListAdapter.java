package com.example.vknewslist;

import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKPostArray;

public class PostsListAdapter extends BaseAdapter {

	Context ctx;
	LayoutInflater lInflater;
	VKPostArray posts;
	Map<Integer, VKApiUser> users;
	Map<Integer, VKApiCommunity> groups;

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
		this.ctx = context;
		this.posts = posts;
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.startFrom = startFrom;
		this.count = count;
		this.groups = groups;
		this.users = users;
	}

	@Override
	public int getCount() {
		return posts.size();
	}

	@Override
	public Object getItem(int position) {
		return posts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final VKApiPost post = (VKApiPost) getItem(position);

		return (new PostView(post, users, groups, convertView, lInflater, ctx))
				.setupPostView(parent);
	}
}