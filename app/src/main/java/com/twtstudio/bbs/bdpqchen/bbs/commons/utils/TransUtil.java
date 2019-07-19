package com.twtstudio.bbs.bdpqchen.bbs.commons.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.twtstudio.bbs.bdpqchen.bbs.R;

/**
 * Created by bdpqchen on 17-9-25.
 */

public final class TransUtil {

    public static Bundle getAvatarTransOptions(Context context, View view) {
        return getTransOptions(context, view, R.string.share_avatar);
    }

    public static Bundle getTransOptions(Context context, View view, int stringId) {
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation((Activity) context, view, context.getString(stringId));
        return options.toBundle();
    }

    public static void setSharedElementsInterpolator(Window window) {
        if (VersionUtil.eaLollipop()) {
            if (window.getSharedElementEnterTransition() != null) {
                window.getSharedElementEnterTransition().setInterpolator(new AccelerateDecelerateInterpolator());
            }
            if (window.getSharedElementExitTransition() != null) {
                window.getSharedElementExitTransition().setInterpolator(new AccelerateDecelerateInterpolator());
            }
        }
    }


}
