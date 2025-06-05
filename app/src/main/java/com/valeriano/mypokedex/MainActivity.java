package com.valeriano.mypokedex;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.valeriano.mypokedex.FavoritesFragment;
import com.valeriano.mypokedex.PokemonListFragment;

public class MainActivity extends AppCompatActivity {
    private FrameLayout fragmentContainer;
    private RadioGroup navigationRadioGroup;
    private CheckBox soundCheckBox;
    private TextView welcomeText;
    private EditText searchEditText;
    private Button searchButton;
    private Spinner regionSpinner;
    private ImageView logoImageView;
    private Button aboutButton;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupSpinner();
        setupListeners();
        setupMediaPlayer();

        // Cargar fragment inicial
        loadFragment(new PokemonListFragment());
    }

    private void initializeViews() {
        fragmentContainer = findViewById(R.id.fragment_container);
        navigationRadioGroup = findViewById(R.id.navigation_radio_group);
        soundCheckBox = findViewById(R.id.sound_checkbox);
        welcomeText = findViewById(R.id.welcome_text);
        searchEditText = findViewById(R.id.search_edit_text);
        searchButton = findViewById(R.id.search_button);
        regionSpinner = findViewById(R.id.region_spinner);
        logoImageView = findViewById(R.id.logo_image_view);
        aboutButton = findViewById(R.id.about_button);
    }

    private void setupSpinner() {
        String[] regions = {"Kanto", "Johto", "Hoenn", "Sinnoh", "Unova", "Kalos", "Alola", "Galar"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, regions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regionSpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        navigationRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_pokemon_list) {
                loadFragment(new PokemonListFragment());
            } else if (checkedId == R.id.radio_favorites) {
                loadFragment(new FavoritesFragment());
            }
        });

        searchButton.setOnClickListener(v -> {
            String searchTerm = searchEditText.getText().toString().trim();
            if (!searchTerm.isEmpty()) {
                searchPokemon(searchTerm);
            } else {
                Toast.makeText(this, "Ingresa el nombre de un Pokémon", Toast.LENGTH_SHORT).show();
            }
        });

        soundCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && mediaPlayer != null) {
                mediaPlayer.start();
                Toast.makeText(this, "🎵 Música activada", Toast.LENGTH_SHORT).show();
            } else if (mediaPlayer != null) {
                mediaPlayer.pause();
                Toast.makeText(this, "🔇 Música desactivada", Toast.LENGTH_SHORT).show();
            }
        });

        aboutButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://pokemon.com"));
            startActivity(browserIntent);
        });

        regionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRegion = parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this,
                        "Región seleccionada: " + selectedRegion, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.pokemon_theme);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void searchPokemon(String pokemonName) {
        Intent intent = new Intent(this, PokemonDetailActivity.class);
        intent.putExtra("pokemon_name", pokemonName);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
}