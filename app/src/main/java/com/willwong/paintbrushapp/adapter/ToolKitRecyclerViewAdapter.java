package com.willwong.paintbrushapp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.willwong.paintbrushapp.R;

/**
 * Created by WillWong on 01/12/19
 */
public class ToolKitRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private String[] ToolKitList;

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    //interface callback object to transmit data to container activity.
    private ToolKitViewHolder.ToolKitDataTransmitter transmitter;

    public ToolKitRecyclerViewAdapter(Context context, ToolKitViewHolder.ToolKitDataTransmitter transmitter) {
        mContext = context;
        this.transmitter = transmitter;
        ToolKitList = mContext.getResources().getStringArray(R.array.toolkit_icons);
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.horizontal_tool_item, viewGroup, false);
        ToolKitViewHolder vh = new ToolKitViewHolder(view, mContext, transmitter);
        return vh;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int pos) {
        ToolKitViewHolder toolHolder = (ToolKitViewHolder) holder;
        toolHolder.toolImageView.setImageResource(mContext.getResources().getIdentifier(ToolKitList[pos],"drawable", mContext.getPackageName()));
    }
    @Override
    public int getItemCount() {
        return ToolKitList.length;
    }

    public static class ToolKitViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView toolImageView;
        private Context context;
        private ToolKitDataTransmitter toolKitData;

        public interface ToolKitDataTransmitter {
            public void getPos(int pos);
        }
        public ToolKitViewHolder(View view,Context context, ToolKitDataTransmitter toolKitData) {
            super(view);
            toolImageView = view.findViewById(R.id.toolkit_imageview);
            this.context = context;
            this.toolKitData = toolKitData;
            view.setOnClickListener(this);
            view.setClickable(true);
        }


        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            if (toolKitData != null) {
                toolKitData.getPos(getAdapterPosition());
            }
        }
    }
}
