package com.example.aplikacjadoocenyfilmow;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.widget.Spinner;
import android.widget.EditText;
import android.widget.Button;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Switch;

import androidx.appcompat.app.AlertDialog;

import android.widget.ListView;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class MainActivity extends AppCompatActivity {

    private List<Movie> movieList = new ArrayList<>();
    private ArrayAdapter<String> adapter1;
    private SharedPreferences sharedPreferences;
    private static final String MOVIE_LIST_KEY = "movieList";
    private Switch sortSwitch;
    private ListView listView;
    private boolean sorted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText film = findViewById(R.id.filmName);
        Spinner genre = findViewById(R.id.spinner);
        EditText rate = findViewById(R.id.filmRate);
        Button button = findViewById(R.id.button);
        listView = findViewById(R.id.outcome);
        sortSwitch = findViewById(R.id.switch1);
        List<String> resultList = new ArrayList<>();

        String[] filmGenre = {"Akcja", "Animacja", "Dokumentalny", "Dramat", "Fantasy", "Horror", "Komedia", "Przygodowy", "Romans", "Science fiction", "Thriller", "Inne"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, filmGenre);
        genre.setAdapter(adapter);

        sortSwitch.setOnCheckedChangeListener((cb, b) -> {
            if (b) {
                sortMoviesByName();
            } else {
                if (sorted) {
                    loadData();
                } else {
                    refreshListView();
                }
            }
        });

        adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, resultList);
        listView.setAdapter(adapter1);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        loadData();

        button.setOnClickListener(view -> {
            String filmName = film.getText().toString().trim();
            String selectedGenre = genre.getSelectedItem().toString();
            String filmRateStr = rate.getText().toString().trim();

            if (filmName.isEmpty() || filmRateStr.isEmpty()) {
                showAlertDialog("Proszę uzupełnić wymagane wartości!");
            } else {
                int filmRate = Integer.parseInt(filmRateStr);
                if (filmRate < 1 || filmRate > 5) {
                    showAlertDialog("Ocena powinna się zawierać w przedziale 1-5");
                } else {
                    Movie newMovie = new Movie(filmName, getGenreDescription(selectedGenre), filmRate);
                    movieList.add(newMovie);

                    saveData();
                    if (sortSwitch.isChecked()) {
                        sortMoviesByName();
                    } else {
                        refreshListView();
                    }

                    film.getText().clear();
                    rate.getText().clear();
                }
            }
        });
    }

    private String getGenreDescription(String selectedGenre) {
        String genreDescription;

        switch (selectedGenre) {
            case "Akcja":
                genreDescription = "Film akcji";
                break;
            case "Animacja":
                genreDescription = "Film animowany";
                break;
            case "Dokumentalny":
                genreDescription = "Film dokumentalny";
                break;
            case "Dramat":
                genreDescription = "Film dramatyczny";
                break;
            case "Fantasy":
                genreDescription = "Film fantasy";
                break;
            case "Horror":
                genreDescription = "Film horror";
                break;
            case "Komedia":
                genreDescription = "Film komediowy";
                break;
            case "Przygodowy":
                genreDescription = "Film przygodowy";
                break;
            case "Romans":
                genreDescription = "Film romantyczny";
                break;
            case "Science fiction":
                genreDescription = "Film science fiction";
                break;
            case "Thriller":
                genreDescription = "Thriller";
                break;
            default:
                genreDescription = "Inny gatunek";
                break;
        }

        return genreDescription;
    }

    private void sortMoviesByName() {
        Collections.sort(movieList, (movie1, movie2) -> movie1.getName().compareToIgnoreCase(movie2.getName()));
        sorted = true;
        refreshListView();
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message).setTitle("MovieCritic").setPositiveButton("OK", (dialog, id) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void refreshListView() {
        adapter1.clear();

        for (Movie movie : movieList) {
            adapter1.add(movie.toString());
        }
        adapter1.notifyDataSetChanged();
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(movieList);
        editor.putString(MOVIE_LIST_KEY, json);
        editor.apply();
    }

    private void loadData() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(MOVIE_LIST_KEY, "");
        Type type = new TypeToken<List<Movie>>() {
        }.getType();
        movieList = gson.fromJson(json, type);

        if (movieList == null) {
            movieList = new ArrayList<>();
        }

        sorted = false;
        refreshListView();
    }

    public class Movie {
        private String name;
        private String genre;
        private int rate;

        public Movie(String name, String genre, int rate) {
            this.name = name;
            this.genre = genre;
            this.rate = rate;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name + ", Gatunek: " + genre + ", Ocena: " + rate;
        }
    }
}