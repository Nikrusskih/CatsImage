package com.example.cats;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private MainViewModel viewModel;

    private ImageView imageViewCat;
    private ProgressBar progressBar;
    private Button buttonLoadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.loadCatImage();
        viewModel.getCatImage().observe(this, new Observer<CatImage>() {
            @Override
            public void onChanged(CatImage catImage) {
                Glide.with(MainActivity.this)
                        .load(catImage.getUrl())
                        .into(imageViewCat);
            }
        });
    }

    private void initViews() {
        imageViewCat = findViewById(R.id.imageViewCat);
        progressBar = findViewById(R.id.progressBar);
        buttonLoadImage = findViewById(R.id.buttonLoadImage);
    }
}

