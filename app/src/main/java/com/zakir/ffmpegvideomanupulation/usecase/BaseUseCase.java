package com.zakir.ffmpegvideomanupulation.usecase;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public abstract class BaseUseCase<Response, Request, Error> {

    private final Scheduler subscriberScheduler;
    private final Scheduler observerScheduler;
    private final CompositeDisposable compositeDisposable;

    protected BaseUseCase(Scheduler subscriberScheduler, Scheduler observerScheduler) {
        this.subscriberScheduler = subscriberScheduler;
        this.observerScheduler = observerScheduler;
        compositeDisposable = new CompositeDisposable();
    }

    abstract Observable<Response> build(Request request);


    public void execute(DisposableObserver<Response> observer, Request request) {
        final Observable<Response> observable = build(request)
                .subscribeOn(subscriberScheduler)
                .observeOn(observerScheduler);
        addDisposable(observable.subscribeWith(observer));
    }

    public void dispose() {
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public boolean remove(Disposable disposable) {
        return compositeDisposable.remove(disposable);
    }

    private void addDisposable(Disposable disposable) {
        compositeDisposable.add(disposable);
    }
}
