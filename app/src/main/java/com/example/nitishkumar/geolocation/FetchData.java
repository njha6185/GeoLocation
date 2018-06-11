package com.example.nitishkumar.geolocation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by NITISH KUMAR on 10-06-2018.
 */

public class FetchData extends AsyncTask<Void, Void, Void> {

    String place, city;
    String data = "";
    String dataParsed = "";
    String singleParsed = "";
    String imageLink;
    Bitmap forcastImage;

    public FetchData(String place, String city) {
        this.place = place;
        this.city = city;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather." +
                    "forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)" +
                    "%20where%20text%3D%22" + place + "%2C%20" + city + "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                data += line;
            }


            JSONObject object = new JSONObject(data);
            object = object.getJSONObject("query");
            object = object.getJSONObject("results");
            object = object.getJSONObject("channel");

            JSONObject unitObj = object.getJSONObject("units");
            String unit = unitObj.getString("temperature");

            object = object.getJSONObject("item");

            imageLink = object.getString("description");

            object = object.getJSONObject("condition");
            singleParsed = object.getString("temp");

            int temperature = Integer.parseInt(singleParsed);
            temperature = ((temperature - 32) * 5) / 9;

            singleParsed = singleParsed + "°" + unit;
            singleParsed = temperature + "°C" + "\n" + singleParsed;

            imageLink = imageLink.substring(imageLink.indexOf("src=\""), imageLink.indexOf("\"/>"));
            imageLink = imageLink.substring(imageLink.indexOf("http:"), imageLink.indexOf(".gif"));
            imageLink = imageLink + ".gif";

            loadImage(imageLink);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        MainActivity.tempPlaceValue.setText(this.singleParsed);
        MainActivity.weatherRelatedImage.setImageBitmap(this.forcastImage);
        Log.d("executed : ", "message : " + singleParsed);
    }

    public void loadImage(String url) throws IOException {
        URL url1 = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        forcastImage = BitmapFactory.decodeStream(input);
    }
}
