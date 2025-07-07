package com.example.myapplicationpratica5;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class GestaoMapa extends AppCompatActivity {

    private DataBaseSingleton db;
    private LinearLayout listaLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestaomapa);

        db = DataBaseSingleton.getInstance(this);
        listaLayout = findViewById(R.id.lista);

        carregarListaCheckins();
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

    private void carregarListaCheckins() {
        Cursor c = db.search("Checkin", new String[]{"Local", "qtdVisitas"}, null, null, "qtdVisitas DESC");

        if (c != null && c.moveToFirst()) {
            do {
                String nomeLocal = c.getString(c.getColumnIndexOrThrow("Local"));
                listaLayout.addView(criarLinhaCheckin(nomeLocal));
            } while (c.moveToNext());
            c.close();
        }
    }

    private LinearLayout criarLinhaCheckin(String nomeLocal) {
        LinearLayout linha = new LinearLayout(this);
        linha.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams linhaParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        linhaParams.setMargins(0, 0, 0, 32);
        linha.setLayoutParams(linhaParams);

        TextView localText = new TextView(this);
        localText.setText(nomeLocal);
        localText.setTextColor(Color.WHITE);

        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        localText.setLayoutParams(textParams);
        linha.addView(localText);

        ImageButton botaoExcluir = new ImageButton(this);
        botaoExcluir.setImageResource(android.R.drawable.ic_delete);
        botaoExcluir.setBackgroundColor(Color.TRANSPARENT);

        botaoExcluir.setOnClickListener(v -> confirmarExclusao(nomeLocal));
        linha.addView(botaoExcluir);

        return linha;
    }

    private void confirmarExclusao(String nomeLocal) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmação")
                .setMessage("Deseja excluir o local \"" + nomeLocal + "\"?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    db.delete("Checkin", "Local = ?", new String[]{ nomeLocal });
                    reiniciarTela();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }


    private void reiniciarTela() {
        finish();
        startActivity(getIntent());
    }
}
