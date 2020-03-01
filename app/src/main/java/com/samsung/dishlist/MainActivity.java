package com.samsung.dishlist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.samsung.dishlist.models.Dish;

import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    LoadConfigAsyncTask task = new LoadConfigAsyncTask();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLoadConfig = findViewById(R.id.button_load_config);
        Button buttonFindDishes = findViewById(R.id.button_find_dishes);
        TextView textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);

        buttonLoadConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream ins = getResources().openRawResource(
                        getResources().getIdentifier("test", "raw", getPackageName()));

                task.execute(ins);
            }
        });

        buttonFindDishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    public class LoadConfigAsyncTask extends AsyncTask<InputStream, Void, Dish[]> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Dish[] doInBackground(InputStream... streams) {
            String json = getJsonContent(streams[0]);

            return new Gson().fromJson(json, Dish[].class);
        }

        @Override
        protected void onPostExecute(Dish[] dishes) {
            progressBar.setVisibility(View.GONE);
        }

        private String getJsonContent(InputStream ins) {
            Scanner scanner = new Scanner(ins);
            StringBuilder builder = new StringBuilder();
            while (scanner.hasNext()) {
                builder.append(scanner.next());
            }
            return builder.toString();
        }
    }
}
