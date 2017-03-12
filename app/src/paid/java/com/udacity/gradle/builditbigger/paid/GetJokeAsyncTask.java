package com.udacity.gradle.builditbigger.paid;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import backend.myJokeApi.MyJokeApi;

/**
 * Created by devbox on 3/10/17.
 */
public class GetJokeAsyncTask extends AsyncTask<Context, Void, String> {
    //NOTE the endpoint annotation API name is capitalized by the framework
    private static MyJokeApi myJokeApiService = null;
    private Context context;



    @Override
    protected String doInBackground(Context... params) {

        if(myJokeApiService == null){
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

        context = params[0];

        try{
            return myJokeApiService.getJoke().execute().getData();
        }
        catch (IOException ioe){
            return ioe.getMessage();
        }

    }


    @Override
    protected void onPostExecute(String s) {
        //super.onPostExecute(s);
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

}
