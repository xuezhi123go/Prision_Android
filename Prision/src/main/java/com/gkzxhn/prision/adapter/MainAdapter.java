package com.gkzxhn.prision.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.entity.MeetingEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raleigh.Luo on 17/4/11.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder>{
    private Context mContext;
    private List<MeetingEntity> mDatas=new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null && !onItemClickListener.equals("null"))
            this.onItemClickListener = onItemClickListener;
    }

    public MainAdapter(Context context){
        this.mContext=context;

    }
    public void updateItems(List<MeetingEntity> mDatas){
        this.mDatas.clear();
        if(mDatas!=null&&mDatas.size()>0){
            this.mDatas=mDatas;
        }
        notifyDataSetChanged();

    }
    public String getItemsId(int positon){
        return mDatas.get(positon).getId();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.main_item_layout,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MeetingEntity entity=mDatas.get(position);
        holder.tvName.setText(entity.getName());
        holder.tvTime.setText(entity.getTime());
        holder.tvArea.setText(entity.getArea());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null)onItemClickListener.onClickListener(v,position);
            }
        });
        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null)onItemClickListener.onClickListener(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTime,tvName,tvArea,tvCancel;
        public ViewHolder(View itemView) {
            super(itemView);
            tvTime= (TextView) itemView.findViewById(R.id.main_item_layout_tv_time);
            tvName= (TextView) itemView.findViewById(R.id.main_item_layout_tv_name);
            tvArea= (TextView) itemView.findViewById(R.id.main_item_layout_tv_prison_area);
            tvCancel= (TextView) itemView.findViewById(R.id.main_item_layout_tv_cancel);
        }
    }
}
