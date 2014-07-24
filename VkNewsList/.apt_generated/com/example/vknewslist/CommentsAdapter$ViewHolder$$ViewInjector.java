// Generated code from Butter Knife. Do not modify!
package com.example.vknewslist;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class CommentsAdapter$ViewHolder$$ViewInjector {
  public static void inject(Finder finder, final com.example.vknewslist.CommentsAdapter.ViewHolder target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2130968629, "field 'commentDate'");
    target.commentDate = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2130968633, "field 'commentLikeButton'");
    target.commentLikeButton = (android.widget.Button) view;
    view = finder.findRequiredView(source, 2130968631, "field 'commentImageContainer'");
    target.commentImageContainer = (android.widget.LinearLayout) view;
    view = finder.findRequiredView(source, 2130968632, "field 'commentCountOfLike'");
    target.commentCountOfLike = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2130968627, "field 'commentUserImage'");
    target.commentUserImage = (android.widget.ImageView) view;
    view = finder.findRequiredView(source, 2130968630, "field 'commentText'");
    target.commentText = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2130968628, "field 'commentUserName'");
    target.commentUserName = (android.widget.TextView) view;
  }

  public static void reset(com.example.vknewslist.CommentsAdapter.ViewHolder target) {
    target.commentDate = null;
    target.commentLikeButton = null;
    target.commentImageContainer = null;
    target.commentCountOfLike = null;
    target.commentUserImage = null;
    target.commentText = null;
    target.commentUserName = null;
  }
}
