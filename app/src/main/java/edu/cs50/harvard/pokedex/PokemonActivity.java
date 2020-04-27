package edu.cs50.harvard.pokedex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class PokemonActivity extends AppCompatActivity {

    private TextView nameTextView;
    private TextView numberTextView;
    private TextView type1TextView;
    private TextView type2TextView;
    private String url;
    private RequestQueue requestQueue;
    private Boolean caught;
    private Button buttonView;
    private String currentPokemonName;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        url = getIntent().getStringExtra("url");
        currentPokemonName = getIntent().getStringExtra("currentPokemon");


        nameTextView = findViewById(R.id.pokemon_name);
        numberTextView = findViewById(R.id.pokemon_number);
        type1TextView = findViewById(R.id.pokemon_type1);
        type2TextView = findViewById(R.id.pokemon_type2);
        buttonView = findViewById(R.id.catchButton);
        imageView = findViewById(R.id.pokeImg);

        load();
    }


    public void load() {
        type1TextView.setText("");
        type2TextView.setText("");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String name = response.getString("name");
                    JSONObject sprites = response.getJSONObject("sprites");
                    String imageUrl = sprites.getString("front_default");

                    nameTextView.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
                    numberTextView.setText(String.format("#%03d", response.getInt("id")));


                    JSONArray typeEntries =  response.getJSONArray("types");
                    for (int i = 0; i < typeEntries.length(); i++) {
                        JSONObject typeEntry = typeEntries.getJSONObject(i);
                        int slot = typeEntry.getInt("slot");
                        String type = typeEntry.getJSONObject("type").getString("name");

                        if (slot == 1) {
                            type1TextView.setText(type);
                        }
                        else if (slot == 2) {
                            type2TextView.setText(type);
                        }
                    }
                    new DownloadSpriteTask().execute(imageUrl);

                } catch (JSONException e) {
                    Log.e("cs50", "Pokemon json error", e);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("cs50", "json error");
            }
        });
        requestQueue.add(request);
        checkPreferences();
    }

    public void toggleCatch(View view) {
        if (caught) {
            caught = false;
            buttonView.setText("Catch!");
            getPreferences(Context.MODE_PRIVATE).edit().putBoolean(currentPokemonName, false).commit();
        }
        else {
            caught = true;
            buttonView.setText("Release!");
            getPreferences(Context.MODE_PRIVATE).edit().putBoolean(currentPokemonName, true).commit();
        }
    }


    private void checkPreferences() {
        caught = getPreferences(Context.MODE_PRIVATE).getBoolean(currentPokemonName, false);


        if (caught == true) {
            buttonView.setText("Release!");
        }
        else if (!caught) {
            buttonView.setText("Catch!");
        }

    }


    private class DownloadSpriteTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return BitmapFactory.decodeStream(url.openStream());
            }
            catch (IOException e) {
                Log.e("cs50", "Download sprite error", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }

}
