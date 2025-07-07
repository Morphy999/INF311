package com.example.myapplicationpratica5;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private DataBaseSingleton db;
    private LocationManager locationManager;
    private String provider;
    private LatLng currentLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = DataBaseSingleton.getInstance(this);
        setupLocationManager();
        loadCategories();
    }

    private void setupLocationManager() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        boolean hasGPS = getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        criteria.setAccuracy(hasGPS ? Criteria.ACCURACY_FINE : Criteria.ACCURACY_COARSE);

        provider = locationManager.getBestProvider(criteria, true);
        if (provider == null) {
            provider = LocationManager.GPS_PROVIDER;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(provider, 5000, 0, this);
        }
    }

    @Override
    protected void onDestroy() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_opcao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_mapa_checkin) {
            startActivity(new Intent(this, Mapa.class));
        } else if (id == R.id.menu_gerenciar_checkins) {
            startActivity(new Intent(this, GestaoMapa.class));
        } else if (id == R.id.menu_checkins_populares) {
            startActivity(new Intent(this, Relatorio.class));
        }
        return true;
    }

    private void loadCategories() {
        Spinner spinner = findViewById(R.id.spinner_categoria);
        ArrayList<String> categoryNames = new ArrayList<>();

        Cursor c = db.search("Categoria", new String[]{"nome"}, null, null, "idCategoria ASC");
        if (c != null && c.moveToFirst()) {
            do {
                categoryNames.add(c.getString(c.getColumnIndexOrThrow("nome")));
            } while (c.moveToNext());
            c.close();
        }

        if (categoryNames.isEmpty()) return;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
                String selected = (String) parent.getItemAtPosition(position);

                Cursor c = db.search(
                        "Categoria",
                        new String[]{"idCategoria"},
                        "nome = ?",
                        new String[]{selected},
                        null
                );

                if (c != null && c.moveToFirst()) {
                    int categoryId = c.getInt(c.getColumnIndexOrThrow("idCategoria"));
                    loadLocationsByCategory(categoryId);
                    c.close();
                }

                ((AutoCompleteTextView) findViewById(R.id.input_nome_local)).setText("");
                ((TextView) findViewById(R.id.valor_latitude)).setText("");
                ((TextView) findViewById(R.id.valor_longitude)).setText("");
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadLocationsByCategory(int categoryId) {
        AutoCompleteTextView autoComplete = findViewById(R.id.input_nome_local);
        ArrayList<String> locationNames = new ArrayList<>();

        Cursor c = db.search("Checkin", new String[]{"Local"}, "cat = ?", new String[]{String.valueOf(categoryId)}, null);

        if (c != null && c.moveToFirst()) {
            do {
                locationNames.add(c.getString(c.getColumnIndexOrThrow("Local")));
            } while (c.moveToNext());
            c.close();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, locationNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoComplete.setAdapter(adapter);

        autoComplete.setOnItemClickListener((parent, view, position, id) -> {
            String local = (String) parent.getItemAtPosition(position);
            Cursor cur = db.search("Checkin", new String[]{"latitude", "longitude"},
                    "Local = ?", new String[]{local}, null);

            if (cur != null && cur.moveToFirst()) {
                ((TextView) findViewById(R.id.valor_latitude)).setText(cur.getString(cur.getColumnIndexOrThrow("latitude")));
                ((TextView) findViewById(R.id.valor_longitude)).setText(cur.getString(cur.getColumnIndexOrThrow("longitude")));
                cur.close();
            }
        });
    }

    public void checkin(View v) {
        AutoCompleteTextView input = findViewById(R.id.input_nome_local);
        String nome = input.getText().toString().trim();
        String categoria = ((Spinner) findViewById(R.id.spinner_categoria)).getSelectedItem().toString();

        if (nome.isEmpty() || currentLocation == null) {
            return;
        }

        Cursor c = db.search("Checkin", new String[]{"qtdVisitas"}, "Local = ?", new String[]{nome}, null);
        if (c != null && c.moveToFirst()) {
            int visitas = c.getInt(c.getColumnIndexOrThrow("qtdVisitas"));
            ContentValues vals = new ContentValues();
            vals.put("qtdVisitas", visitas + 1);
            db.update("Checkin", vals, "Local = ?", new String[]{nome});
            c.close();
        } else {
            ContentValues vals = new ContentValues();
            vals.put("Local", nome);
            vals.put("qtdVisitas", 1);
            vals.put("latitude", String.valueOf(currentLocation.latitude));
            vals.put("longitude", String.valueOf(currentLocation.longitude));

            Cursor catCursor = db.search(
                    "Categoria",
                    new String[]{"idCategoria"},
                    "nome = ?",
                    new String[]{categoria},
                    null
            );
            if (catCursor != null && catCursor.moveToFirst()) {
                vals.put("cat", catCursor.getInt(catCursor.getColumnIndexOrThrow("idCategoria")));
                catCursor.close();
            }
            db.insert("Checkin", vals);
        }

        recreate();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
    }
}
