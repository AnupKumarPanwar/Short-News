package com.newsupdates.headlines;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

;

/**
 * Created by Anup
 */

public class VerticlePagerAdapter extends PagerAdapter {

    JSONObject jsonObject=null;

    String mResources[] = {"NYSE Parent to Buy Chicago Stock Exchange",
    "JP Morgan's Jamie Dimon: \"not unreasonable\" for US to push for fair trade with China"};

    String mDescription[]={"The owner of the New York Stock Exchange has reached a deal to buy the Chicago Stock Exchange, after a two-year acquisition effort from a Chinese-led investor group failed.",
    "JPMorgan Chase CEO Jamie Dimon says it is \"not unreasonable\" for Trump to fight for better trade terms with China. In letter to shareholders, banker says market could be caught off guard by quicker pace of rate hikes."};

    Context mContext;
    LayoutInflater mLayoutInflater;

    int adsCounter=0;

    private InterstitialAd mInterstitialAd;

    public VerticlePagerAdapter(Context context, String response) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(mContext.getResources().getString(R.string.admob_interstitial));

        try {
            jsonObject= new JSONObject(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getCount() {
        try {
            return jsonObject.getJSONArray("articles").length();
        } catch (Exception e) {
//            e.printStackTrace();
            return 20;
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.fragment_main, container, false);

        CardView cardView=(CardView)itemView.findViewById(R.id.card_view);

        TextView label = (TextView) itemView.findViewById(R.id.textView);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);

        TextView newsDesc=(TextView) itemView.findViewById(R.id.textView2);

        TextView source=(TextView)itemView.findViewById(R.id.textView_link);

        FloatingActionButton share=(FloatingActionButton)itemView.findViewById(R.id.share);
        FloatingActionButton categoryMenu=(FloatingActionButton)itemView.findViewById(R.id.menu);
//        TextView author=(TextView)itemView.findViewById(R.id.textView_link2);
//        TextView publishTime=(TextView)itemView.findViewById(R.id.textView_link3);


        adsCounter++;
        if (adsCounter%5==0)
        {
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
        }
        if (adsCounter%10==0)
        {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }


        try {

                String newsImage=jsonObject.getJSONArray("articles").getJSONObject(position).getString("urlToImage");

                String newsTitle=jsonObject.getJSONArray("articles").getJSONObject(position).getString("title");
                String newsDescription=jsonObject.getJSONArray("articles").getJSONObject(position).getString("description");
                String pt=jsonObject.getJSONArray("articles").getJSONObject(position).getString("publishedAt");
                String newsSrc=jsonObject.getJSONArray("articles").getJSONObject(position).getJSONObject("source").getString("name");
                String  newsAuthor=jsonObject.getJSONArray("articles").getJSONObject(position).getString("author");

                String  newsWebpage=jsonObject.getJSONArray("articles").getJSONObject(position).getString("url");

                pt=pt.substring(0, 10);

                if (newsSrc.equals(null) || newsSrc.equals("null"))
                {
                    newsSrc="Unknown";
                }

                if (newsAuthor.equals(null) || newsAuthor.equals("null"))
                {
                    newsAuthor="Anonymous";
                }

                if (newsTitle.equals(null) || newsTitle.equals("null"))
                {
                    newsTitle="Headline not available";
                }

                if (newsDescription.equals(null) || newsDescription.equals("null"))
                {
                    newsDescription="Description not available";
                }

                Picasso.get().load(newsImage).into(imageView);
                if (newsImage.equals(null) || newsImage.equals("null"))
                {
                    Picasso.get().load(R.drawable.images).into(imageView);

                }


//                imageView.setImageResource(jsonObject.getJSONArray("articles").getJSONObject(position).getString("urlToImage"));
                label.setText(newsTitle);
                newsDesc.setText(newsDescription);


                source.setText("more at " +newsSrc+" / "+pt);

//                author.setText(newsAuthor);

//                publishTime.setText(pt);

                final String finalNewsSrc = newsWebpage;
                source.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(mContext, NewsWebView.class);
                        intent.putExtra("url", finalNewsSrc);
                        mContext.startActivity(intent);
                    }
                });


//                imageView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent=new Intent(mContext, NewsWebView.class);
//                        intent.putExtra("url", finalNewsSrc);
//                        mContext.startActivity(intent);
//                    }
//                });



            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    View rootView = ((Activity)mContext).getWindow().getDecorView().findViewById(android.R.id.content);
                    Bitmap bitmap=getScreenShot(rootView);
                    Uri filePath=store(bitmap,"news.png");
                    shareImage(filePath);
                }
            });


            categoryMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(mContext, CategoryMenu.class);
                    mContext.startActivity(intent);
                }
            });

            } catch (Exception e) {
                e.printStackTrace();
            }

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }






    private void shareImage(Uri file){
        Uri uri = file;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, mContext.getString(R.string.app_name));
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "Download "+mContext.getString(R.string.app_name)+" - The #1 News App.\nhttps://play.google.com/store/apps/details?id=com.newsupdates.headlines");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        try {
            mContext.startActivity(Intent.createChooser(intent, "Share score"));
        } catch (Exception e) {
            Toast.makeText(mContext, "No App Available", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public Uri store(Bitmap bm, String fileName){
        Uri bmpUri=null;
        try {
            // This way, you don't need to request external read/write permission.
            File file =  new File(mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "news.png");
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

}
