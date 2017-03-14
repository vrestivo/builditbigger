package com.udacity.gradle.builditbigger;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private final long TIMEOUT = 5000;
    private InterstitialAd mInterstitialAd;
    private Context mContext;
    private Button mJokeButton;
    private GetJokeAsyncTask mGetJokeAsyncTask;

    //ProgressDialog variables
    private ProgressDialog mProgressDialog;
    private String mProgressTitle;
    private String mProgressMessage;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mJokeButton = (Button) root.findViewById(R.id.button);

        setHasOptionsMenu(true);

        //Interstitial Ad Setup
        MobileAds.initialize(mContext, getString(R.string.banner_ad_unit_id));
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(getString(R.string.banner_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
                tellJoke();
            }
        });

        requestNewInterstitial();


        mJokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }

            }
        });


        AdView mAdView = (AdView) root.findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
                //specify emulator as a target device
                //change for a physical device ID if testing on a piece of hardware
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        return root;

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_explain) {
            Toast.makeText(mContext, getString(R.string.bar_explained), Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }


    public void tellJoke() {
        requestNewInterstitial();

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressMessage = getString(R.string.progress_message);
            mProgressTitle = getString(R.string.progress_title);
        }

        try {
            mProgressDialog.show(mContext, mProgressTitle, mProgressMessage, false);
            mGetJokeAsyncTask = new GetJokeAsyncTask();
            mGetJokeAsyncTask.execute(mContext).get(TIMEOUT, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
        } catch (ExecutionException e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
        } catch (TimeoutException e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        mInterstitialAd.loadAd(adRequest);
    }


    public class GetJokeAsyncTask extends AsyncTask<Context, Void, String> {
        //NOTE the endpoint annotation API name is capitalized by the framework
        private MyJokeApi myJokeApiService = null;
        private Context mContext;
        private final String LOG_TAG = "GetJokeAsyncTask";

        @Override
        protected String doInBackground(Context... params) {
            mContext = params[0];

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

                //creates an Appengine object
                myJokeApiService = builder.build();
            }


            try {
                return myJokeApiService.getJoke().execute().getData();
            } catch (IOException ioe) {
                return ioe.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String s) {
            mProgressDialog.dismiss();
            Intent displayJokeIntent = new Intent(mContext, JokeDisplayActivity.class);
            displayJokeIntent.putExtra(INTENT_JOKE, s);
            mContext.startActivity(displayJokeIntent);
        }
    }


}
