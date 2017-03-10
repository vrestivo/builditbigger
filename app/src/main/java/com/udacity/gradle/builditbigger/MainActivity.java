package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

import backend.myJokeApi.MyJokeApi;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

/*    public void tellJoke(View view) {
        //Toast.makeText(this, new JokeTeller().getJoke(),    //"derp",
        //        Toast.LENGTH_SHORT).show();

        Intent jokeIntent = new Intent(this, JokeDisplayActivity.class);
        jokeIntent.putExtra(GlobalConstants.INTENT_JOKE, new JokeTeller().getJoke());
        startActivity(jokeIntent);


    }*/


    public void tellJoke(View view){
        //TODO spawn off an AsyncTask
        new GetJokeAsyncTask().execute(this);
    }


}

class GetJokeAsyncTask extends AsyncTask<Context, Void, String> {
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