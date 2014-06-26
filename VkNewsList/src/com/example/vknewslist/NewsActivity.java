package com.example.vknewslist;


import java.util.HashMap;
import java.util.Map;

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
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.VKRequest.VKRequestListener;
import com.vk.sdk.api.model.VKApiCommunity;
import com.vk.sdk.api.model.VKApiCommunityArray;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKPostArray;
import com.vk.sdk.api.model.VKUsersArray;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

public class NewsActivity extends SherlockFragmentActivity implements PostIdListener{
	 PostsListFragment lf = new PostsListFragment();
	 WallPostFragment f = new WallPostFragment();
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
		
	        getSupportFragmentManager()
	                .beginTransaction()
	                .add(R.id.container, lf)
	                .show(lf)
	                .commit();
	    }
	 
	 public void showWallPost(WallPostFragment f) {
	        getSupportFragmentManager()
	                .beginTransaction()
	                .addToBackStack(null)
	                .add(R.id.container, f)
	                .hide(lf)
	                .show(f)
	                .commit();
	    }

	@Override
	public void setId(final String s) {
		
		
		VKRequest request = VKApi.wall().getById(
				VKParameters.from("posts", s, VKApiConst.EXTENDED, 1));


		request.executeWithListener(new VKRequestListener() {
			@Override
			public void onComplete(VKResponse response) {
				VKPostArray p = (VKPostArray) response.parsedModel;

				// получаем список пользователей, которые упоминаются в
				// пришедших постах
				VKUsersArray u = new VKUsersArray();
				u.fill(response.json.optJSONObject("response").optJSONArray(
						"profiles"), VKApiUser.class);

				// получаем список групп, которые упоминаются в пришедших постах
				VKApiCommunityArray g = new VKApiCommunityArray();
				g.fill(response.json.optJSONObject("response").optJSONArray(
						"groups"), VKApiCommunity.class);

				 Map<Integer, VKApiCommunity> gg = new HashMap<Integer, VKApiCommunity>();
				 Map<Integer, VKApiUser> uu = new HashMap<Integer, VKApiUser>();
				 
				for (VKApiCommunity gr : g) {
				 gg.put(gr.id, gr);
				}
				for (VKApiUser us : u) {
					uu.put(us.id, us);
				}
				
				f = new WallPostFragment();
				f.id = s;
				
				setupFragment(f, p, gg, uu);
			}

			@Override
			public void onError(VKError error) {
				Log.d("Error", error.toString());
			}
		});

	}
	
	private void setupFragment(WallPostFragment f, VKPostArray p, Map<Integer, VKApiCommunity>gg,   Map<Integer, VKApiUser> uu) {
		f.post = p.get(0);
		f.groups = gg;
		f.users = uu;
		showWallPost(f);
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
	
}
