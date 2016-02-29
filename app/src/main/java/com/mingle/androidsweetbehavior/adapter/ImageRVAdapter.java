package com.mingle.androidsweetbehavior.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mingle.androidsweetbehavior.R;
import com.mingle.androidsweetbehavior.entity.ImageEntity;

import java.util.List;

/**
 * Created by zzz40500 on 15/11/16.
 */
public class ImageRVAdapter  extends RecyclerView.Adapter<ImageRVAdapter.VH> {


    private OnRvItemClickListener mOnRvItemClickListener;

    private List<ImageEntity> mData;

    public ImageRVAdapter(@NonNull List<ImageEntity> data,OnRvItemClickListener onRvItemClickListener) {
        mData=data;
        mOnRvItemClickListener=onRvItemClickListener;
    }



    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image,parent,false));
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        Glide.with(holder.itemView.getContext()).load(mData.get(position).resId).into(holder.mIv);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnRvItemClickListener.onRvItemClick(v,mData.get(position),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public interface OnRvItemClickListener {
        void  onRvItemClick(View view,Object o,int position);
    }
    public  static class VH extends RecyclerView.ViewHolder {

        ImageView mIv;
        public VH(View itemView) {
            super(itemView);
            mIv= (ImageView) itemView.findViewById(R.id.imageIv);
        }
    }
}
