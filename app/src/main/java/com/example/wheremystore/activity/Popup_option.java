package com.example.wheremystore.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.CheckBox;

import androidx.core.content.ContextCompat;

import com.example.wheremystore.R;
import com.example.wheremystore.item.Sp;

public class Popup_option extends Activity {
    CheckBox type01, type02, type03, type04, type05, type06, type07;
    Sp sp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_option);

        sp = new Sp(Popup_option.this);

        type01 = findViewById(R.id.popup_option_type01);
        type02 = findViewById(R.id.popup_option_type02);
        type03 = findViewById(R.id.popup_option_type03);
        type04 = findViewById(R.id.popup_option_type04);
        type05 = findViewById(R.id.popup_option_type05);
        type06 = findViewById(R.id.popup_option_type06);
        type07 = findViewById(R.id.popup_option_type07);

        type01.setOnClickListener(v -> textColorChange(type01));
        type02.setOnClickListener(v -> textColorChange(type02));
        type03.setOnClickListener(v -> textColorChange(type03));
        type04.setOnClickListener(v -> textColorChange(type04));
        type05.setOnClickListener(v -> textColorChange(type05));
        type06.setOnClickListener(v -> textColorChange(type06));
        type07.setOnClickListener(v -> textColorChange(type07));

        findViewById(R.id.popup_option_okBtn).setOnClickListener(v -> register());
        findViewById(R.id.popup_option_cancelBtn).setOnClickListener(v -> finish());

        setLayout();

    }

    private void setLayout() {
        String option = sp.getOption();

        for(int i=0; i<option.length(); i++) {
            String value = option.substring(i, i+1);
            boolean booleanValue = value.equals("1");

            switch (i) {
                case 0 :
                    type01.setChecked(booleanValue);
                    textColorChange(type01);
                    break;
                case 1 :
                    type02.setChecked(booleanValue);
                    textColorChange(type02);
                    break;
                case 2 :
                    type03.setChecked(booleanValue);
                    textColorChange(type03);
                    break;
                case 3 :
                    type04.setChecked(booleanValue);
                    textColorChange(type04);
                    break;
                case 4 :
                    type05.setChecked(booleanValue);
                    textColorChange(type05);
                    break;
                case 5 :
                    type06.setChecked(booleanValue);
                    textColorChange(type06);
                    break;
                case 6 :
                    type07.setChecked(booleanValue);
                    textColorChange(type07);
                    break;
            }
        }
    }

    private void register() {
        StringBuilder option = null;
        for(int i=0; i<7; i++) {
            switch (i) {
                case 0 :
                    if(type01.isChecked()) {
                        option = new StringBuilder("1");
                    } else {
                        option = new StringBuilder("0");
                    }
                    break;
                case 1 :
                    if(type02.isChecked()) {
                        option.append("1");
                    } else {
                        option.append("0");
                    }
                    break;
                case 2 :
                    if(type03.isChecked()) {
                        option.append("1");
                    } else {
                        option.append("0");
                    }
                    break;
                case 3 :
                    if(type04.isChecked()) {
                        option.append("1");
                    } else {
                        option.append("0");
                    }
                    break;
                case 4 :
                    if(type05.isChecked()) {
                        option.append("1");
                    } else {
                        option.append("0");
                    }
                    break;
                case 5 :
                    if(type06.isChecked()) {
                        option.append("1");
                    } else {
                        option.append("0");
                    }
                    break;
                case 6 :
                    if(type07.isChecked()) {
                        option.append("1");
                    } else {
                        option.append("0");
                    }
                    break;
            }

        }

        sp.setOption(option.toString());
        setResult(RESULT_OK);
        finish();
    }

    @SuppressLint("ResourceAsColor")
    private void textColorChange(CheckBox cb) {
        if(cb.isChecked()) {
            // 이 형식은 작동 안됨 > setTextColor(R.color.white); 아래처럼 작동시켜야됨
            cb.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            cb.setTextColor(ContextCompat.getColor(this, R.color.black));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }

}