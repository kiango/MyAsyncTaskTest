package com.example.torsh.myasynctasktest;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;


public class MainActivity extends ActionBarActivity {

    private static final long PASSWORD = 127458;
    public ProgressBar spinningCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinningCircle = (ProgressBar) findViewById(R.id.spinner);
        spinningCircle.setVisibility(View.GONE);
    }

    // onClick method: compute password guesses
    public void startTask(View v) {

        final long RANGE = 10000000;
        final long PUBLISH_RATE = 100000;


        PasswordGuesserTask passwordGuesserTask = new PasswordGuesserTask();
        passwordGuesserTask.execute(RANGE, PUBLISH_RATE);
    }


    private void displayProgress(String msg) {
        TextView textView_status = (TextView) findViewById(R.id.text_status);
        textView_status.setText(msg);
    }


    public void displayAnswer(long answer){
        String answerStr = "The password is ... " + answer;

        TextView textView_answer = (TextView) findViewById(R.id.text_answer);
        textView_answer.setText(answerStr);
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



    /*
     * background class for calculations
     * When the activity is closed the AsyncTask is also closed
     *
     * */

    // Generic class parameters
    // 1. Long: type of references passed to doInBackground(); ( argument for doInBackground() )
    // 2. String: type of reference passed to onProgressUpdate()
    // 3. Long: type of reference returned by doInBackground(); ( return type of doInBackground() )
    // value passed onPostExecute()

    private class PasswordGuesserTask extends AsyncTask<Long, String, Long>{

        // Executed on the Main UI thread
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // runs on background thread
        @Override
        protected Long doInBackground(Long... params) {


            // Extract arguments from outer class
            long range = params[0];
            long publish_rate = params[1];

            long guess = 0;
            long countGuessNo = 0;
            Random rand = new Random();

            while (guess != PASSWORD){
                guess = Math.abs(rand.nextLong() % range); // to get only the positive values from 1-100000
                countGuessNo++;

                if ( countGuessNo % publish_rate == 0 ) {
                    // publishProgress triggers onProgressUpdate()
                    // passing String param inside publishProgress:
                    publishProgress(
                            "Guess# " + countGuessNo +
                            "\n" +
                            "last guess: " + guess +
                            "\n"
                    );
                    //displayProgress("guessing..." + countGuessNo);
                }
            }

            //displayProgress("Done:"); calling to the main thread
            //displayAnswer(guess); calling to the main thread


            return guess;
        }

        // Executed on main UI thread: (calling back on the UI method on the top)
        // onProgressUpdate allows us to do something on the main UI thread
        // called for every 1000 times (publish rate)
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            String message = "";
            for ( String str : values )
                message += str + "> ";

            displayProgress(message);

            MainActivity.this.spinningCircle.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            displayAnswer(aLong);
            MainActivity.this.spinningCircle.setVisibility(View.GONE);
        }
    }
}
