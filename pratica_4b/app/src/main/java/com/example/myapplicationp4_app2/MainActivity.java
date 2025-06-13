package com.example.myapplicationp4_app2;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    private float luz = -1;
    private float proximidade = -1;

    public Button btnResponder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btnResponder = findViewById(R.id.btnResponder);
        Intent it = getIntent();

        float luz = it.getFloatExtra("luminosidade", -1);
        float proximidade = it.getFloatExtra("proximidade", -1);

        btnResponder.setOnClickListener(v -> {

            if (luz != -1 && proximidade != -1) {
                String classificacaoLuz = (luz < 20) ? "baixa" : "alta";
                String classificacaoProximidade = (proximidade > 3) ? "distante" : "perto";

                Intent resposta = new Intent();
                resposta.putExtra("classificacao_luz", classificacaoLuz);
                resposta.putExtra("classificacao_proximidade", classificacaoProximidade);
                setResult(1, resposta);
            }

            finish();

        });

    }

}
