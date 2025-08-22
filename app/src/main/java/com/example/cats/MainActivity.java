package com.example.cats;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;

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
        viewModel.getIsError().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isError) {
                if (isError){
                    Toast.makeText(MainActivity.this,
                            R.string.error_loading,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getIsLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                if (loading){
                    progressBar.setVisibility(View.VISIBLE);
                }else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        viewModel.getCatImage().observe(this, new Observer<CatImage>() {
            @Override
            public void onChanged(CatImage catImage) {
                Glide.with(MainActivity.this)
                        .load(catImage.getUrl())
                        .into(imageViewCat);
            }
        });
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.loadCatImage();
            }
        });
    }

    private void initViews() {
        imageViewCat = findViewById(R.id.imageViewCat);
        progressBar = findViewById(R.id.progressBar);
        buttonLoadImage = findViewById(R.id.buttonLoadImage);
    }
}

