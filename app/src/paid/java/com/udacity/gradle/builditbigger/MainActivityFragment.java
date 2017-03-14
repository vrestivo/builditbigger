package com.udacity.gradle.builditbigger;

import android.app.ProgressDialog;
import android.content.Context;
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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


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
    public static ProgressDialog mProgressDialogSpinner;
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


        //set progress dialog strings
        mProgressMessage = getString(R.string.progress_message);
        mProgressTitle = getString(R.string.progress_title);

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

        try {
            mProgressDialogSpinner = new ProgressDialog(mContext, ProgressDialog.STYLE_SPINNER).show(mContext, mProgressTitle, mProgressMessage, true);
            mGetJokeAsyncTask = new GetJokeAsyncTask();
            mGetJokeAsyncTask.execute(mContext).get(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            //mProgressDialogSpinner.dismiss();
        } catch (ExecutionException e) {
            e.printStackTrace();
            //mProgressDialogSpinner.dismiss();
        } catch (TimeoutException e) {
            e.printStackTrace();
            //mProgressDialogSpinner.dismiss();
        }

    }


}
