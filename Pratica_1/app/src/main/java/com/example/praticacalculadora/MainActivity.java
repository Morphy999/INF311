package com.example.praticacalculadora;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button clearButton, divisionButton, multiplyButton, negativeButton, plusButton, equalButton, commaButton, zeroButton,  oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton, sevenButton, eightButton, nineButton;
    private TextView txtExpression, txtResult;
    private ImageView backspaceButton;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        init_components();

        zeroButton.setOnClickListener(this);
        oneButton.setOnClickListener(this);
        twoButton.setOnClickListener(this);
        threeButton.setOnClickListener(this);
        fourButton.setOnClickListener(this);
        fiveButton.setOnClickListener(this);
        sixButton.setOnClickListener(this);
        sevenButton.setOnClickListener(this);
        eightButton.setOnClickListener(this);
        nineButton.setOnClickListener(this);
        commaButton.setOnClickListener(this);
        plusButton.setOnClickListener(this);
        negativeButton.setOnClickListener(this);
        multiplyButton.setOnClickListener(this);
        divisionButton.setOnClickListener(this);


        clearButton.setOnClickListener(v -> {
            txtExpression.setText(" ");
            txtResult.setText(" ");
        });

        backspaceButton.setOnClickListener(v -> {
            TextView expression = findViewById(R.id.txt_expression);
            String string = expression.getText().toString();
            if (!string.isEmpty()) {
                expression.setText(string.substring(0, string.length() - 1));
            }
        });

        equalButton.setOnClickListener(v -> {
            try {
                Expression expression = new ExpressionBuilder(txtExpression.getText().toString()).build();

                double result = expression.evaluate();

                if (Double.isInfinite(result) || Double.isNaN(result)) {
                    txtResult.setText("ERROR");
                } else {
                    txtResult.setText(String.valueOf(result));
                }
            } catch (Exception e) {

                txtResult.setText("ERROR");
            }
        });
    }


    private void init_components(){
        clearButton = findViewById(R.id.clear_button);
        divisionButton = findViewById(R.id.division_button);
        multiplyButton = findViewById(R.id.multiply_button);
        negativeButton = findViewById(R.id.negative_button);
        plusButton = findViewById(R.id.plus_button);
        equalButton = findViewById(R.id.equal_button);
        commaButton = findViewById(R.id.comma_button);
        zeroButton = findViewById(R.id.number_zero_button);
        oneButton = findViewById(R.id.number_one_button);
        twoButton = findViewById(R.id.number_two_button);
        threeButton = findViewById(R.id.number_three_button);
        fourButton = findViewById(R.id.number_four_button);
        fiveButton = findViewById(R.id.number_five_button);
        sixButton = findViewById(R.id.number_six_button);
        sevenButton = findViewById(R.id.number_seven_button);
        eightButton = findViewById(R.id.number_eight_button);
        nineButton = findViewById(R.id.number_nine_button);
        backspaceButton = findViewById(R.id.backspace_button);
        txtExpression = findViewById(R.id.txt_expression);
        txtResult = findViewById(R.id.txt_result);
    }

    private void AddExpression(String string, boolean canClear) {

        if (txtResult.getText().equals("")){
            txtExpression.setText(" ");
        }

        if (canClear) {
            txtResult.setText(" ");
            txtExpression.append(string);
        }
        else{
            txtExpression.append(txtResult.getText());
            txtExpression.append(string);
            txtResult.setText(" ");

        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.number_zero_button) {
            AddExpression("0", true);
        } else if (view.getId() == R.id.number_one_button) {
            AddExpression("1", true);
        } else if (view.getId() == R.id.number_two_button) {
            AddExpression("2", true);
        } else if (view.getId() == R.id.number_three_button) {
            AddExpression("3", true);
        } else if (view.getId() == R.id.number_four_button) {
            AddExpression("4", true);
        } else if (view.getId() == R.id.number_five_button) {
            AddExpression("5", true);
        } else if (view.getId() == R.id.number_six_button) {
            AddExpression("6", true);
        } else if (view.getId() == R.id.number_seven_button) {
            AddExpression("7", true);
        } else if (view.getId() == R.id.number_eight_button) {
            AddExpression("8", true);
        } else if (view.getId() == R.id.number_nine_button) {
            AddExpression("9", true);
        } else if (view.getId() == R.id.plus_button){
            AddExpression("+", false);
        } else if (view.getId() == R.id.negative_button) {
            AddExpression("-", false);
        } else if (view.getId() == R.id.multiply_button) {
            AddExpression("*", false);
        } else if (view.getId() == R.id.division_button) {
            AddExpression("/", false);
        }
        else if (view.getId() == R.id.comma_button) {
            AddExpression(",", true);
        }
    }
}