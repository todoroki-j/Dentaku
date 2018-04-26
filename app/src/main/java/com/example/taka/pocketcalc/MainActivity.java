package com.example.taka.dentaku;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    private static final int maxKeta = 15;
    private static final double maxDouble =  999999999999999.0;

    private static final String OVERFLOW = "Overflow (Push AC)";
    private static final String ZERODIV  = "Div by 0 (Push AC)";
    private String operand1 = "";
    private String operator = "";
    private boolean firstNum = true;

    private TextView textViewDisplay;
    private ClipboardManager clipboard;

    @SuppressLint("SetTextI18n")
    private void process(String buttonStr) {
        String display = textViewDisplay.getText().toString();
        Log.d("INFO", "buttonStr="+buttonStr+", operand1="+operand1+", operator="+operator+", display="+display);
        if (display.equals(OVERFLOW) || display.equals(ZERODIV)) {
            if (!buttonStr.equals("AC")) {
                return;
            }
        }

        long displayNum;
        switch(buttonStr) {
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                if (firstNum) {
                    if (!operator.equals("")) {
                        operand1 =  display;
                    }
                    display = "0";
                    firstNum = false;
                }
                if (display.length() >= maxKeta) {
                    textViewDisplay.setText(OVERFLOW);
                    break;
                }
                displayNum = Long.parseLong(display);
                long buttonNum = Long.parseLong(buttonStr);
                displayNum = displayNum * 10 + buttonNum;
                textViewDisplay.setText(Long.toString(displayNum));
                break;

            case "+":
            case "-":
            case "*":
            case "/":
            case "=":
            case "%":
                if (!operator.equals("") && !operand1.equals("") && !firstNum) {
                    long operand1Long = Long.parseLong(operand1);
                    long operand2Long = Long.parseLong(display);
                    double operand1Double = (double)operand1Long;
                    double operand2Double = (double)operand2Long;
                    switch(operator) {
                        case "+":
                            if (Math.abs(operand1Double + operand2Double) > maxDouble) {
                                textViewDisplay.setText(OVERFLOW);
                            } else {
                                textViewDisplay.setText(Long.toString(operand1Long + operand2Long));
                            }
                            break;
                        case "-":
                            if (Math.abs(operand1Double - operand2Double) > maxDouble) {
                                textViewDisplay.setText(OVERFLOW);
                            } else {
                                textViewDisplay.setText(Long.toString(operand1Long - operand2Long));
                            }
                            break;
                        case "*":
                            if (Math.abs(operand1Double * operand2Double) > maxDouble) {
                                textViewDisplay.setText(OVERFLOW);
                            } else {
                                textViewDisplay.setText(Long.toString(operand1Long * operand2Long));
                            }
                            break;
                        case "/":
                            if (operand2Long == 0L) {
                                textViewDisplay.setText(ZERODIV);
                            } else {
                                textViewDisplay.setText(Long.toString(operand1Long / operand2Long));
                            }
                            break;
                        case "%":
                            if (operand2Long == 0L) {
                                textViewDisplay.setText(ZERODIV);
                            } else {
                                textViewDisplay.setText(Long.toString(operand1Long % operand2Long));
                            }
                            break;
                        default:
                            Log.d("ERROR", "operatorが識別できない：" + operator);
                    }
                }
                if (buttonStr.equals("=")) {
                    operator = "";
                } else {
                    operator = buttonStr;
                }
                firstNum = true;
                break;

            case "←":
                displayNum = Long.parseLong(display);
                displayNum = displayNum / 10;
                textViewDisplay.setText(Long.toString(displayNum));
                break;

            case "±":
                displayNum = Long.parseLong(display);
                displayNum = -displayNum;
                textViewDisplay.setText(Long.toString(displayNum));
                break;

            case "C":
                textViewDisplay.setText("0");
                break;

            case "AC":
                textViewDisplay.setText("0");
                operand1 = "";
                operator = "";
                break;

            default:
                Log.d("ERROR", "識別できないボタンが押された：" + buttonStr);
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewDisplay = (TextView) findViewById(R.id.textViewDisplay);
        int[] buttonIDs = {R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
                            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9,
                            R.id.buttonBS, R.id.buttonC, R.id.buttonAC, R.id.buttonAdd, R.id.buttonSub,
                            R.id.buttonMul, R.id.buttonEq, R.id.buttonDiv, R.id.buttonMod, R.id.buttonSgn};
        for (int buttonID : buttonIDs) {
            Button button = (Button)findViewById(buttonID);
            if (button == null) {
                Log.d("ERROR", "button == null");
            } else {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Button button = (Button) v;
                        String buttonStr = button.getText().toString();
                        process(buttonStr);
                    }
                });
            }
        }

        clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        Button buttonCopy = (Button)findViewById(R.id.buttonCopy);
        if (buttonCopy == null) {
            Log.d("ERROR", "buttonCopy == null");
        } else {
            buttonCopy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipData clip = ClipData.newPlainText("copied_text", textViewDisplay.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }
            });
        }

        if (savedInstanceState != null) {
            textViewDisplay.setText(savedInstanceState.getString("display"));
            operand1 = savedInstanceState.getString("operand1");
            operator = savedInstanceState.getString("operator");
            firstNum = savedInstanceState.getBoolean("firstNum");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("display",  textViewDisplay.getText().toString());
        outState.putString("operand1", operand1);
        outState.putString("operator", operator);
        outState.putBoolean("firstNum", firstNum);
    }
}
