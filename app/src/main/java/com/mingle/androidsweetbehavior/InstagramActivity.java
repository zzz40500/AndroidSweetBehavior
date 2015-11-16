package com.mingle.androidsweetbehavior;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.InAppBarBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class InstagramActivity extends AppCompatActivity implements View.OnClickListener, ImageRVAdapter.OnRvItemClickListener {




    private RecyclerView mRV;

    private ImageView mContentIv;

    private AppBarLayout mAppBarLayout;
    private CoordinatorLayout mCoordinatorLayout;

    public static  void startActivity(Context ctx){
        ctx.startActivity(new Intent(ctx,InstagramActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);
        mRV= (RecyclerView) findViewById(R.id.rv);
        mAppBarLayout= (AppBarLayout) findViewById(R.id.appBarLayout);
        mCoordinatorLayout= (CoordinatorLayout) findViewById(R.id.coordinatorLY);
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

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,4,GridLayoutManager.VERTICAL,false);

        mRV.setLayoutManager(gridLayoutManager);

        mRV.setAdapter(new ImageRVAdapter(list,this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instagram, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this,"toast",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRvItemClick(View view, int position) {

//        CoordinatorLayout.LayoutParams layoutParams= (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
//       InAppBarBehavior  appBarBehavior= (InAppBarBehavior) layoutParams.getBehavior();
//        appBarBehavior.snapScroll(mCoordinatorLayout,mAppBarLayout,true);

        mAppBarLayout.setExpanded(true, true);

        Rect childRect=new Rect();
        view.getGlobalVisibleRect(childRect);
        Rect rVRect=new Rect();
        mRV.getGlobalVisibleRect(rVRect);



        mRV.smoothScrollBy(0,  childRect.bottom-view.getHeight()-rVRect.top );
    }
}
