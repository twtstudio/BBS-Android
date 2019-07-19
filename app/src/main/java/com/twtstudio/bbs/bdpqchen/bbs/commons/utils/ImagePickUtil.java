package com.twtstudio.bbs.bdpqchen.bbs.commons.utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;

import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.REQUEST_CODE_IMAGE_SELECTED;

/**
 * Created by bdpqchen on 17-6-9.
 */

public final class ImagePickUtil {

    public static void commonPickImage(Fragment fragment){
        if (PermissionUtil.checkReadStorage(fragment.getActivity())) {
            Matisse.from(fragment)
                    .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF))
                    .countable(true)
                    .maxSelectable(1)
                    .gridExpectedSize(fragment.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .forResult(REQUEST_CODE_IMAGE_SELECTED);
        }
    }

    public static void commonPickImage(Activity activity){
        if (PermissionUtil.checkReadStorage(activity)) {
            Matisse.from(activity)
                    .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF))
                    .countable(true)
                    .maxSelectable(1)
                    .gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .forResult(REQUEST_CODE_IMAGE_SELECTED);
        }

    }

}
