package com.example.myapplicationimcpapai;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.content.Intent;

public class ReportScreen extends Activity {

    private TextView classificationField;
    private TextView imcField;
    private TextView nameField;
    private TextView ageField;
    private TextView weightField;
    private TextView heightField;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        initializeFields();
        Intent intent = getIntent();
        double imc = intent.getDoubleExtra("imc", 0.0);
        String name = intent.getStringExtra("name");
        int age = intent.getIntExtra("age", 0);
        double weight = intent.getDoubleExtra("weight", 0.0);
        double height = intent.getDoubleExtra("height", 0.0);
        String classification = intent.getStringExtra("imc_classification");

        classificationField.setText("Classificação IMC: " + (classification != null ? classification : ""));
        imcField.setText("IMC: " + String.format("%.2f", imc) +" kg/m²");
        nameField.setText("Nome: " + (name != null ? name : ""));
        ageField.setText("Idade: " + age + " anos");
        weightField.setText("Peso: " + String.format("%.2f", weight) + " kg");
        heightField.setText("Altura: " + String.format("%.2f", height) + " m");

        disableFields();
    }

    private void initializeFields() {
        imcField = findViewById(R.id.imc_label);
        weightField = findViewById(R.id.weight_label);
        heightField = findViewById(R.id.height_label);
        classificationField = findViewById(R.id.imc_classification_label);
        nameField = findViewById(R.id.name_label);
        ageField = findViewById(R.id.age_label);
    }

    private void disableFields() {
        classificationField.setEnabled(false);
        imcField.setEnabled(false);
        nameField.setEnabled(false);
        ageField.setEnabled(false);
        weightField.setEnabled(false);
        heightField.setEnabled(false);
    }

    public void onBackButtonPressed(View view) {
        finish();
    }
}
