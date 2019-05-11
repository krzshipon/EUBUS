package com.cyclicsoft.com.acitivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.cyclicsoft.com.R;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Button adminRegBtn;
    TextView mDateView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adminRegBtn = (Button)findViewById(R.id.adminRegBtn);
//        Function for Date View
//        Thread t = new Thread() {
//
//            @Override
//            public void run() {
//                try {
//                    while (!isInterrupted()) {
//                        Thread.sleep(1000);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mDateView = (TextView)findViewById(R.id.dateViewId);
//                                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//                                mDateView.setText(currentDateTimeString);
//                            }
//                        });
//                    }
//                } catch (InterruptedException e) {
//                }
//            }
//        };
//        t.start();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id==R.id.studentViewId){
            Intent studentViewIntent = new Intent(this,FindBusActivity.class);
            this.startActivity(studentViewIntent);
            return true;
        }

        if(id==R.id.adminViewId){
            Intent adminViewIntent = new Intent(this,AdminActivity.class);
            this.startActivity(adminViewIntent);
            return true;
        }
        if(id==R.id.studentRegId){
            return true;
        }

        if(id==R.id.adminRegId){
            Intent adminRegIntent = new Intent(this,AdminRegActivity.class);
            this.startActivity(adminRegIntent);
            return true;
        }
        if(id==R.id.developerInfoId){
            Intent adminViewIntent = new Intent(this,DeveloperInfoActivity.class);
            this.startActivity(adminViewIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
