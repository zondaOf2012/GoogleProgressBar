// Generated code from Butter Knife. Do not modify!
package com.jpardogo.android.googleprogressbar;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final com.jpardogo.android.googleprogressbar.MainActivity target, Object source) {
    View view;
    view = finder.findById(source, 2131034173);
    if (view == null) {
      throw new IllegalStateException("Required view with id '2131034173' for field 'mProgressBar' was not found. If this view is optional add '@Optional' annotation.");
    }
    target.mProgressBar = (com.jpardogo.android.googleprogressbar.library.GoogleProgressBar) view;
  }

  public static void reset(com.jpardogo.android.googleprogressbar.MainActivity target) {
    target.mProgressBar = null;
  }
}
