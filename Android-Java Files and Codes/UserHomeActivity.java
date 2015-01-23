package com.example.ankit.smartattendancesystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class UserHomeActivity extends ActionBarActivity {
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = sharedPreferences.getString("username","mishra14");
        ((TextView)findViewById(R.id.message)).setText("Welcome "+username+ "\nPlease select an option - ");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void viewGroups(View view)
    {
        Intent intent = new Intent(getApplicationContext(),ViewGroupsActivity.class);
        startActivity(intent);
    }

    public void joinGroup(View view)
    {
        Intent intent = new Intent(getApplicationContext(),JoinGroupActivity.class);
        startActivity(intent);
    }

    public void createGroup(View view)
    {
        Intent intent = new Intent(getApplicationContext(),CreateGroupActivity.class);
        startActivity(intent);
    }
    public void quit(View view)
    {
        stopService(new Intent(getApplicationContext(), LocationService.class));
        finish();
    }
    public void exit(View view)
    {
        finish();
    }
}
