package com.example.ankit.smartattendancesystem;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class StartPageActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        checkStatus();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.start_page, menu);
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
    public void signup(View view)
    {
        checkStatus();
        if(checkLocation(this) && checkInternet(this)) {
            Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void login (View view)
    {
        checkStatus();
        if(checkLocation(this) && checkInternet(this)) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void checkStatus()
    {
        if (isMyServiceRunning(LocationService.class))
        {
            Toast.makeText(this, "Location Service is Running", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), UserHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "No Service Running", Toast.LENGTH_LONG).show();
        }
         if (checkInternet(this))
         {
                //Toast.makeText(this, "Internet Connection Available", Toast.LENGTH_LONG).show();
         }
         else
         {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Internet Connection not Available\nPlease enable Wi-Fi or Mobile Data to use this app");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Open Network Settings", new DialogInterface.OnClickListener()
                {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(myIntent);
                        //get gps
                    }
                });
                dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        System.exit(0);
                    }
                });
                dialog.show();
            }
            if (checkLocation(this))
            {
                //Toast.makeText(this, "Location Enabled", Toast.LENGTH_LONG).show();
            } else
            {
                //Toast.makeText(this, "Internet Connection Not Available!!!", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Location Services not Enabled");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Open Location Settings", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                        //get gps
                    }
                });
                dialog.setNegativeButton("Exit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub
                        System.exit(0);
                    }
                });
                dialog.show();
            }
    }
    public boolean checkInternet(Context ctx)
    {
        ConnectivityManager connec = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        // Check if wifi or mobile network is available or not. If any of them is
        // available or connected then it will return true, otherwise false;
        return wifi.isConnected() || mobile.isConnected();
    }
    public boolean checkLocation(Context ctx)
    {
        LocationManager locationManager=(LocationManager)ctx.getSystemService(Context.LOCATION_SERVICE);
        try
        {
            return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
        }
        catch(Exception e)
        {
            Log.e("Location Error", e.toString());
        }
        return false;
    }

    private boolean isMyServiceRunning(Class serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
