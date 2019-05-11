package com.cyclicsoft.com.acitivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.cyclicsoft.com.R;

import java.util.Calendar;

public class DeveloperInfoActivity extends AppCompatActivity {

    TextView txt_date,txt_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_developer_info);

    }
}
