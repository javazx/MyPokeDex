package com.valeriano.mypokedex;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import com.valeriano.mypokedex.PokemonDetailActivity;
import com.valeriano.mypokedex.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoritesFragment extends Fragment {
    private ListView favoritesListView;
    private ArrayAdapter<String> adapter;
    private List<String> favoritesPokemon;
    private TextView emptyText;
    private Button addFavoriteButton;
    private EditText addFavoriteEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        initializeViews(view);
        setupListView();
        setupListeners();

        return view;
    }

    private void initializeViews(View view) {
        favoritesListView = view.findViewById(R.id.favorites_list_view);
        emptyText = view.findViewById(R.id.empty_text);
        addFavoriteButton = view.findViewById(R.id.add_favorite_button);
        addFavoriteEditText = view.findViewById(R.id.add_favorite_edit_text);
    }

    private void setupListView() {
        // Algunos favoritos de ejemplo
        favoritesPokemon = new ArrayList<>(Arrays.asList(
                "Pikachu (#25)", "Charizard (#6)", "Mewtwo (#150)"
        ));

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, favoritesPokemon);
        favoritesListView.setAdapter(adapter);

        updateEmptyState();

        favoritesListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedPokemon = favoritesPokemon.get(position);
            // Extraer nombre del Pokémon (antes del paréntesis)
            String pokemonName = selectedPokemon.split(" \\(")[0].toLowerCase();

            Intent intent = new Intent(getContext(), PokemonDetailActivity.class);
            intent.putExtra("pokemon_name", pokemonName);
            startActivity(intent);
        });

        favoritesListView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Eliminar de favoritos con long click
            String removedPokemon = favoritesPokemon.remove(position);
            adapter.notifyDataSetChanged();
            updateEmptyState();
            Toast.makeText(getContext(), removedPokemon + " eliminado de favoritos",
                    Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void setupListeners() {
        addFavoriteButton.setOnClickListener(v -> {
            String pokemonName = addFavoriteEditText.getText().toString().trim();
            if (!pokemonName.isEmpty()) {
                String formattedName = capitalizeFirst(pokemonName) + " (Favorito)";
                favoritesPokemon.add(formattedName);
                adapter.notifyDataSetChanged();
                updateEmptyState();
                addFavoriteEditText.setText("");
                Toast.makeText(getContext(), "¡" + pokemonName + " agregado a favoritos!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Ingresa el nombre de un Pokémon",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (favoritesPokemon.isEmpty()) {
            emptyText.setVisibility(View.VISIBLE);
            favoritesListView.setVisibility(View.GONE);
        } else {
            emptyText.setVisibility(View.GONE);
            favoritesListView.setVisibility(View.VISIBLE);
        }
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}