package com.valeriano.mypokedex;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.*;
import com.bumptech.glide.Glide;
import com.valeriano.mypokedex.models.Pokemon;
import com.valeriano.mypokedex.utils.ApiClient;

public class PokemonDetailActivity extends AppCompatActivity {
    private ImageView pokemonImageView;
    private TextView pokemonNameText;
    private TextView pokemonIdText;
    private TextView pokemonHeightText;
    private TextView pokemonWeightText;
    private TextView pokemonTypesText;
    private TextView pokemonAbilitiesText;
    private TextView pokemonStatsText;
    private ProgressBar loadingProgressBar;
    private LinearLayout contentLayout;
    private CheckBox favoriteCheckBox;
    private Button shareButton;
    private Button wikiButton;
    private VideoView pokemonVideoView;
    private Button playVideoButton;
    private EditText notesEditText;
    private Spinner moveSpinner;
    private RadioGroup viewModeRadioGroup;

    private Pokemon currentPokemon;
    private Handler mainHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_detail);

        initializeViews();
        setupListeners();

        mainHandler = new Handler(Looper.getMainLooper());

        String pokemonName = getIntent().getStringExtra("pokemon_name");
        if (pokemonName != null) {
            loadPokemonData(pokemonName);
        }
    }

    private void initializeViews() {
        pokemonImageView = findViewById(R.id.pokemon_image_view);
        pokemonNameText = findViewById(R.id.pokemon_name_text);
        pokemonIdText = findViewById(R.id.pokemon_id_text);
        pokemonHeightText = findViewById(R.id.pokemon_height_text);
        pokemonWeightText = findViewById(R.id.pokemon_weight_text);
        pokemonTypesText = findViewById(R.id.pokemon_types_text);
        pokemonAbilitiesText = findViewById(R.id.pokemon_abilities_text);
        pokemonStatsText = findViewById(R.id.pokemon_stats_text);
        loadingProgressBar = findViewById(R.id.loading_progress_bar);
        contentLayout = findViewById(R.id.content_layout);
        favoriteCheckBox = findViewById(R.id.favorite_checkbox);
        shareButton = findViewById(R.id.share_button);
        wikiButton = findViewById(R.id.wiki_button);
        pokemonVideoView = findViewById(R.id.pokemon_video_view);
        playVideoButton = findViewById(R.id.play_video_button);
        notesEditText = findViewById(R.id.notes_edit_text);
        moveSpinner = findViewById(R.id.move_spinner);
        viewModeRadioGroup = findViewById(R.id.view_mode_radio_group);
    }

    private void setupListeners() {
        favoriteCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "❤️ Agregado a favoritos", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "💔 Removido de favoritos", Toast.LENGTH_SHORT).show();
            }
        });

        shareButton.setOnClickListener(v -> {
            if (currentPokemon != null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        "¡Mira este increíble Pokémon: " + currentPokemon.getName() +
                                " #" + currentPokemon.getId() + "!");
                startActivity(Intent.createChooser(shareIntent, "Compartir Pokémon"));
            }
        });

        wikiButton.setOnClickListener(v -> {
            if (currentPokemon != null) {
                String url = "https://pokemon.fandom.com/wiki/" + currentPokemon.getName();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        playVideoButton.setOnClickListener(v -> {
            setupVideoView();
        });

        viewModeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_front_view && currentPokemon != null) {
                loadPokemonImage(currentPokemon.getSprites().getFront_default());
            } else if (checkedId == R.id.radio_back_view && currentPokemon != null) {
                loadPokemonImage(currentPokemon.getSprites().getBack_default());
            }
        });
    }

    private void loadPokemonData(String pokemonName) {
        showLoading(true);

        ApiClient.getPokemon(pokemonName, new ApiClient.PokemonCallback() {
            @Override
            public void onSuccess(Pokemon pokemon) {
                currentPokemon = pokemon;
                mainHandler.post(() -> {
                    displayPokemonData(pokemon);
                    showLoading(false);
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    Toast.makeText(PokemonDetailActivity.this, error, Toast.LENGTH_LONG).show();
                    showLoading(false);
                });
            }
        });
    }

    private void displayPokemonData(Pokemon pokemon) {
        pokemonNameText.setText(capitalizeFirst(pokemon.getName()));
        pokemonIdText.setText("#" + pokemon.getId());
        pokemonHeightText.setText("Altura: " + (pokemon.getHeight() / 10.0) + " m");
        pokemonWeightText.setText("Peso: " + (pokemon.getWeight() / 10.0) + " kg");

        // Tipos
        StringBuilder types = new StringBuilder("Tipos: ");
        if (pokemon.getTypes() != null) {
            for (int i = 0; i < pokemon.getTypes().size(); i++) {
                types.append(capitalizeFirst(pokemon.getTypes().get(i).getType().getName()));
                if (i < pokemon.getTypes().size() - 1) types.append(", ");
            }
        }
        pokemonTypesText.setText(types.toString());

        // Habilidades
        StringBuilder abilities = new StringBuilder("Habilidades: ");
        if (pokemon.getAbilities() != null) {
            for (int i = 0; i < pokemon.getAbilities().size(); i++) {
                abilities.append(capitalizeFirst(pokemon.getAbilities().get(i).getAbility().getName()));
                if (i < pokemon.getAbilities().size() - 1) abilities.append(", ");
            }
        }
        pokemonAbilitiesText.setText(abilities.toString());

        // Estadísticas
        StringBuilder stats = new StringBuilder("Estadísticas:\n");
        if (pokemon.getStats() != null) {
            for (Pokemon.Stat stat : pokemon.getStats()) {
                stats.append("• ").append(capitalizeFirst(stat.getStat().getName()))
                        .append(": ").append(stat.getBase_stat()).append("\n");
            }
        }
        pokemonStatsText.setText(stats.toString());

        // Imagen
        if (pokemon.getSprites() != null && pokemon.getSprites().getFront_default() != null) {
            loadPokemonImage(pokemon.getSprites().getFront_default());
        }

        // Configurar spinner de movimientos (simulado)
        setupMoveSpinner();
    }

    private void loadPokemonImage(String imageUrl) {
        if (imageUrl != null) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.pokeball_placeholder)
                    .error(R.drawable.pokeball_placeholder)
                    .into(pokemonImageView);
        }
    }

    private void setupMoveSpinner() {
        String[] moves = {"Tackle", "Thunder Shock", "Quick Attack", "Growl", "Tail Whip"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, moves);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moveSpinner.setAdapter(adapter);
    }

    private void setupVideoView() {
        try {
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.pokemon_battle);
            pokemonVideoView.setVideoURI(videoUri);
            pokemonVideoView.setVisibility(View.VISIBLE);
            pokemonVideoView.start();

            pokemonVideoView.setOnCompletionListener(mp -> {
                Toast.makeText(this, "¡Video completado!", Toast.LENGTH_SHORT).show();
            });
        } catch (Exception e) {
            Toast.makeText(this, "Error al reproducir video", Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoading(boolean show) {
        loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        contentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}