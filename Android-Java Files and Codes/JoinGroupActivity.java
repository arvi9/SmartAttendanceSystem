package com.example.ankit.smartattendancesystem;

import android.annotation.TargetApi;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class JoinGroupActivity extends ActionBarActivity {

    private static String username;
    private static String editGroupName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username=sharedPreferences.getString("username","mishra14");
        ((TextView)findViewById(R.id.joinGroupsMessage)).setText("All groups - ");
        new fetchAllGroups().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.join_group, menu);
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

    class fetchAllGroups extends AsyncTask<Void, Void, ArrayList<UserGroup>>
    {

        @Override
        protected ArrayList<UserGroup> doInBackground(Void... params)
        {
            ArrayList<UserGroup> result=new ArrayList<UserGroup>();
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet=new HttpGet("http://mishra14.ddns.net:3000/groups");
            try
            {
                JSONObject jsonObject=new JSONObject();
                try
                {
                    jsonObject.put("user_name",JoinGroupActivity.username);
                } catch (JSONException e)
                {
                    Log.e("JSONException", e.toString());
                }
                httpGet.setHeader("Accept", "application/json");
                httpGet.setHeader("Content-type", "application/json");

                HttpResponse httpResponse = httpclient.execute(httpGet);
                HttpEntity entity = httpResponse.getEntity();
                //Log.v("HTTP", entity.toString());
                if (entity != null)
                {
                    InputStream stream = entity.getContent();
                    String data = convertStreamToString(stream);
                    result=parseJSON(data);
                    //Log.v("Result",Boolean.toString(result));
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
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        public ArrayList<UserGroup> parseJSON(String in)
        {
            ArrayList<UserGroup> groupArrayList=new ArrayList<UserGroup>();
            try
            {
                JSONObject responseJSON = new JSONObject(in);
                Log.v("Response JSON", responseJSON.toString());
                if(responseJSON.getInt("status")==200)
                {
                    JSONArray groupsJSON=responseJSON.getJSONArray("groups");
                    Log.v("Groups",groupsJSON.length()+" "+groupsJSON.toString());
                    for(int i=0;i<groupsJSON.length();i++)
                    {
                        String lat="";//groupsJSON.getJSONObject(i).getString("latitude");
                        String lng="";//groupsJSON.getJSONObject(i).getString("longitude");
                        long duration= 0;//groupsJSON.getJSONObject(i).getLong("reqd_duration_in_secs");
                        float percentage=0;//(float)groupsJSON.getJSONObject(i).getDouble("attendance_reqd_percentage");
                        int groupID = groupsJSON.getJSONObject(i).getInt("id");
                        String role = "Member";//groupsJSON.getJSONObject(i).getString("");
                        String name = groupsJSON.getJSONObject(i).getString("name");
                        Boolean attendance = true;//groupsJSON.getJSONObject(i).getBoolean("");
                        groupArrayList.add(new UserGroup(lat,lng,name,role,groupID,attendance,duration,percentage));
                    }
                }

            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.v("groupArray",groupArrayList.toString());
            return groupArrayList;
        }

        @Override
        protected void onPostExecute(ArrayList<UserGroup> groupArrayList)
        {
            super.onPostExecute(groupArrayList);
            if(!groupArrayList.isEmpty())
            {
                TableRow tableRow=new TableRow(getApplicationContext());
                ScrollView scrollView=new ScrollView(getApplicationContext());
                TableLayout tableLayout=new TableLayout(getApplicationContext());
                tableLayout.setOrientation(LinearLayout.VERTICAL);
                TextView groupIDTitle=new TextView(getApplicationContext());
                groupIDTitle.setText("Group ID");
                TextView groupNameTitle=new TextView(getApplicationContext());
                groupNameTitle.setText("Group Name");
                TextView actionTitle=new TextView(getApplicationContext());
                actionTitle.setText("Action");
                tableRow.addView(groupIDTitle);
                tableRow.addView(groupNameTitle);
                tableRow.addView(actionTitle);
                tableLayout.addView(tableRow);
                for (UserGroup group : groupArrayList)
                {
                    Log.v("group",group.toString());
                    TableRow tableRowValues = new TableRow(getApplicationContext());
                    TextView groupID = new TextView(getApplicationContext());
                    groupID.setText(""+group.getID());
                    final TextView groupName = new TextView(getApplicationContext());
                    groupName.setText(group.getName());
                    final Button actionButton = new Button(getApplicationContext());
                    actionButton.setText("Join");
                    actionButton.setId(group.getID());
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            editGroupName=groupName.getText().toString();
                            Toast.makeText(getApplicationContext(), editGroupName, Toast.LENGTH_LONG).show();
                            new joinGroup().execute();
                        }
                    });
                    tableRowValues.addView(groupID);
                    tableRowValues.addView(groupName);
                    tableRowValues.addView(actionButton);
                    tableLayout.addView(tableRowValues);
                }
                scrollView.addView(tableLayout);
                setContentView(scrollView);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "NO Groups Present", Toast.LENGTH_LONG).show();
            }
        }
    }

    class joinGroup extends AsyncTask<Void, Void, Boolean>
    {

        @Override
        protected Boolean doInBackground(Void... params)
        {
            Boolean result=false;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost=new HttpPost("http://mishra14.ddns.net:3000/groups/"+editGroupName+"/add_user");
            try
            {
                JSONObject jsonObject=new JSONObject();
                try
                {
                    jsonObject.put("user_name",JoinGroupActivity.username);
                } catch (JSONException e)
                {
                    Log.e("JSONException", e.toString());
                }
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                StringEntity se=new StringEntity(jsonObject.toString());
                httpPost.setEntity(se);
                HttpResponse httpResponse = httpclient.execute(httpPost);
                HttpEntity entity = httpResponse.getEntity();
                //Log.v("HTTP", entity.toString());
                if (entity != null)
                {
                    InputStream stream = entity.getContent();
                    String data = convertStreamToString(stream);
                    result=parseJSON(data);
                    //Log.v("Result",Boolean.toString(result));
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
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        public Boolean parseJSON(String in)
        {
            Boolean result=false;
            try
            {
                JSONObject responseJSON = new JSONObject(in);
                Log.v("Response JSON", responseJSON.toString());
                if(responseJSON.getInt("status")==201)
                {
                    result=true;
                }

            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            if(result)
            {
                Toast.makeText(getApplicationContext(), "Joined the group as a Member", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Unable to join group", Toast.LENGTH_LONG).show();
            }
        }
    }
}
