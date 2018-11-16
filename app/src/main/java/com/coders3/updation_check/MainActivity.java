package com.coders3.updation_check;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ConnectivityManager manager;
    static ImageView imageView;
    final String path = "http://placeimg.com/640/360/any";
    final String textPath = "https://www.dropbox.com/s/5l9tsoil4o9qoui/Android.txt?dl=1";
    static TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        imageView = findViewById(R.id.image);
        textView = findViewById(R.id.text);

    }

    public void Click(View view)
    {
        NetworkInfo info = manager.getActiveNetworkInfo();
        if(info!=null && info.isConnected())
        {
            if(info.getType() == ConnectivityManager.TYPE_WIFI)
            {
                Toast.makeText( this, "WI FI is Connected ", Toast.LENGTH_SHORT).show();
            }

            else if(info.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                Toast.makeText(this, "MObile Data Connected", Toast.LENGTH_SHORT).show();
                new MyAsync().execute(path);

            }
        }
        else
        {
            Toast.makeText(this, "NOt Connected", Toast.LENGTH_SHORT).show();
        }


    }



    class MyAsync extends AsyncTask<String,Void,Bitmap>
    {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Downloading Image");
            progressDialog.setTitle("Wait...");
            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            return downloadImage(strings[0]);
        }

        private Bitmap downloadImage(String string) {

            Bitmap bitmap = null;

            try {
                URL url = new URL(path);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();


                int code = connection.getResponseCode();
                if(code == HttpURLConnection.HTTP_OK)
                {
                    InputStream stream = connection.getInputStream();
                    if(stream!=null)
                    {
                        bitmap = BitmapFactory.decodeStream(stream);
                    }
                }



            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null)
            {
                progressDialog.dismiss();
                MainActivity.imageView.setImageBitmap(bitmap);
                new MyTextTak().execute(textPath);
            }
        }
    }

    class MyTextTak extends AsyncTask<String,Void,String>
    {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            progressDialog.setTitle("Wait...");
            progressDialog.setMessage("Text Downloading");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            return StringDownload(strings[0]);
        }

        private String StringDownload(String string) {

            String text = null;

            URL url = null;
            try {
                url = new URL(textPath);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                int code = connection.getResponseCode();

                if(code == HttpURLConnection.HTTP_OK)
                {
                    InputStream stream = connection.getInputStream();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

                    StringBuilder stringBuilder = new StringBuilder();

                    if(stream!=null)
                    {

                        while((text = bufferedReader.readLine()) != null)
                        {
                            stringBuilder.append(text);
                        }
                        text = stringBuilder.toString();

                        bufferedReader.close();
                    }
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return text;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s!=null)
            {
                progressDialog.dismiss();
                MainActivity.textView.setText(s);
            }

        }
    }



}
