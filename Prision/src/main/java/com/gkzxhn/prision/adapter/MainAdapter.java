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
    public MainAdapter(Context context){
        this.mContext=context;

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.main_item_layout,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        MeetingEntity entity=mDatas.get(position);
//        holder.tvName.setText(entity.getName());
//        holder.tvTime.setText(entity.getTime());
//        holder.tvArea.setText(entity.getArea());
    }

    @Override
    public int getItemCount() {
//        return mDatas.size();
        return  10;
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
