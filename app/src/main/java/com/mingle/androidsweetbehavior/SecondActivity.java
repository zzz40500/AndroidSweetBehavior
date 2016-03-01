package com.mingle.androidsweetbehavior;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.SheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class SecondActivity extends AppCompatActivity {

    private SheetBehavior<View> mSheetBehavior;

    public static void startActivity(Context ctx) {
        ctx.startActivity(new Intent(ctx, SecondActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        View view=findViewById(R.id.design_bottom_sheet);


        mSheetBehavior =SheetBehavior.from(view);
//        mSheetBehavior.setHideable(true);
        mSheetBehavior.setBottomSheetCallback(new SheetBehavior.SheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, @SheetBehavior.State int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                Log.e("dim",slideOffset+"");
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_second, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_switch) {
           if( mSheetBehavior.getSlideModel()== SheetBehavior.TOP_SHEET){
               mSheetBehavior.setSlideModel(SheetBehavior.BOTTOM_SHEET);
           }else {
               mSheetBehavior.setSlideModel(SheetBehavior.TOP_SHEET);
           }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
