package com.example.myapplicationimcpapai;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText ageInput;
    private EditText weightInput;
    private EditText heightInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameInput = findViewById(R.id.edit_name);
        ageInput = findViewById(R.id.age_edit);
        weightInput = findViewById(R.id.weight_edit);
        heightInput = findViewById(R.id.height_edit);
    }

    public void onGenerateReportClicked(View view) {
        String name = nameInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim();
        String weightStr = weightInput.getText().toString().trim();
        String heightStr = heightInput.getText().toString().trim();

        if (name.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty()) {
            return;
        }

        int age = Integer.parseInt(ageStr);
        double weight = Double.parseDouble(weightStr);
        double height = Double.parseDouble(heightStr);

        double imc = calculateIMC(weight, height);
        String imcClassification = classifyBMI(imc);

        Intent intent = new Intent(this, ReportScreen.class);
        intent.putExtra("name", name);
        intent.putExtra("age", age);
        intent.putExtra("weight", weight);
        intent.putExtra("height", height);
        intent.putExtra("imc", imc);
        intent.putExtra("imc_classification", imcClassification);
        startActivity(intent);
    }

    private double calculateIMC(double weight, double height) {
        return weight / (height * height);
    }

    private String classifyBMI(double imc) {
        if (imc < 18.5) return "Abaixo do Peso";
        else if (imc < 25) return "Saudável";
        else if (imc < 30) return "Sobrepeso";
        else if (imc < 35) return "Obesidade Grau I";
        else if (imc < 40) return "Obesidade Grau II (severa)";
        else return "Obesidade Grau III (mórbida)";
    }
}
