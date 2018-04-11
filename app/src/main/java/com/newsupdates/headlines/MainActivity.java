package com.newsupdates.headlines;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String newsUrl;

    Runnable runnable;
    Handler handler;

    String jsonString;

    ProgressBar progressBar;

    String country;

    TextView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor=sharedPreferences.edit();

        MobileAds.initialize(this, "ca-app-pub-3971583580619783~6316622155");

        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        loading=(TextView)findViewById(R.id.loading);

        handler=new Handler();

        progressBar.setVisibility(View.VISIBLE);

        country=sharedPreferences.getString("country",null);
        if (country==null)
        {
            country="us";
        }

        try {
            newsUrl=getIntent().getExtras().getString("newsUrl",null);
            if (newsUrl==null)
            {
                newsUrl="https://newsapi.org/v2/top-headlines?category=general&apiKey=e1d2194d001540cd903f61c8f8966390&pageSize=100&country="+country;
            }
            else {
                newsUrl=newsUrl+"&country="+country;
            }
        }
        catch (Exception E)
        {
            newsUrl="https://newsapi.org/v2/top-headlines?category=general&apiKey=e1d2194d001540cd903f61c8f8966390&pageSize=100&country="+country;
        }




        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        runnable=new Runnable() {
            @Override
            public void run() {
//                initSwipePager();
                JSONAsyncTask getData=new JSONAsyncTask();
                getData.execute();
            }
        };

        try {
            handler.postDelayed(runnable, 10);
        }
        catch (Exception E)
        {
//            initSwipePager();
            JSONAsyncTask getData=new JSONAsyncTask();
            getData.execute();
        }


        Window window = MainActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(Color.parseColor("#ddd4cb"));

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);



        scheduleNotification();
//    scheduleRepeatingElapsedNotification(getApplicationContext());



    }

    private void scheduleNotification() {
        NotificationHelper.enableBootReceiver(getApplicationContext());
        NotificationHelper.scheduleRepeatingElapsedNotification(getApplicationContext());
    }


//    public void initSwipePager(){
//
//        OkHttpClient client = new OkHttpClient();
//
//                    Request request = new Request.Builder()
//                    .url(newsUrl)
//                    .build();
//
//        String response = null;
//
//        try {
//            response = client.newCall(request).execute().body().string();
//            editor.putString("cachedData", response);
//            editor.apply();
//        } catch (Exception e) {
//            response=sharedPreferences.getString("cachedData", null);
////            e.printStackTrace();
//        }
//
//
//        VerticalViewPager verticalViewPager = (VerticalViewPager) findViewById(R.id.vPager);
//        try {
//            verticalViewPager.setAdapter(new VerticlePagerAdapter(this , response));
//        } catch (Exception e) {
////            e.printStackTrace();
//        }
//    }













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
                JSONObject jsonObject = new JSONObject(jsonString);


            }
            catch (Exception E)
            {
//                    Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show();
                    jsonString=sharedPreferences.getString("cachedData", null);
            }

            return true;
        }

        protected void onPostExecute(Boolean result) {
            VerticalViewPager verticalViewPager = (VerticalViewPager) findViewById(R.id.vPager);
            try {
                progressBar.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                verticalViewPager.setAdapter(new VerticlePagerAdapter(MainActivity.this , jsonString));
            } catch (Exception e) {
//            e.printStackTrace();
            }
        }
    }








//    public static void scheduleRepeatingElapsedNotification(Context context) {
//        //Setting intent to class where notification will be handled
//        Intent intent = new Intent(context, AlarmReceiver.class);
//
//        //Setting pending intent to respond to broadcast sent by AlarmManager everyday at 8am
//        PendingIntent alarmIntentElapsed = PendingIntent.getBroadcast(context, ALARM_TYPE_ELAPSED, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        //getting instance of AlarmManager service
//        AlarmManager alarmManagerElapsed = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//
//        //Inexact alarm everyday since device is booted up. This is a better choice and
//        //scales well when device time settings/locale is changed
//        //We're setting alarm to fire notification after 15 minutes, and every 15 minutes there on
////        alarmManagerElapsed.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
////                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
////                AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntentElapsed);
//
//        alarmManagerElapsed.setRepeating(AlarmManager.ELAPSED_REALTIME,
//                SystemClock.elapsedRealtime() + 10,
//                10, alarmIntentElapsed);
//    }

}
