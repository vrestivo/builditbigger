import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.udacity.gradle.builditbigger.GetJokeAsyncTask;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

/**
 * Created by devbox on 3/10/17.
 */


@RunWith(AndroidJUnit4.class)
public class AsyncTaskTest {

    private String LOG_TAG = "_AsyncTaskTest";

    private Context mContext;

    @Before
    public void getContext(){
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void getJokeAsyncTaskTest(){
        GetJokeAsyncTask task = new GetJokeAsyncTask();

        String joke = null;

        try {
            joke = task.execute(mContext).get();

        }
        catch (InterruptedException ie){
            ie.printStackTrace();
        }
        catch (ExecutionException ee){
            ee.printStackTrace();
        }

        Assert.assertNotNull("returned null string", joke);

        System.out.println("_got_joke: " + joke);

        Log.v(LOG_TAG, joke);

    }



}