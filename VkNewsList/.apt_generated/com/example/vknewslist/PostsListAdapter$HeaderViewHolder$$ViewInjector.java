// Generated code from Butter Knife. Do not modify!
package com.example.vknewslist;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class PostsListAdapter$HeaderViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.example.vknewslist.PostsListAdapter.HeaderViewHolder target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2130968648, "field 'postUserImage'");
    target.postUserImage = (android.widget.ImageView) view;
    view = finder.findRequiredView(source, 2130968649, "field 'postUserName'");
    target.postUserName = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2130968650, "field 'postDate'");
    target.postDate = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2130968651, "field 'postUserComment'");
    target.postUserComment = (android.widget.TextView) view;
  }

  public static void reset(com.example.vknewslist.PostsListAdapter.HeaderViewHolder target) {
    target.postUserImage = null;
    target.postUserName = null;
    target.postDate = null;
    target.postUserComment = null;
  }
}
