package com.example.vknewslist;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.vknewslist.PostsListFragment.PostIdListener;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiCommunityArray;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKPostArray;
import com.vk.sdk.api.model.VKUsersArray;

public class NewsActivity extends SherlockFragmentActivity implements
		PostIdListener {
	final static String POST_LIST_TAG = "PostsListFragment";
	final static String WALL_POST_TAG = "WallPostFragment";
	String id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		VKUIHelper.onCreate(this);
		createNews();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VKUIHelper.onDestroy(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		VKUIHelper.onResume(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
	}

	void createNews() {
		PostsListFragment postsListFragment = (PostsListFragment) getSupportFragmentManager()
				.findFragmentByTag(POST_LIST_TAG);
		if (postsListFragment == null) {
			postsListFragment = new PostsListFragment();
		}
		getSupportFragmentManager().beginTransaction()
				.add(R.id.container, postsListFragment, POST_LIST_TAG)
				.show(postsListFragment).commit();
	}

	public void showWallPost(WallPostFragment f) {
		PostsListFragment postsListFragment = (PostsListFragment) getSupportFragmentManager()
				.findFragmentByTag(POST_LIST_TAG);
		getSupportFragmentManager().beginTransaction().addToBackStack(null)
				.add(R.id.container, f, WALL_POST_TAG).hide(postsListFragment)
				.show(f).commit();
	}

	@Override
	public void setId(final String id) {
		this.id = id;
		VKRequest request = VKApi.wall().getById(
				VKParameters.from("posts", id, VKApiConst.EXTENDED, 1));

		request.executeWithListener(new VKRequestListener() {
			@Override
			public void onComplete(VKResponse response) {
				ParseJSON  parseJSON = new ParseJSON();
				parseJSON.execute(response);
			}

			@Override
			public void onError(VKError error) {
				Log.d("Error", error.toString());
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_itemlist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logout:
			VKSdk.logout();
			startActivity(new Intent(this, LoginVkActivity.class));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	class ParseJSON extends AsyncTask<VKResponse, Void, Void> {

		@Override
		protected Void doInBackground(VKResponse... params) {
			VKResponse response = null;
			for (VKResponse r : params) {
				response = r;
			}
			VKPostArray p = (VKPostArray) response.parsedModel;

			// get a list of users that are mentioned in the received posts
			VKUsersArray u = new VKUsersArray();
			u.fill(response.json.optJSONObject("response").optJSONArray(
					"profiles"), VKApiUser.class);

			// get a list of groups that are mentioned in the received posts
			VKApiCommunityArray g = new VKApiCommunityArray();
			g.fill(response.json.optJSONObject("response").optJSONArray(
					"groups"), VKApiCommunity.class);

			Map<Integer, VKApiCommunity> groups = new HashMap<Integer, VKApiCommunity>();
			Map<Integer, VKApiUser> users = new HashMap<Integer, VKApiUser>();

			for (VKApiCommunity group : g) {
				groups.put(group.id, group);
			}
			for (VKApiUser user : u) {
				users.put(user.id, user);
			}
			WallPostFragment wallPostFragment = (WallPostFragment) getSupportFragmentManager()
					.findFragmentByTag(WALL_POST_TAG);
			if (wallPostFragment == null) {
				wallPostFragment = new WallPostFragment();
				wallPostFragment.id = id;
				wallPostFragment.post = p.get(0);
				wallPostFragment.groups = groups;
				wallPostFragment.users = users;
			}
			showWallPost(wallPostFragment);

			return null;
		}

	}
}
