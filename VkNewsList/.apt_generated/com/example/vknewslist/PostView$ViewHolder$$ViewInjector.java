// Generated code from Butter Knife. Do not modify!
package com.example.vknewslist;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class PostView$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.example.vknewslist.PostView.ViewHolder target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2130968646, "field 'postCountOfLike'");
    target.postCountOfLike = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2130968642, "field 'postText'");
    target.postText = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2130968641, "field 'postHeader'");
    target.postHeader = (android.widget.LinearLayout) view;
    view = finder.findRequiredView(source, 2130968647, "field 'postLikeButton'");
    target.postLikeButton = (android.widget.Button) view;
    view = finder.findRequiredView(source, 2130968644, "field 'postShareButton'");
    target.postShareButton = (android.widget.Button) view;
    view = finder.findRequiredView(source, 2130968645, "field 'postCountOfShare'");
    target.postCountOfShare = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2130968643, "field 'imageContainer'");
    target.imageContainer = (android.widget.LinearLayout) view;
  }

  public static void reset(com.example.vknewslist.PostView.ViewHolder target) {
    target.postCountOfLike = null;
    target.postText = null;
    target.postHeader = null;
    target.postLikeButton = null;
    target.postShareButton = null;
    target.postCountOfShare = null;
    target.imageContainer = null;
  }
}
