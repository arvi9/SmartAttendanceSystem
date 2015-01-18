package com.example.ankit.smartattendancesystem;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
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


public class CreateGroupActivity extends ActionBarActivity {

    private static String username;
    private static String groupName;
    private static String duration;
    private static String percentage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username=sharedPreferences.getString("username","mishra14");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_group, menu);
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

    public void backToUserHome(View view)
    {
        Intent intent=new Intent(getApplicationContext(),UserHomeActivity.class);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void createGroup(View view)
    {
        if(((TextView)findViewById(R.id.groupname)).getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Enter a group name", Toast.LENGTH_LONG).show();
        }
        else
        {
            if(!(((TextView)findViewById(R.id.groupname)).getText().toString().matches("[a-z0-9]*")))
            {
                Toast.makeText(getApplicationContext(), "Group Name should contain lower case alphabets and number only", Toast.LENGTH_LONG).show();
            }
            else
            {
                if(((TextView)findViewById(R.id.duration)).getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Enter the work duration", Toast.LENGTH_LONG).show();
                }
                else if(!(((TextView)findViewById(R.id.duration)).getText().toString().matches("[0-9]*")))
                {
                    Toast.makeText(getApplicationContext(), "Duration can only be numbers", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(((TextView)findViewById(R.id.percentage)).getText().toString().isEmpty())
                    {
                        Toast.makeText(getApplicationContext(), "Enter the work percentage", Toast.LENGTH_LONG).show();
                    }
                    else if(!(((TextView)findViewById(R.id.percentage)).getText().toString().matches("[0-9]*")))
                    {
                        Toast.makeText(getApplicationContext(), "Percentage can only be numbers", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        groupName=((TextView)findViewById(R.id.groupname)).getText().toString();
                        duration=(((TextView)findViewById(R.id.duration)).getText().toString());
                        percentage=((TextView)findViewById(R.id.percentage)).getText().toString();
                        new createGroupRequest().execute();
                    }
                }
            }
        }
    }

    class createGroupRequest extends AsyncTask<Void, Void, Boolean>
    {

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Boolean result=false;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://mishra14.ddns.net:3000/groups");
            try
            {
                JSONObject jsonObject=new JSONObject();
                try
                {
                    jsonObject.put("group",new JSONObject().put("name",groupName));
                    jsonObject.put("user_name",CreateGroupActivity.username);
                } catch (JSONException e)
                {
                    Log.e("JSONException", e.toString());
                }
                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");
                Log.v("Payload JSON",jsonObject.toString());
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
                Toast.makeText(getApplicationContext(), "Group Created", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getApplicationContext(),StartPageActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Group Name already exists", Toast.LENGTH_LONG).show();
            }
        }
    }
}
