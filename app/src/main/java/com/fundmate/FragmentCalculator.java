package com.fundmate;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.fragment.app.Fragment;

public class FragmentCalculator extends Fragment {
    private EditText display;
    private String currentInput = "";
    private String lastInput = "";
    private String operator = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_calculator, container, false);
        display = rootView.findViewById(R.id.display);

        setButtonListeners(rootView);

        return rootView;
    }

    private void setButtonListeners(View rootView) {
        // Numbers
        rootView.findViewById(R.id.btn0).setOnClickListener(v -> appendToDisplay("0"));
        rootView.findViewById(R.id.btn1).setOnClickListener(v -> appendToDisplay("1"));
        rootView.findViewById(R.id.btn2).setOnClickListener(v -> appendToDisplay("2"));
        rootView.findViewById(R.id.btn3).setOnClickListener(v -> appendToDisplay("3"));
        rootView.findViewById(R.id.btn4).setOnClickListener(v -> appendToDisplay("4"));
        rootView.findViewById(R.id.btn5).setOnClickListener(v -> appendToDisplay("5"));
        rootView.findViewById(R.id.btn6).setOnClickListener(v -> appendToDisplay("6"));
        rootView.findViewById(R.id.btn7).setOnClickListener(v -> appendToDisplay("7"));
        rootView.findViewById(R.id.btn8).setOnClickListener(v -> appendToDisplay("8"));
        rootView.findViewById(R.id.btn9).setOnClickListener(v -> appendToDisplay("9"));

        // Operators
        rootView.findViewById(R.id.btnPlus).setOnClickListener(v -> setOperator("+"));
        rootView.findViewById(R.id.btnMinus).setOnClickListener(v -> setOperator("-"));
        rootView.findViewById(R.id.btnMultiply).setOnClickListener(v -> setOperator("*"));
        rootView.findViewById(R.id.btnDivide).setOnClickListener(v -> setOperator("/"));

        // Other buttons
        rootView.findViewById(R.id.btnClear).setOnClickListener(v -> clear());
        rootView.findViewById(R.id.btnEquals).setOnClickListener(v -> calculateResult());
    }

    private void appendToDisplay(String number) {
        currentInput += number;
        display.setText(currentInput);
    }

    private void setOperator(String operator) {
        if (!TextUtils.isEmpty(currentInput)) {
            lastInput = currentInput;
            currentInput = "";
            this.operator = operator;
        }
    }

    private void clear() {
        currentInput = "";
        lastInput = "";
        operator = "";
        display.setText("");
    }

    private void calculateResult() {
        if (!TextUtils.isEmpty(lastInput) && !TextUtils.isEmpty(currentInput)) {
            double num1 = Double.parseDouble(lastInput);
            double num2 = Double.parseDouble(currentInput);
            double result = 0;

            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 != 0) {
                        result = num1 / num2;
                    } else {
                        display.setText("Error");
                        return;
                    }
                    break;
            }

            display.setText(String.valueOf(result));
            currentInput = String.valueOf(result); // Store result for further calculations
            lastInput = "";
            operator = "";
        }
    }
}
