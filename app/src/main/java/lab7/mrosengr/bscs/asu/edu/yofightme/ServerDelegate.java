package lab7.mrosengr.bscs.asu.edu.yofightme;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Any code here is free to use by anyone for any purpose whatsoever =)
 * Copyright 2015 Mike
 *
 * @author Mike    mailto:mrosengr@asu.edu.
 * @version 4/13/2015
 */
public class ServerDelegate {
    Context context;

    public ServerDelegate(Context con) {
        this.context = con;
    }

    public void getRandomUser() {
        android.util.Log.d("getRandomUser()", "ServerDelegate getRandomUser() called");
        GetRandomUserTask task = new GetRandomUserTask();
        task.execute();
    }

    public void getUserNames() {
        android.util.Log.d("getUserNames()", "ServerDelegate getUserNames() called");
        GetUserNamesTask task = new GetUserNamesTask();
        task.execute();
    }

    private class GetRandomUserTask extends AsyncTask<Context, String, JSONObject> {

        @Override
        protected JSONObject doInBackground(Context... params) {

            android.util.Log.d("GetRandomUserTask", "GetRandomUserTask doInBackground() called");
            JSONObject resultObj = null;

            try {
                URI uri = new URI("http://yofightme.mjrosengrant.com/get_random_user.php");
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(uri);

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader
                        (new InputStreamReader(response.getEntity().getContent()));

                //Reads from Buffered Reader into a String
                StringBuilder builder = new StringBuilder();
                String aux = "";
                while ((aux = in.readLine()) != null) {
                    builder.append(aux);
                }
                String resultString = builder.toString();
                Log.d("GetRandomUser", resultString);
                resultObj = new JSONObject(resultString);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }

            return resultObj;
        }

        @Override
        protected void onPostExecute(JSONObject userData) {
            android.util.Log.d("onPostExecute", "Entered onPostExecute. userData = " + userData.toString());
            Activity activity = (Activity) context;
            TextView namebox = (TextView) activity.findViewById(R.id.nameTV);
            TextView agebox = (TextView) activity.findViewById(R.id.ageTV);
            TextView biobox = (TextView) activity.findViewById(R.id.bioTV);

            try {
                JSONArray user = userData.getJSONArray("user");
                android.util.Log.d("JSON decoding", user.toString());
                JSONObject userdata = user.getJSONObject(0);
                android.util.Log.d("JSON decoding", userdata.toString());
                String userName = userdata.getString("name");
                String userBio = String.valueOf(Html.fromHtml(Html.fromHtml(userdata.getString("bio")).toString()));
                String userAge = userdata.getString("age");
                String userImageURL = "http://yofightme.mjrosengrant.com/" + userdata.getString("img_url").replaceAll("\\\\", "");
                android.util.Log.d("JSON decoding", userImageURL);

                namebox.setText(userName);
                agebox.setText(userAge);
                biobox.setText(userBio);

                GetImageFromUrlTask task = new GetImageFromUrlTask();
                task.execute(userImageURL);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }//End GetRandomUserTask

    private class GetImageFromUrlTask extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(params[0]).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Activity activity = (Activity) context;
            ImageView imagebox = (ImageView) activity.findViewById(R.id.imageView);
            imagebox.setImageBitmap(bitmap);
        }
    }





    // Download JSON file AsyncTask
    private class GetUserNamesTask extends AsyncTask<Void, Void, JSONArray> {

        @Override
        protected JSONArray doInBackground(Void... params) {
            android.util.Log.d("GetUserNames", "doInBackground() called");
            JSONArray resultArr = null;

            try {
                URI uri = new URI("http://yofightme.mjrosengrant.com/get_user_names.php");
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(uri);

                HttpResponse response = client.execute(request);
                BufferedReader in = new BufferedReader
                        (new InputStreamReader(response.getEntity().getContent()));

                //Reads from Buffered Reader into a String
                StringBuilder builder = new StringBuilder();
                String aux = "";
                while ((aux = in.readLine()) != null) {
                    builder.append(aux);
                }
                String resultString = builder.toString();
                Log.d("GetUserNames", resultString);
                resultArr = new JSONArray(resultString);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (org.json.JSONException e) {
                e.printStackTrace();
            }

            return resultArr;
        }

        @Override
        protected void onPostExecute(JSONArray namesListJSON) {

            Activity activity = (Activity) context;
            Spinner mySpinner = (Spinner) activity.findViewById(R.id.user_spinner);

            ArrayList<String> namesList = new ArrayList<String>();
            try {
                //Converts JSON array to ArrayList
                if (namesListJSON != null) {
                    for (int i = 0; i < namesListJSON.length(); i++) {
                        namesList.add(namesListJSON.get(i).toString());
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

            //selected item will look like a spinner set from XML
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, namesList);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mySpinner.setAdapter(spinnerArrayAdapter);

        }
    }
}



