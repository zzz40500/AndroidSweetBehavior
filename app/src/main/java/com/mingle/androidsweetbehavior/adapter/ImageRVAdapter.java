package com.mingle.androidsweetbehavior.adapter;

import android.support.annotation.NonNull;

import com.mingle.androidsweetbehavior.adapter.adapterItem.ImageAdapterItem;

import java.util.List;

import kale.adapter.AdapterItem;
import kale.adapter.recycler.CommonRcvAdapter;

/**
 * Created by zzz40500 on 15/11/16.
 */
public class ImageRVAdapter  extends CommonRcvAdapter {
    public ImageRVAdapter(@NonNull List data) {
        super(data);
    }

    @NonNull
    @Override
    public AdapterItem getItemView(Object o) {
        return new ImageAdapterItem();
    }
}
