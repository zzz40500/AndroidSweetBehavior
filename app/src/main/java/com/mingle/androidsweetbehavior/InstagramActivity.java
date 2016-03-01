package com.mingle.androidsweetbehavior;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.InAppBarBehavior;
import android.support.design.widget.InNestChildBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mingle.androidsweetbehavior.adapter.ImageRVAdapter;
import com.mingle.androidsweetbehavior.entity.ImageEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InstagramActivity extends AppCompatActivity implements View.OnClickListener, ImageRVAdapter.OnRvItemClickListener {


    private RecyclerView mRV;

    private ImageView mContentIv;

    private AppBarLayout mAppBarLayout;
    private CoordinatorLayout mCoordinatorLayout;
    private GridLayoutManager gridLayoutManager;
    private int totalScroll;

    public static void startActivity(Context ctx) {
        ctx.startActivity(new Intent(ctx, InstagramActivity.class));
    }

    private int[] randomIntArray = new int[]{R.mipmap.ic_01, R.mipmap.ic_03,

            R.mipmap.ic_06, R.mipmap.ic_7,
            R.mipmap.ic_08, R.mipmap.ic_10,
            R.mipmap.ic_11, R.mipmap.ic_01,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram);
        mRV = (RecyclerView) findViewById(R.id.rv);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mContentIv = (ImageView) findViewById(R.id.contentIv);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLY);
        mContentIv.setOnClickListener(this);


        List<ImageEntity> list = new ArrayList<>();
        list.add(new ImageEntity(randomIntArray[0]));
        for (int i = 0; i < 78; i++) {
            ImageEntity item = new ImageEntity();
            item.resId = randomIntArray[new Random().nextInt(8)];
            list.add(item);
        }

        gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);

        mRV.setLayoutManager(gridLayoutManager);


        mRV.setAdapter(new ImageRVAdapter(list, this));
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
        Toast.makeText(this, "小样,点击了图片", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRvItemClick(View view, Object o, int position) {

        ImageEntity imageEntity = (ImageEntity) o;
        Glide.with(this).load(imageEntity.resId).into(mContentIv);
        InAppBarBehavior appBarBehavior = (InAppBarBehavior) ((CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams()).getBehavior();
        appBarBehavior.setExpanded(true, true);

        InNestChildBehavior inNestChildBehavior= (InNestChildBehavior) ((CoordinatorLayout.LayoutParams)mRV.getLayoutParams()).getBehavior();
        inNestChildBehavior.smoothScrollToView(view, mRV);
    }


}
