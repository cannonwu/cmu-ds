// Cannon Wu
// AndrewID: clwu

// Code from https://github.com/CMU-Heinz-95702/lab8-Android
package edu.heinz.ds.androidinterestingpicture;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InterestingPicture extends AppCompatActivity {

    InterestingPicture me = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * The click listener will need a reference to this object, so that upon successfully finding a picture from Flickr, it
         * can callback to this object with the resulting picture Bitmap.  The "this" of the OnClick will be the OnClickListener, not
         * this InterestingPicture.
         */
        final InterestingPicture ma = this;

        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button)findViewById(R.id.submit);


        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String searchTerm = ((EditText)findViewById(R.id.searchTerm)).getText().toString();
                System.out.println("searchTerm = " + searchTerm);
                GetPicture gp = new GetPicture();
                gp.search(searchTerm, me, ma); // Done asynchronously in another thread.  It calls ip.pictureReady() in this thread when complete.
            }
        });
    }

    /*
     * This is called by the GetPicture object when the picture is ready.  This allows for passing back the Bitmap picture for updating the ImageView
     */
    public void pictureReady(double message, String homeTeam, String awayTeam, String gameTime) {
        // ImageView pictureView = (ImageView)findViewById(R.id.interestingPicture);
        TextView searchView = (EditText)findViewById(R.id.searchTerm);
        TextView resultView = (TextView) findViewById(R.id.textView2);
        TextView feedBack = (TextView) findViewById(R.id.textView);
        if (message == 0) {
            System.out.println("RECEIVED");
            feedBack.setText(new StringBuilder().append("Here is the next upcoming game for the sport key: ").append(((EditText)findViewById(R.id.searchTerm)).getText().toString()).toString());
            resultView.setText(new StringBuilder()
                    .append("\nHome Team: ").append(homeTeam)
                    .append("\nAway Team: ").append(awayTeam)
                    .append("\nDate: ").append(gameTime)
                    .toString());
        } else {
            System.out.println("NOT RECEIVED");
            feedBack.setText(new StringBuilder().append("Sorry, I could not find an upcoming game for ").append(((EditText)findViewById(R.id.searchTerm)).getText().toString()).toString());
        }
        searchView.setText("");


//        if (picture != null) {
//            pictureView.setImageBitmap(picture);
//
//            TextView feedBack = (TextView) findViewById(R.id.textView);
//            feedBack.setText(new StringBuilder().append("Here is a picture of a ").append(((EditText)findViewById(R.id.searchTerm)).getText().toString()).toString());
//
//            System.out.println("picture");
//            pictureView.setVisibility(View.VISIBLE);
//        } else {
//            pictureView.setImageResource(R.mipmap.ic_launcher);
//
//            TextView feedBack = (TextView) findViewById(R.id.textView);
//            feedBack.setText(new StringBuilder().append("Sorry, I could not find a picture of a ").append(((EditText)findViewById(R.id.searchTerm)).getText().toString()).toString());
//
//            System.out.println("No picture");
//            pictureView.setVisibility(View.INVISIBLE);
//        }
//        searchView.setText("");
//        pictureView.invalidate();
    }
}
