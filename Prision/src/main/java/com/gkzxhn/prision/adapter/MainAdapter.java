package com.gkzxhn.prision.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
    private int mCurrentIndex=-1;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener != null && !onItemClickListener.equals("null"))
            this.onItemClickListener = onItemClickListener;
    }

    public MainAdapter(Context context){
        this.mContext=context;

    }
    public void removeCurrentItem(){
        this.mDatas.remove(mCurrentIndex);
        notifyItemRemoved(mCurrentIndex);
    }
    public void updateItems(List<MeetingEntity> mDatas){
        this.mDatas.clear();
        if(mDatas!=null&&mDatas.size()>0){
            this.mDatas=mDatas;
        }
        notifyDataSetChanged();

    }
    public String getCurrentId(){
        return mDatas.get(mCurrentIndex).getId();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.main_item_layout,null);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        MeetingEntity entity=mDatas.get(position);
        holder.tvName.setText(entity.getName());
        String meetingTime=entity.getTime();
        String formateTime=meetingTime.substring(meetingTime.length()-11,meetingTime.length());
        holder.tvTime.setText(formateTime);
        holder.tvArea.setText(entity.getArea());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex=position;
                if(onItemClickListener!=null)onItemClickListener.onClickListener(v,position);
            }
        });
        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex=position;
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
