package com.mingle.androidsweetbehavior.adapter.adapterItem;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mingle.androidsweetbehavior.R;
import com.mingle.androidsweetbehavior.adapter.ImageRVAdapter;
import com.mingle.androidsweetbehavior.entity.ImageEntity;

import kale.adapter.AdapterItem;

/**
 * Created by zzz40500 on 15/11/16.
 */
public class ImageAdapterItem implements AdapterItem<ImageEntity> {

    private ImageRVAdapter.OnRvItemClickListener mOnRvItemClickListener;
    private View contentView;

    private ImageView mImageView;
    public ImageAdapterItem(ImageRVAdapter.OnRvItemClickListener onRvItemClickListener) {
        mOnRvItemClickListener=onRvItemClickListener;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.item_image;
    }


    @Override
    public void onBindViews(View view) {

        mImageView= (ImageView) view.findViewById(R.id.imageIv);

        contentView=view;
    }

    @Override
    public void onSetViews() {

    }

    @Override
    public void onUpdateViews(final ImageEntity o, final int i) {



        Glide.with(contentView.getContext()).load(o.resId).into(mImageView);
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnRvItemClickListener.onRvItemClick(v,o,i);
            }
        });
    }
}
