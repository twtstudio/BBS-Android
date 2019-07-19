package com.twtstudio.bbs.bdpqchen.bbs.home;

import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseView;

/**
 * Created by bdpqchen on 17-4-21.
 */

public interface HomeContract {

    interface View extends BaseView{
        void onGotMessageCount(int integer);
        void onGetMessageFailed(String m);
    }

    interface Presenter extends BasePresenter{
        void getUnreadMessageCount();
    }
}
