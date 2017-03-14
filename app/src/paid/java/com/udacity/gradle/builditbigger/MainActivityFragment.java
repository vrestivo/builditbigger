package com.udacity.gradle.builditbigger;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
    private String mJoke = null;
    private Button mJokeButton;
    private GetJokeAsyncTask mGetJokeAsyncTask;
    private Context mContext;

    //ProgressDialog variables
    private ProgressDialog mProgressDialog;
    private String mProgressTitle;
    private String mProgressMessage;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mContext =  getContext();
    }

    public MainActivityFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mJokeButton = (Button) root.findViewById(R.id.button);

        mJokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tellJoke();
            }
        });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_explain) {
            Toast.makeText(mContext, getString(R.string.bar_explained), Toast.LENGTH_LONG).show();
        }

        return true;
    }



    public void tellJoke(){

        if(mProgressDialog==null){
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
            mProgressDialog.dismiss();
            Log.v(LOG_TAG, BuildConfig.FLAVOR);
            //TODO lauch joke activity
            Intent displayJokeIntent = new Intent(mContext, JokeDisplayActivity.class);
            displayJokeIntent.putExtra(INTENT_JOKE, s);
            mContext.startActivity(displayJokeIntent);

        }
    }

}
