package com.example.myapplicationpratica5;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;

public class Relatorio extends AppCompatActivity {

    private DataBaseSingleton db;
    private LinearLayout layoutLocais, layoutQtds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio);

        db = DataBaseSingleton.getInstance(this);

        layoutLocais = findViewById(R.id.lista_local);
        layoutQtds = findViewById(R.id.lista_qtds);

        carregarRelatorio();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.menu_voltar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent it = new Intent(this, MainActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(it);
        return true;
    }

    private void carregarRelatorio() {
        Cursor c = db.search("Checkin", new String[]{"Local", "qtdVisitas"}, null, null, "qtdVisitas DESC");

        if (c != null && c.moveToFirst()) {
            do {
                String nomeLocal = c.getString(c.getColumnIndexOrThrow("Local"));
                String qtdVisitas = c.getString(c.getColumnIndexOrThrow("qtdVisitas"));

                layoutLocais.addView(criarTextView(nomeLocal));
                layoutQtds.addView(criarTextView(qtdVisitas));
            } while (c.moveToNext());

            c.close();
        }
    }

    private TextView criarTextView(String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextColor(Color.WHITE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        tv.setLayoutParams(params);

        return tv;
    }
}
