package com.ways2u.android.goapp;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.ways2u.android.goapp.base.BaseActivity;

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

    /**
     * @param msg
     */
    // Used to load the 'native-lib' library on application startup.
/*
    static {
        System.loadLibrary("native-lib");
    }

*/
    @Override
    public void undateUI(Message msg) {

    }



    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    @Override
    protected void initView() {
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText("hello");

        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("11");
                e.onNext("22");
                e.onComplete();
            }
        });
        observable.compose(this.<String>bindToLifecycle());
        observable.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String value) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
}
