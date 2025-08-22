package com.example.cats;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
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

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<CatImage> getCatImage() {
        return catImage;
    }

    public void loadCatImage() {
        Disposable disposable = loadCatImageRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<CatImage>() {
                    @Override
                    public void accept(CatImage image) throws Throwable {
                        catImage.setValue(image);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        Log.d(TAG, "Error" + throwable.getMessage());
                    }
                });
        compositeDisposable.add(disposable);
    }

    private Single<CatImage> loadCatImageRx() {
        return Single.fromCallable(new Callable<CatImage>() {
            @Override
            public CatImage call() throws Exception {
                URL urlSite = new URL(BASE_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) urlSite.openConnection();
                try {
                    InputStream inputStream = urlConnection.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder data = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        data.append(line);
                    }
                    bufferedReader.close();

                    JSONArray jsonArray = new JSONArray(data.toString());
                    if (jsonArray.length() == 0) {
                        throw new Exception("Empty JSON array");
                    }
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String id = jsonObject.getString(ID);
                    String urlStr = jsonObject.getString(URL);
                    int width = jsonObject.getInt(WIDTH);
                    int height = jsonObject.getInt(HEIGHT);
                    return new CatImage(id, urlStr, width, height);
                } finally {
                    urlConnection.disconnect();
                }
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}