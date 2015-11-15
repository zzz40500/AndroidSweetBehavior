package com.mingle.androidsweetbehavior;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mingle.androidsweetbehavior.adapter.ImageRVAdapter;

import java.util.ArrayList;
import java.util.List;

public class InstagramActivity extends AppCompatActivity implements View.OnClickListener {




    private RecyclerView mRV;

    private ImageView mContentIv;

    public static  void startActivity(Context ctx){
        ctx.startActivity(new Intent(ctx,InstagramActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);
        mRV= (RecyclerView) findViewById(R.id.rv);
        findViewById(R.id.contentIv).setOnClickListener(this);

        findViewById(R.id.contentIv).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        List<String> list=new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            list.add("");
        }

        GridLayoutManager l=new GridLayoutManager(this,4,GridLayoutManager.VERTICAL,false);

        mRV.setLayoutManager(l);

        mRV.setAdapter(new ImageRVAdapter(list));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instagram, menu);
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

    @Override
    public void onClick(View v) {
        Toast.makeText(this,"toast",Toast.LENGTH_LONG).show();
    }
}
