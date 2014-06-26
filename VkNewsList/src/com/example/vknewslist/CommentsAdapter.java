package com.example.vknewslist;

import java.util.Map;

import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKCommentArray;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CommentsAdapter extends BaseAdapter {
	Context ctx;
	LayoutInflater lInflater;
	VKCommentArray comments;
	VKApiPost post;
	Map<Integer, VKApiUser> users;
	Map<Integer, VKApiCommunity> groups;
	Integer count;
	Integer offset;
	boolean k = true;

	public CommentsAdapter(Context context, VKCommentArray comments,
			Map<Integer, VKApiUser> users, Map<Integer, VKApiCommunity> groups,
			Integer count, Integer offset, VKApiPost post) {
		this.ctx = context;
		this.comments = comments;
		lInflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.offset = offset;
		this.count = count;
		this.groups = groups;
		this.users = users;
		this.post = post;
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	@Override
	public Object getItem(int position) {
		return comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return (new CommentView(comments.get(position),users,groups,convertView,lInflater,ctx)).setupComment(parent);
	}
}
