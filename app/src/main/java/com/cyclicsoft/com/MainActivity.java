package com.cyclicsoft.com;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.sql.Time;
import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView mDateView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setContentView(R.layout.activity_main);


        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mDateView = (TextView)findViewById(R.id.dateViewId);
                                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                mDateView.setText(currentDateTimeString);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();

//        mDateView = (TextView)findViewById(R.id.dateViewId);
//        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
//        mDateView.setText(currentDateTimeString);


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
