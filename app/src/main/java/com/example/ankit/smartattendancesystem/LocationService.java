package com.example.ankit.smartattendancesystem;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ankit on 1/17/2015.
 */
public class LocationService extends Service
{
    private static String username;
    LocationManager locationManager;
    LocationListener locationListenerGPS, locationListenerNetwork;
    public static Location gpsLocation, networkLocation, lastKnownLocation;
    public static boolean gpsStatus, networkStatus, networkListener, gpsListener, gotLocation, firstTime;
    android.os.Handler locationCheck=new Handler();
    Runnable locationCheckLoop=new Runnable()
    {
        @Override
        public void run()
        {
            if(!gotLocation && !firstTime)
            {
                //TODO when location was not updated
                updateLocation();
            }
            gpsStatus=networkStatus=false;
            try
            {
                gpsStatus=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
            catch(Exception e)
            {
                Log.e("LocationError",e.toString());
            }
            try
            {
                networkStatus=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
            catch(Exception e)
            {
                Log.e("LocationError",e.toString());
            }

            //don't start listeners if no provider is enabled
            if(!gpsStatus && !networkStatus)
            {
                //TODO location and network disabled
            }
            if(gpsStatus && !gpsListener)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
                gpsListener=true;
            }
            if(networkStatus && !networkListener)
            {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
                networkListener=true;
            }
            locationCheck.postDelayed(locationCheckLoop,30000);
        }
    };
    public void onCreate()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username = sharedPreferences.getString("user","mishra14");

        Log.v("Service","Service Started");
        /*LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        */
        firstTime=true;
        gotLocation= gpsStatus = networkStatus = networkListener = gpsListener = false;
        locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        lastKnownLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        firstTime=false;
        locationListenerGPS=new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                Log.v("LocationChange",location.toString());
                gpsLocation=location;
                lastKnownLocation=gpsLocation;
                locationManager.removeUpdates(this);
                locationManager.removeUpdates(locationListenerNetwork);
                networkListener=false;
                gpsListener=false;
                gotLocation=true;
                updateLocation();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {
                Log.v("StatusChange",s);
                Log.v("StatusChange",""+i);
                Log.v("StatusChange",bundle.toString());
            }

            @Override
            public void onProviderEnabled(String s)
            {
                Log.v("ProviderEnabled",s);
            }

            @Override
            public void onProviderDisabled(String s)
            {
                Log.v("ProviderDisabled",s);
            }
        };
        locationListenerNetwork=new LocationListener() {
            @Override
            public void onLocationChanged(Location location)
            {
                Log.v("LocationChange",location.toString());
                networkLocation=location;
                lastKnownLocation=networkLocation;
                locationManager.removeUpdates(this);
                networkListener=false;
                gotLocation=false;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle)
            {
                Log.v("StatusChange",s);
                Log.v("StatusChange",""+i);
                Log.v("StatusChange",bundle.toString());
            }

            @Override
            public void onProviderEnabled(String s)
            {
                Log.v("ProviderEnabled",s);
            }

            @Override
            public void onProviderDisabled(String s)
            {
                Log.v("ProviderDisabled",s);
            }
        };
        locationCheck.post(locationCheckLoop);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public int onStartCommand (Intent intent, int flags, int startId)
    {
        //Toast.makeText(this, "Location Service Started", Toast.LENGTH_LONG).show();
        Log.v("Location", "Location Service Started");
        return START_STICKY;
    }

    public void onDestroy()
    {
        Toast.makeText(this, "Location Service Stopped", Toast.LENGTH_LONG).show();
        Log.v("Service","Service Stopped");
        if(gpsListener)
        {
            locationManager.removeUpdates(locationListenerGPS);
            gpsListener=false;
        }
        if(networkListener)
        {
            locationManager.removeUpdates(locationListenerNetwork);
            networkListener=false;
        }
        locationCheck.removeCallbacksAndMessages(null);
    }

    public void updateLocation()
    {
        Log.v("Location",lastKnownLocation.toString());
        new postLocationRequest().execute();
    }

    class postLocationRequest extends AsyncTask<Void, Void, Boolean>
    {

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Boolean result=false;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mishra14.ddns.net:3000/users/"+LocationService.username+"/log_location");
            try
            {
                JSONObject jsonObject=new JSONObject();
                try
                {
                    jsonObject.put("location_log",new JSONObject().put("lattitude",lastKnownLocation.getLatitude()).put("longitude",lastKnownLocation.getLongitude()));
                } catch (JSONException e)
                {
                    Log.e("JSONException", e.toString());
                }
                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");
                StringEntity se=new StringEntity(jsonObject.toString());

                httppost.setEntity(se);
                HttpResponse httpResponse = httpclient.execute(httppost);
                HttpEntity entity = httpResponse.getEntity();
                //Log.v("HTTP", entity.toString());
                if (entity != null)
                {
                    InputStream stream = entity.getContent();
                    String data = convertStreamToString(stream);
                    result=parseJSON(data);
                    Log.v("Result",Boolean.toString(result));
                    stream.close();
                }
            }
            catch (ClientProtocolException e)
            {
                // TODO Auto-generated catch block
            } catch (IOException e)
            {
                // TODO Auto-generated catch block
            }
            return result;
        }
        String convertStreamToString(java.io.InputStream is)
        {
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
        public Boolean parseJSON(String in)
        {
            try
            {
                JSONObject responseJSON = new JSONObject(in);
                Log.v("Response JSON", responseJSON.toString());
                //Log.v("Response JSON", Integer.toString(responseJSON.getInt("DBConnection")));
                if(responseJSON.getInt("status")==201)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            if(result)
            {
                    //DO Nothing
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Unable to log location", Toast.LENGTH_LONG).show();
            }
        }
    }
}