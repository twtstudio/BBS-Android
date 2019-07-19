package com.twtstudio.bbs.bdpqchen.bbs.individual.updateInfo;

import android.os.Bundle;

import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseView;
import com.twtstudio.bbs.bdpqchen.bbs.commons.model.BaseModel;

import java.io.File;

/**
 * Created by bdpqchen on 17-5-6.
 */

interface UpdateInfoContract {

    interface View extends BaseView {
        void updateAvatarFailed(String msg);

        void updateAvatarSuccess(BaseModel baseModel);

        void updateInfoFailed(String m);

        void updateInfoSuccess();
    }

    interface Presenter extends BasePresenter {
        void doUpdateAvatar(File file);

        void doUpdateInfo(Bundle bundle, int t);
    }

}
