package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import backend.myJokeApi.MyJokeApi;
import jokedisplay.JokeDisplayActivity;

import static apputil.GlobalConstants.INTENT_JOKE;

/**
 * Created by devbox on 3/14/17.
 */

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

            //Appengine object
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
        if(MainActivityFragment.mProgressDialog!=null) {
            MainActivityFragment.mProgressDialog.dismiss();
        }
        Log.v(LOG_TAG, BuildConfig.FLAVOR);
        Intent displayJokeIntent = new Intent(mContext, JokeDisplayActivity.class);
        displayJokeIntent.putExtra(INTENT_JOKE, s);
        mContext.startActivity(displayJokeIntent);

    }
}
