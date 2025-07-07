package com.example.myapplicationpratica5;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class Mapa extends AppCompatActivity {

    private GoogleMap mapa;
    private DataBaseSingleton db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        db = DataBaseSingleton.getInstance(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapa);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::carregarCheckinsNoMapa);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.menu_voltar, menu);
        return true;
    }

    private void carregarCheckinsNoMapa(GoogleMap googleMap) {
        this.mapa = googleMap;

        List<String> categorias = carregarCategorias();

        Cursor c = db.search("Checkin",
                new String[]{"Local", "latitude", "longitude", "cat", "qtdVisitas"},
                null,
                null,
                null);

        if (c != null && c.moveToFirst()) {
            do {
                double lat = Double.parseDouble(c.getString(c.getColumnIndexOrThrow("latitude")));
                double lng = Double.parseDouble(c.getString(c.getColumnIndexOrThrow("longitude")));
                LatLng posicao = new LatLng(lat, lng);

                int categoriaIndex = c.getInt(c.getColumnIndexOrThrow("cat")) - 1;
                String nomeCategoria = (categoriaIndex >= 0 && categoriaIndex < categorias.size())
                        ? categorias.get(categoriaIndex)
                        : "Desconhecida";

                String titulo = c.getString(c.getColumnIndexOrThrow("Local"));
                int visitas = c.getInt(c.getColumnIndexOrThrow("qtdVisitas"));
                String snippet = "Categoria: " + nomeCategoria + " | Visitas: " + visitas;

                mapa.addMarker(new MarkerOptions()
                        .position(posicao)
                        .title(titulo)
                        .snippet(snippet));
            } while (c.moveToNext());
            c.close();
        }
    }

    private List<String> carregarCategorias() {
        List<String> categorias = new ArrayList<>();

        Cursor c = db.search("Categoria", new String[]{"nome"}, null, null, "idCategoria ASC");

        if (c != null && c.moveToFirst()) {
            do {
                categorias.add(c.getString(c.getColumnIndexOrThrow("nome")));
            } while (c.moveToNext());
            c.close();
        }

        return categorias;
    }
}
