package com.valeriano.mypokedex;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.valeriano.mypokedex.PokemonDetailActivity;
import com.valeriano.mypokedex.R;
import com.valeriano.mypokedex.models.Pokemon;
import com.valeriano.mypokedex.utils.ApiClient;
import java.util.ArrayList;
import java.util.List;

public class PokemonListFragment extends Fragment {
    private ListView pokemonListView;
    private ArrayAdapter<String> adapter;
    private List<String> pokemonNames;
    private ProgressBar loadingProgressBar;
    private TextView statusText;
    private Handler mainHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pokemon_list, container, false);

        initializeViews(view);
        setupListView();
        loadInitialPokemon();

        mainHandler = new Handler(Looper.getMainLooper());

        return view;
    }

    private void initializeViews(View view) {
        pokemonListView = view.findViewById(R.id.pokemon_list_view);
        loadingProgressBar = view.findViewById(R.id.loading_progress_bar);
        statusText = view.findViewById(R.id.status_text);
    }

    private void setupListView() {
        pokemonNames = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, pokemonNames);
        pokemonListView.setAdapter(adapter);

        pokemonListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPokemon = pokemonNames.get(position);
            Log.d("Pokemon", "Pokémon seleccionado: " + selectedPokemon);
            Intent intent = new Intent(getContext(), PokemonDetailActivity.class);
            intent.putExtra("pokemon_name", selectedPokemon);
            startActivity(intent);
        });
    }

    private void loadInitialPokemon() {
        showLoading(true);
        statusText.setText("Cargando Pokémon populares...");

        // Lista de Pokémon populares para mostrar inicialmente
        String[] popularPokemon = {
                "pikachu", "charizard", "blastoise", "venusaur", "mewtwo",
                "mew", "lugia", "ho-oh", "rayquaza", "dialga",
                "palkia", "giratina", "arceus", "reshiram", "zekrom"
        };

        loadPokemonBatch(popularPokemon, 0);
    }

    private void loadPokemonBatch(String[] pokemonArray, int index) {
        if (index >= pokemonArray.length) {
            showLoading(false);
            statusText.setText("¡" + pokemonNames.size() + " Pokémon cargados!");
            return;
        }

        String pokemonName = pokemonArray[index];
        ApiClient.getPokemon(pokemonName, new ApiClient.PokemonCallback() {
            @Override
            public void onSuccess(Pokemon pokemon) {
                mainHandler.post(() -> {
                    String displayName = capitalizeFirst(pokemon.getName());
                    pokemonNames.add(displayName);
                    adapter.notifyDataSetChanged();

                    // Cargar siguiente Pokémon
                    loadPokemonBatch(pokemonArray, index + 1);
                });
            }

            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    // Continuar con el siguiente aunque falle uno
                    loadPokemonBatch(pokemonArray, index + 1);
                });
            }
        });
    }

    private void showLoading(boolean show) {
        if (loadingProgressBar != null) {
            loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}