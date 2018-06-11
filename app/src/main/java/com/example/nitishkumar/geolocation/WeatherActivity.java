package com.example.nitishkumar.geolocation;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodec;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class WeatherActivity extends AppCompatActivity {

    public static TextView text;
    public static ImageView imageView;
    String temp;
    Bitmap icon = null;
    ArrayList<String> weather = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        init();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(WeatherActivity.this, "image Clicked!!!", Toast.LENGTH_SHORT).show();
                FetchData process = new FetchData("noida", "up");
                process.execute();
            }
        });
    }

    public void init() {
        text = (TextView) findViewById(R.id.tempText);
        imageView = (ImageView) findViewById(R.id.weatherImageView);
    }
}


//
//protected class retrieve_weatherTask extends AsyncTask<Void, String, String>
//{
//    @Override
//    protected void onPreExecute() {
//        super.onPreExecute();
//    }
//
//    @Override
//    protected String doInBackground(Void... voids) {
//
//        String qResult = "";
//        HttpClient httpClient = new DefaultHttpClient();
//        HttpContext localContext = new BasicHttpContext();
//        HttpGet httpGet = new HttpGet("http://weather.yahooapis.com/forecastrss?w=29224994&u=c&#8221;");
//
//        try
//        {
//            HttpResponse response = httpClient.execute(httpGet, localContext);
//            HttpEntity entity = response.getEntity();
//
//            if (entity != null)
//            {
//                InputStream inputStream = entity.getContent();
//                Reader in = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(in);
//                StringBuilder stringBuilder = new StringBuilder();
//                String stringReadline = null;
//                while ((stringReadline = bufferedReader.readLine()) != null)
//                {
//                    stringBuilder.append(stringReadline+"\n");
//                }
//                qResult = stringBuilder.toString();
//            }
//        }
//        catch (ClientProtocolException e)
//        {
//            e.printStackTrace();
//            Toast.makeText(WeatherActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//        }catch (IOException e1)
//        {
//            e1.printStackTrace();
//        }
//
//        Document dest = null;
//        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder parser;
//        try {
//            parser = documentBuilderFactory.newDocumentBuilder();
//            dest = parser
//                    .parse(new ByteArrayInputStream(qResult.getBytes()));
//        }catch (ParserConfigurationException e1)
//        {
//            e1.printStackTrace();
//            Toast.makeText(WeatherActivity.this, e1.toString(), Toast.LENGTH_SHORT).show();
//        }catch (SAXException e)
//        {
//            e.printStackTrace();
////                Toast.makeText(WeatherActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//        }catch (IOException e)
//        {
//            e.printStackTrace();
//            Toast.makeText(WeatherActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//        }
//        Node temperatureNode = dest.getElementsByTagName("yweather:condition").item(0);
//        temp = temperatureNode.getAttributes().getNamedItem("temp").getNodeValue().toString();
//        Node tempunitNode = dest.getElementsByTagName("yweather:umits").item(0);
//        temp = temp+ "*"+tempunitNode.getAttributes().getNamedItem("temperature").getNodeValue().toString();
////            temp="10";
//
//        String desc = dest.getElementsByTagName("item").item(0).getChildNodes().item(13).getTextContent().toString();
//        StringTokenizer str = new StringTokenizer(desc, "<=>");
//        String src = str.nextToken();
//        String url1 = src.substring(1,src.length()-2);
//        Pattern TAG_REGEX = Pattern.compile("(.+?)<br/>");
//        Matcher matcher = TAG_REGEX.matcher(desc);
//        while (matcher.find())
//        {
//            weather.add(matcher.group(1));
//        }
//
//        InputStream in = null;
//        try {
//            int response =-1;
//            URL url = new URL(url1);
//            URLConnection conn = url.openConnection();
//            if (!(conn instanceof HttpURLConnection))
//                throw new IOException("Not an HTTP Connection");
//            HttpURLConnection httpConn = (HttpURLConnection)conn;
//            httpConn.setAllowUserInteraction(false);
//            httpConn.setInstanceFollowRedirects(true);
//            httpConn.setRequestMethod("GET");
//            httpConn.connect();
//
//            response = httpConn.getResponseCode();
//            if (response == HttpURLConnection.HTTP_OK)
//            {
//                in = httpConn.getInputStream();
//            }
//            icon = BitmapFactory.decodeStream(in);
//            in.close();
//        }catch (IOException e1)
//        {
//            e1.printStackTrace();
//        }
//        return qResult;
//    }
//
//    @Override
//    protected void onPostExecute(String s) {
//        text.setText("Tempertaute: "+ temp);
//        imageView.setImageBitmap(icon);
//    }
//}