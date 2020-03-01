package com.samsung.dishlist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.samsung.dishlist.models.Dish;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import ca.rmen.porterstemmer.PorterStemmer;

public class MainActivity extends AppCompatActivity {
    LoadConfigAsyncTask task = new LoadConfigAsyncTask();
    ProgressBar progressBar;
    PorterStemmer porterStemmer = new PorterStemmer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLoadConfig = findViewById(R.id.button_load_config);
        Button buttonFindDishes = findViewById(R.id.button_find_dishes);
        final EditText editText = findViewById(R.id.editText);
        final TextView textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar);

        buttonLoadConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream ins = getResources().openRawResource(
                        getResources().getIdentifier("test", "raw", getPackageName()));

                task.execute(ins);
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String ingr = porterStemmer.stemWord(s.toString());
                try {
                    ArrayList<Dish> dishes = task.get().get(ingr);
                    textView.setText(dishes.toString());
                } catch (Exception e) {
                    textView.setText("");
                }
            }
        });

        buttonFindDishes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingr = porterStemmer.stemWord(editText.getText().toString());
                try {
                    ArrayList<Dish> dishes = task.get().get(ingr);
                    textView.setText(dishes.toString());
                } catch (ExecutionException | InterruptedException e) {
                    Toast.makeText(getBaseContext(), "Config not loaded", Toast.LENGTH_SHORT).show();
                } catch (NullPointerException e) {
                    Toast.makeText(getBaseContext(), "No such dish", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public class LoadConfigAsyncTask extends AsyncTask<InputStream, Void, HashMap<String, ArrayList<Dish>>> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected HashMap<String, ArrayList<Dish>> doInBackground(InputStream... streams) {
            String json = getJsonContent(streams[0]);

            return loadMap(new Gson().fromJson(json, Dish[].class));
        }

        @Override
        protected void onPostExecute(HashMap<String, ArrayList<Dish>> dishes) {
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

        private HashMap<String, ArrayList<Dish>> loadMap(Dish[] dishes) {
            HashMap<String, ArrayList<Dish>> map = new HashMap<>();

            for (Dish dish : dishes) {
                for (String ingredient : dish.ingredients) {
                    String stemIngredient = porterStemmer.stemWord(ingredient);
                    if (map.containsKey(stemIngredient)) {
                        map.get(stemIngredient).add(dish);
                    } else {
                        ArrayList<Dish> list = new ArrayList<>();
                        list.add(dish);
                        map.put(stemIngredient, list);
                    }
                }
            }

            return map;
        }
    }
}
