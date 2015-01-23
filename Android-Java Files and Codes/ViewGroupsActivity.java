package com.example.ankit.smartattendancesystem;

import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class ViewGroupsActivity extends ActionBarActivity {

    private static String username;
    private static String editGroupName;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_groups);
        SharedPreferences sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        username=sharedPreferences.getString("username","mishra14");
        ((TextView)findViewById(R.id.viewGroupsMessage)).setText("All groups for "+ username + " - ");

        new fetchUserGroups().execute();

       //TODO fetch user tables data
        TableRow tableRow=new TableRow(this);
        ScrollView scrollView=new ScrollView(this);
        TableLayout tableLayout=new TableLayout(this);
        tableLayout.setOrientation(LinearLayout.VERTICAL);
        TextView groupIDTitle=new TextView(this);
        groupIDTitle.setText("Group ID");
        TextView roleTitle=new TextView(this);
        roleTitle.setText("Role");
        TextView attendanceTitle=new TextView(this);
        attendanceTitle.setText("Attendance");
        TextView actionTitle=new TextView(this);
        actionTitle.setText("Action");
        tableRow.addView(groupIDTitle);
        tableRow.addView(roleTitle);
        tableRow.addView(attendanceTitle);
        tableRow.addView(actionTitle);
        tableLayout.addView(tableRow);
        for(int i=0;i<100;i++)
        {
            TableRow tableRowValues=new TableRow(this);
            TextView groupID=new TextView(this);
            groupID.setText("id" + i);
            TextView role=new TextView(this);
            role.setText("role" + i);
            TextView attendance=new TextView(this);
            attendance.setText("present"+i);
            Button actionButton=new Button(this);
            actionButton.setText("Details");
            actionButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                }
            });
            tableRowValues.addView(groupID);
            tableRowValues.addView(role);
            tableRowValues.addView(attendance);
            tableRowValues.addView(actionButton);
            tableLayout.addView(tableRowValues);
        }
        scrollView.addView(tableLayout);
        setContentView(scrollView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_groups, menu);
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

    class fetchUserGroups extends AsyncTask<Void, Void, ArrayList<UserGroup>>
    {

        @Override
        protected ArrayList<UserGroup> doInBackground(Void... params)
        {
            ArrayList<UserGroup> result=new ArrayList<UserGroup>();
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://mishra14.ddns.net:3000/users/"+username+"/groups");
            try
            {
                JSONObject jsonObject=new JSONObject();
                try
                {
                    jsonObject.put("user_name",ViewGroupsActivity.username);
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
        public ArrayList<UserGroup> parseJSON(String in)
        {
            ArrayList<UserGroup> groupArrayList=new ArrayList<UserGroup>();
            try
            {
                JSONObject responseJSON = new JSONObject(in);
                Log.v("Response JSON", responseJSON.toString());
                //Log.v("Response JSON", Integer.toString(responseJSON.getInt("DBConnection")));
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
               Log.e("Error",e.toString());
            }
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
                    actionButton.setText("View");
                    actionButton.setId(group.getID());
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {

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
}
