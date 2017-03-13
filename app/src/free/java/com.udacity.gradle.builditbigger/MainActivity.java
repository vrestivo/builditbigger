package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import backend.myJokeApi.MyJokeApi;
import jokedisplay.JokeDisplayActivity;

import static apputil.GlobalConstants.INTENT_JOKE;


public class MainActivity extends AppCompatActivity {
    private final long TIMEOUT = 5000;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id));


        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                //TODO request new add
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void tellJoke(View view) {

        try {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                new GetJokeAsyncTask().execute(this).get(TIMEOUT, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            //TODO Notify user that could not get the joke
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("MY_EMULATOR_DEVICE")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }



    public class GetJokeAsyncTask extends AsyncTask<Context, Void, String> {
        //NOTE the endpoint annotation API name is capitalized by the framework
        private  MyJokeApi myJokeApiService = null;
        private Context mContext;
        private final String LOG_TAG = "GetJokeAsyncTask";


        @Override
        protected String doInBackground(Context... params) {

            if (myJokeApiService == null) {
                MyJokeApi.Builder builder = new MyJokeApi.Builder(  //Appengine service builder
                        AndroidHttp.newCompatibleTransport(),       //abstract HTTP transport mechanism
                        new AndroidJsonFactory(),                   //serialization mechanism for bidirectional
                        // conversion between JSON and JAVA objects

                        null                                        //HTTP request initializer used for URL request configuration
                ).setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
                                request.setDisableGZipContent(true);
                            }
                        });

                //Appengine object
                myJokeApiService = builder.build();
            }

            mContext = params[0];

            try {
                return myJokeApiService.getJoke().execute().getData();
            } catch (IOException ioe) {
                return ioe.getMessage();
            }

        }


        @Override
        protected void onPostExecute(String s) {

            Intent displayJokeIntent = new Intent(mContext, JokeDisplayActivity.class);
            displayJokeIntent.putExtra(INTENT_JOKE, s);
            mContext.startActivity(displayJokeIntent);
        }


    }


}




