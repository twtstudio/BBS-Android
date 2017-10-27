package com.twtstudio.bbs.bdpqchen.bbs.message.personal;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseFragment;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.SnackBarUtil;
import com.twtstudio.bbs.bdpqchen.bbs.main.MainContract;
import com.twtstudio.bbs.bdpqchen.bbs.main.MainPresenter;
import com.twtstudio.bbs.bdpqchen.bbs.main.hot.HotEntity;
import com.twtstudio.bbs.bdpqchen.bbs.main.latest.LatestAdapter;
import com.twtstudio.bbs.bdpqchen.bbs.main.latest.LatestEntity;

import java.util.List;

import butterknife.BindView;

/**
 * Created by bdpqchen on 17-6-5.
 */

public class PersonalFragment extends BaseFragment implements MainContract.View {

    @BindView(R.id.pb_loading)
    ProgressBar mPbLoading;
    @BindView(R.id.rv_latest)
    RecyclerView mRvLatest;
    @BindView(R.id.srl_latest)
    SwipeRefreshLayout mSrlLatest;
    @BindView(R.id.tv_latest_no_data)
    TextView mTvLatestNoData;

    private LatestAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean mRefreshing = false;
    private MainPresenter mPresenter;
    public static PersonalFragment newInstance() {
        return new PersonalFragment();
    }

    @Override
    protected int getFragmentLayoutId() {
        return R.layout.fragmetn_latest;
    }

    @Override
    protected MainPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected void initFragment() {
//        mPresenter = new MainPresenter(this);
        mAdapter = new LatestAdapter(getActivity());
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRvLatest.setLayoutManager(mLinearLayoutManager);
        mRvLatest.setAdapter(mAdapter);
        mSrlLatest.setColorSchemeColors(getResources().getIntArray(R.array.swipeRefreshColors));
        mSrlLatest.setOnRefreshListener(() -> {
            mRefreshing = true;
            mSrlLatest.setRefreshing(true);
        });

    }

    @Override
    public void onGetLatestList(List<LatestEntity> list) {

    }

    @Override
    public void onGetHotList(List<HotEntity> list) {

    }

    @Override
    public void onGotDataFailed(String msg) {
        hideLoading();
        setRefreshing(false);
        SnackBarUtil.notice(this.getActivity(), msg + "\n刷新试试～");
    }

    void setRefreshing(boolean b) {
        mRefreshing = b;
        mSrlLatest.setRefreshing(b);
    }

    private void hideLoading() {
        if (mPbLoading != null) {
            mPbLoading.setVisibility(View.GONE);
        }
    }

}