package com.abhishek.abc.notes.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abhishek.abc.notes.R;
import com.abhishek.abc.notes.network.models.NotesModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<NotesModel> mList;

    public ListAdapter (Context context, ArrayList<NotesModel> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotesModel note = mList.get(position);
        holder.dot.setText(Html.fromHtml("&#8226;"));
        holder.dot.setTextColor(getRandomMaterialColor("400"));
        holder.note.setText(note.getNote());
        holder.time.setText(formatDate(note.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public int getRandomMaterialColor(String type) {
        int returnColor = 0;
        int arrayId = mContext.getResources().getIdentifier("mdcolor_" + type, "array", mContext.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = mContext.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    public String formatDate(String date) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = fmt.parse(date);
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d");
            return fmtOut.format(date);
        } catch (ParseException e) {

        }
        return "--";
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.note)
        TextView note;

        @BindView(R.id.dot)
        TextView dot;

        @BindView(R.id.timestamp)
        TextView time;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
