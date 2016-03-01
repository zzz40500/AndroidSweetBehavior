package com.mingle.androidsweetbehavior;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
        if (id == R.id.action_github) {
            Uri uri = Uri.parse("https://github.com/zzz40500/AndroidSweetBehavior");
            startActivity(new Intent(Intent.ACTION_VIEW,uri));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void instagramAction(View view) {

        InstagramActivity.startActivity(this);
    }

    public void studyNotesAction(View view) {

        WebActivity.startActivity(this,"http://www.jianshu.com/p/99adaad8d55c");
    }

    public void secondClick(View view) {
        SecondActivity.startActivity(this);

    }
}
