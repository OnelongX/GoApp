package com.ways2u.android.goapp;

import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ways2u.android.goapp.base.BaseActivity;
import com.ways2u.android.goapp.meizi.fragment.MainFragment;

import org.reactivestreams.Subscriber;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 */
public class MainActivity extends BaseActivity {
    private MainFragment mainFragment;

    @Override
    public void undateUI(Message msg) {

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void initView() {
        mainFragment = new MainFragment();
        initFragment(mainFragment);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    public void initFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (ft != null && fragment != null) {
            ft.add(R.id.main_content, fragment, "MainFragment");
            ft.commit();
        }
    }

}
