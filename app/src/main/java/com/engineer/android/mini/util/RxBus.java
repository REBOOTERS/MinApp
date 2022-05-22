package com.engineer.android.mini.util;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * @author rookie
 * @since 07-11-2019
 */
public class RxBus {
    private static final RxBus RX_BUS = new RxBus();

    public static RxBus getInstance() {
        return RX_BUS;
    }

    private final PublishSubject<Object> mSubject = PublishSubject.create();

    private RxBus() {
    }

    public void post(Object object) {
        mSubject.onNext(object);
    }

    public <T> Observable<T> toObservable(Class<T> eventType) {
        return mSubject.ofType(eventType);
    }

    public Observable<Object> toObservable() {
        return mSubject.hide();
    }

    public boolean hasObservers() {
        return mSubject.hasObservers();
    }
}
