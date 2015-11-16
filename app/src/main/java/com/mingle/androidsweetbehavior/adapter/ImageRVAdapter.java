package com.mingle.androidsweetbehavior.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import com.mingle.androidsweetbehavior.adapter.adapterItem.ImageAdapterItem;

import java.util.List;

import kale.adapter.AdapterItem;
import kale.adapter.recycler.CommonRcvAdapter;

/**
 * Created by zzz40500 on 15/11/16.
 */
public class ImageRVAdapter  extends CommonRcvAdapter {


    private OnRvItemClickListener mOnRvItemClickListener;


    public ImageRVAdapter(@NonNull List data,OnRvItemClickListener onRvItemClickListener) {
        super(data);
        mOnRvItemClickListener=onRvItemClickListener;
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object o) {
        return new ImageAdapterItem(mOnRvItemClickListener);
    }


    public interface OnRvItemClickListener {
        void  onRvItemClick(View view,Object o,int position);
    }
}
