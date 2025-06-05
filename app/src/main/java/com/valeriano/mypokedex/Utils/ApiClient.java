package com.valeriano.mypokedex.utils;

import com.google.gson.Gson;
import com.valeriano.mypokedex.models.Pokemon;
import okhttp3.*;
import java.io.IOException;

public class ApiClient {
    private static final String BASE_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public interface PokemonCallback {
        void onSuccess(Pokemon pokemon);
        void onError(String error);
    }

    public static void getPokemon(String pokemonName, PokemonCallback callback) {
        String url = BASE_URL + pokemonName.toLowerCase();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Error de conexión: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        Pokemon pokemon = gson.fromJson(responseBody, Pokemon.class);
                        callback.onSuccess(pokemon);
                    } catch (Exception e) {
                        callback.onError("Error al procesar datos: " + e.getMessage());
                    }
                } else {
                    callback.onError("Pokémon no encontrado");
                }
            }
        });
    }

    public static void getPokemonById(int id, PokemonCallback callback) {
        getPokemon(String.valueOf(id), callback);
    }
}