package com.newsupdates.headlines;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Anup
 */

/**
 * AlarmReceiver handles the broadcast message and generates Notification
 */
public class AlarmReceiver extends BroadcastReceiver {
    SharedPreferences.Editor editor;
    String newsUrl;
    String jsonString;
    JSONObject jsonObject;
    Context mContext;

    SharedPreferences sharedPreferences;
    String country;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Get notification manager to manage/send notifications

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mContext=context;

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(mContext);
        editor=sharedPreferences.edit();



        country=sharedPreferences.getString("country",null);
        if (country==null)
        {
            country="us";
        }

        newsUrl="https://newsapi.org/v2/top-headlines?category=general&apiKey=e1d2194d001540cd903f61c8f8966390&pageSize=100&country="+country;

        JSONAsyncTask getData=new JSONAsyncTask();
        getData.execute();

//        //Intent to invoke app when click on notification.
//        //In this sample, we want to start/launch this sample app when user clicks on notification
//        Intent intentToRepeat = new Intent(context, MainActivity.class);
//        //set flag to restart/relaunch the app
//        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        //Pending intent to handle launch of Activity in intent above
//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(context, NotificationHelper.ALARM_TYPE_RTC, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

//        //Build notification
//        Notification repeatedNotification = buildLocalNotification(context, pendingIntent).build();
//
//        //Send local notification
//        NotificationHelper.getNotificationManager(context).notify(NotificationHelper.ALARM_TYPE_RTC, repeatedNotification);
    }

    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent, String title, String url, Bitmap image) {
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.hours)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(image))/*Notification with Image*/
                        .setAutoCancel(false);

        return builder;
    }





    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(newsUrl)
                    .build();



            try {
                Response response;
                response = client.newCall(request).execute();
                jsonString=response.body().string();

                editor.putString("cachedData", jsonString);
                editor.apply();
                jsonObject = new JSONObject(jsonString);


            }
            catch (Exception E)
            {
//                    Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show();
                jsonString=sharedPreferences.getString("cachedData", null);
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {
            //Intent to invoke app when click on notification.
            //In this sample, we want to start/launch this sample app when user clicks on notification

            for (int i=0; i<5; i++)
            {

                try {

                String title=jsonObject.getJSONArray("articles").getJSONObject(i).getString("title");
                String url=jsonObject.getJSONArray("articles").getJSONObject(i).getString("url");

                String newsImage=jsonObject.getJSONArray("articles").getJSONObject(i).getString("urlToImage");

                Bitmap image=getBitmapfromUrl(newsImage);

                Intent intentToRepeat = new Intent(mContext, NewsWebView.class);
                intentToRepeat.putExtra("url", url);
                //set flag to restart/relaunch the app
                intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    Random random = new Random();
                    int m = random.nextInt(9999 - 1000) + 1000;
                //Pending intent to handle launch of Activity in intent above
                PendingIntent pendingIntent =
                        PendingIntent.getActivity(mContext, m, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

                //Build notification
                Notification repeatedNotification = null;

                    repeatedNotification = buildLocalNotification(mContext, pendingIntent, title, url, image).build();



//                    NotificationHelper.getNotificationManager(mContext).notify(NotificationHelper.ALARM_TYPE_RTC, repeatedNotification);

                    NotificationHelper.getNotificationManager(mContext).notify(m, repeatedNotification);

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

            //Send local notification
        }
    }



    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Bitmap bitmap=BitmapFactory.decodeResource(mContext.getResources(),R.drawable.images);
            return bitmap;

        }
    }
}