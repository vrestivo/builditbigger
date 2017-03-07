package jokedisplay;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


import com.example.jokedisplay.R;

import apputil.GlobalConstants;

import static apputil.GlobalConstants.INTENT_JOKE;

/**
 * Created by devbox on 2/8/17.
 */

public class JokeDisplayActivity extends AppCompatActivity {

    private String mJoke;
    private TextView mJokeTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joke_display_activity);

        mJokeTextView = (TextView) findViewById(R.id.joke_activity_joke_tv);

        Intent intent = getIntent();

        if(intent!=null ){
            if(intent.hasExtra(INTENT_JOKE)){
                mJoke = intent.getStringExtra(INTENT_JOKE);
                mJokeTextView.setText(mJoke);
            }
        }


    }
}
