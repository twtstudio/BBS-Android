package com.twtstudio.bbs.bdpqchen.bbs.auth.login;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.twtstudio.bbs.bdpqchen.bbs.R;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BaseActivity;
import com.twtstudio.bbs.bdpqchen.bbs.commons.base.BasePresenter;

import butterknife.BindView;

/**
 * Created by bdpqchen on 17-5-2.
 */

public class twt_login extends BaseActivity implements LoginContract.View {

    @BindView(R.id.et_account_twt)
    EditText mEtAccount;
    @BindView(R.id.et_password_twt)
    EditText mEtPassword;
    @BindView(R.id.linearLayout_twt)
    LinearLayout linearLayout;
    @BindView(R.id.linearLayout2_twt)
    LinearLayout mlinearlayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_twt);
    }

    @Override
    public void loginSuccess(LoginModel loginModel) {

    }

    @Override
    public void loginFailed(String msg) {

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login_twt;
    }

    @Override
    protected Toolbar getToolbarView() {
        return null;
    }

    @Override
    protected BasePresenter getPresenter() {
        return null;
    }
}

