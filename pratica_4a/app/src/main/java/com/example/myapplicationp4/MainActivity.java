package com.example.myapplicationp4;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.content.Context;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private Switch switchLanterna, switchVibracao;
    private LanternaHelper lanternaHelper;
    private MotorHelper motorHelper;
    private SensorManager sensorManager;
    private Sensor lSensor;
    private Sensor pSensor;
    private float valorLuzAtual = -1, valorProximidadeAtual = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        pSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);


        switchLanterna = findViewById(R.id.switchLanterna);
        switchVibracao = findViewById(R.id.switchVibracao);

        lanternaHelper = new LanternaHelper(this);
        motorHelper = new MotorHelper(this);


        if (lSensor != null) {
            sensorManager.registerListener(this, lSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        if (pSensor != null) {
            sensorManager.registerListener(this, pSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        Button btnClassificar = findViewById(R.id.btnClassificar);
        btnClassificar.setOnClickListener(view -> enviarLeiturasParaClassificacao());

    }

    private void enviarLeiturasParaClassificacao() {
        Intent intent = new Intent("APP_CLASSIFICACAO");
        intent.putExtra("luminosidade", valorLuzAtual);
        intent.putExtra("proximidade", valorProximidadeAtual);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int codReq, int codRes, Intent it) {

        super.onActivityResult(codReq, codRes, it);
        String classificacao_luz = it.getStringExtra("classificacao_luz");
        switch(classificacao_luz) {
            case "alta":
                switchLanterna.setChecked(false);
                lanternaHelper.desligar();
                break;
            case "baixa":
                switchLanterna.setChecked(true);
                lanternaHelper.ligar();
                break;
        }

        String classificacao_proximidade = it.getStringExtra("classificacao_proximidade");
        switch(classificacao_proximidade) {
            case "distante":
                switchVibracao.setChecked(false);
                motorHelper.pararVibracao();
                break;
            case "perto":
                switchVibracao.setChecked(true);
                motorHelper.iniciarVibracao();
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            valorLuzAtual = event.values[0];
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            valorProximidadeAtual = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(lSensor != null)
            sensorManager.registerListener(this, lSensor, SensorManager.SENSOR_DELAY_GAME);
        if(pSensor != null)
            sensorManager.registerListener(this, pSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lanternaHelper.desligar();
        motorHelper.pararVibracao();
    }
}