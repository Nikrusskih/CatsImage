package com.example.cats;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {
    private static final String BASE_URL = "https://api.thecatapi.com/v1/images/search";
    private static final String ID = "id";
    private static final String URL = "url";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String TAG = "MainActivity";
    private MutableLiveData<CatImage> catImage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<Boolean> isError = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getIsError() {
        return isError;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<CatImage> getCatImage() {
        return catImage;
    }

    public void loadCatImage() {
        Disposable disposable = loadCatImageRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Throwable {
                        isLoading.setValue(true);
                    }
                })
                .doAfterTerminate(new Action() {
                    @Override
                    public void run() throws Throwable {
                        isLoading.setValue(false);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        isError.setValue(true);
                    }
                })
                .subscribe(new Consumer<CatImage>() {
                    @Override
                    public void accept(CatImage image) throws Throwable {
                        catImage.setValue(image);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        isError.setValue(true);
                        Log.d(TAG, "Error" + throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private Single<CatImage> loadCatImageRx() {
        return ApiFactory.getApiService()
                .loadCatImage()
                .map(list->{
                    if (list.isEmpty()){
                        throw new Exception("No image found");
                    }
                    return list.get(0);
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}