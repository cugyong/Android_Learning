package com.example.xiayong.framework_selflearn.rxjava;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by xiayong on 2018/1/3.
 */

public class RxJavaTestActivity extends AppCompatActivity{

    private final static String TAG = "RxJavaTestActivity";
    private Disposable mDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        _testCreate1();
//        Log.d(TAG, "_testCreate1() complete");
//
//        _testCreate2();
//        Log.d(TAG, "_testCreate2() complete");

//        _testScheduler1();

        _testOperatorMap();
        Log.d(TAG, "_testOperatorMap() complete");

        _testOperatorFlatMapAndConcatMap();
        Log.d(TAG, "_testOperatorFlatMapAndConcatMap() complete");
    }

    private void _testCreate1(){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Log.d(TAG, "emit 1");
                emitter.onNext("1");
                Log.d(TAG, "emit 2");
                emitter.onNext("2");
                Log.d(TAG, "emit 3");
                emitter.onNext("3");
                Log.d(TAG, "emit complete");
                emitter.onComplete();
                Log.d(TAG, "emit 4");
                emitter.onNext("4");
            }
        }).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
                mDisposable = d;
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, s);
                if (s.equals("2")){
                    mDisposable.dispose();
                    Log.d(TAG, mDisposable.isDisposed() + "");
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }

    private void _testCreate2(){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("1");
                e.onComplete();
                e.onNext("2");
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }

    private void _testScheduler1(){
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                Log.d(TAG, "subscribe run in " + Thread.currentThread().getName());
                e.onNext(1);
            }
        });

        Consumer<Integer> consumer = new Consumer<Integer>() {
            @Override
            public void accept(@NonNull Integer integer) throws Exception {
                Log.d(TAG, "observe run in " + Thread.currentThread().getName());
            }
        };

        observable.subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.d(TAG, "After AndroidSchedulers.mainThread() observe run in " +
                                Thread.currentThread().getName());
                    }
                })
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.d(TAG, "After Schedulers.io() observe run in " +
                                Thread.currentThread().getName());
                    }
                })
                .subscribe(consumer);
    }

    private void _testOperatorMap(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).map(new Function<Integer, String>() {
            @Override
            public String apply(@NonNull Integer integer) throws Exception {
                return "This is value: " + integer;
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                    Log.d(TAG, s);
            }
        });
    }

    private void _testOperatorFlatMapAndConcatMap(){
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                e.onNext(1);
                e.onNext(2);
                e.onNext(3);
            }
        }).flatMap(new Function<Integer, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(@NonNull Integer integer) throws Exception {
                List<String> data = new ArrayList<String>();
                for (int i=0; i<3; i++){
                    data.add("This is value " + integer);
                }
                return Observable.fromIterable(data);
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.d(TAG, s);
            }
        });
    }
}
