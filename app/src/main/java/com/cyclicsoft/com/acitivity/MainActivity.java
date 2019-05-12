package com.cyclicsoft.com.acitivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.cyclicsoft.com.R;

public class MainActivity extends AppCompatActivity {

    // For Displaying Date and Time
    TextView mDateView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Call for initialization
        initFields();

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

    /**
     * Initialize all UI View & field variable
     */
    private void initFields() {

    }

    /**
     * For inflating menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }


    /**
     * Selection Menu IN Main Activity
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.studentViewId:
                Intent studentViewIntent = new Intent(this,FindBusActivity.class);
                this.startActivity(studentViewIntent);
                return true;
            case R.id.adminViewId:
                Intent adminViewIntent = new Intent(this, AdminLoginActivity.class);
                this.startActivity(adminViewIntent);
                return true;
            case R.id.studentRegId:
                // do your code
                return true;
            case R.id.adminRegId:
                Intent adminRegIntent = new Intent(this,AdminRegActivity.class);
                this.startActivity(adminRegIntent);
                return true;
            case R.id.developerInfoId:
                Intent developerViewIntent = new Intent(this,DeveloperInfoActivity.class);
                this.startActivity(developerViewIntent);
                return true;
            case R.id.route2Bus1Id:
                // do your code
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
