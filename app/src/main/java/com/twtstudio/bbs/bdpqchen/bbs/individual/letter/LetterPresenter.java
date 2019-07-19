package com.twtstudio.bbs.bdpqchen.bbs.individual.letter;

import com.twtstudio.bbs.bdpqchen.bbs.commons.model.BaseModel;
import com.twtstudio.bbs.bdpqchen.bbs.commons.presenter.RxPresenter;
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.ResponseTransformer;
import com.twtstudio.bbs.bdpqchen.bbs.commons.rx.SimpleObserver;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.twtstudio.bbs.bdpqchen.bbs.individual.letter.LetterActivity.REFRESH;

/**
 * Created by bdpqchen on 17-7-4.
 */

class LetterPresenter extends RxPresenter implements LetterContract.Presenter {
    private LetterContract.View mView;

    LetterPresenter(LetterContract.View view) {
        mView = view;
    }

    @Override
    public void getLetterList(int uid, int page, int mode) {
        SimpleObserver<List<LetterModel>> observer = new SimpleObserver<List<LetterModel>>() {
            @Override
            public void _onError(String msg) {
                if (mView != null)
                    mView.onGetLetterFailed(msg);
            }

            @Override
            public void _onNext(List<LetterModel> LetterModel) {
                if (mView != null) {
                    if (mode == REFRESH) {
                        mView.onRefreshList(LetterModel);
                    } else {
                        mView.onGetLetterList(LetterModel);
                    }
                }
            }
        };
        addSubscribe(sHttpClient.getLetterList(uid, page)
                .map(new ResponseTransformer<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        );
    }

    @Override
    public void sendLetter(int to_uid, String content) {
        ResponseTransformer<BaseModel> transformer = new ResponseTransformer<>();
        SimpleObserver<BaseModel> observer = new SimpleObserver<BaseModel>() {
            @Override
            public void _onError(String msg) {
                if (mView != null)
                    mView.onSendFailed(msg);
            }

            @Override
            public void _onNext(BaseModel model) {
                if (mView != null)
                    mView.onSend(model);
            }
        };
        addSubscribe(sHttpClient.sendLetter(to_uid, content)
                .map(transformer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer)
        );
    }


}
