package com.mingle.androidsweetbehavior.adapter.adapterItem;

import android.view.View;

import com.mingle.androidsweetbehavior.R;
import com.mingle.androidsweetbehavior.adapter.ImageRVAdapter;

import kale.adapter.AdapterItem;

/**
 * Created by zzz40500 on 15/11/16.
 */
public class ImageAdapterItem implements AdapterItem {

    private ImageRVAdapter.OnRvItemClickListener mOnRvItemClickListener;
    private View contentView;

    public ImageAdapterItem(ImageRVAdapter.OnRvItemClickListener onRvItemClickListener) {
        mOnRvItemClickListener=onRvItemClickListener;

    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_image;
    }


    @Override
    public void onBindViews(View view) {

        contentView=view;
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(Object o, final int i) {
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnRvItemClickListener.onRvItemClick(v,i);
            }
        });
    }
}
