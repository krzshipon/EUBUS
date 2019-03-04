package com.cyclicsoft.com;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class FindBusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_bus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu2,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id2 = item.getItemId();

        if(id2==R.id.findBusId){
//            Intent studentViewIntent = new Intent(this,FindBusActivity.class);
//            this.startActivity(studentViewIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
