// Generated code from Butter Knife. Do not modify!
package com.example.vknewslist;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class PostsListAdapter$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.example.vknewslist.PostsListAdapter.ViewHolder target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2130968644, "field 'postShareButton' and method 'share'");
    target.postShareButton = (android.widget.Button) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.share();
        }
      });
    view = finder.findRequiredView(source, 2130968642, "field 'postText'");
    target.postText = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2130968646, "field 'postCountOfLike'");
    target.postCountOfLike = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2130968643, "field 'imageContainer'");
    target.imageContainer = (android.widget.LinearLayout) view;
    view = finder.findRequiredView(source, 2130968645, "field 'postCountOfShare'");
    target.postCountOfShare = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2130968647, "field 'postLikeButton' and method 'like'");
    target.postLikeButton = (android.widget.Button) view;
    view.setOnClickListener(
      new android.view.View.OnClickListener() {
        @Override public void onClick(
          android.view.View p0
        ) {
          target.like();
        }
      });
    view = finder.findRequiredView(source, 2130968641, "field 'postHeader'");
    target.postHeader = (android.widget.LinearLayout) view;
  }

  public static void reset(com.example.vknewslist.PostsListAdapter.ViewHolder target) {
    target.postShareButton = null;
    target.postText = null;
    target.postCountOfLike = null;
    target.imageContainer = null;
    target.postCountOfShare = null;
    target.postLikeButton = null;
    target.postHeader = null;
  }
}
