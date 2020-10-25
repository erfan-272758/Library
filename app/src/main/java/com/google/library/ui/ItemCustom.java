package com.google.library.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.library.R;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import org.jetbrains.annotations.NotNull;

import java.time.format.TextStyle;
import java.util.List;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

public class ItemCustom extends PrimaryDrawerItem {
    private String title;
    private @DrawableRes
    int iconId;
    private Context context;
    public ItemCustom(Context context,String title, int iconId) {
        this.context  = context;
        this.title = title;
        this.iconId = iconId;
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull ViewGroup parent) {
        Log.i("item_view","getViewHolder 2");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu,parent,false);
        return new Holder(view);
    }

    @Override
    public void bindView(@NotNull ViewHolder holder, @NotNull List<?> payloads) {
        Log.i("item_view","bindView 1");
        super.bindView(holder, payloads);
        Holder holder1 = (Holder) holder;
        holder1.iv.setVisibility(View.VISIBLE);
        holder1.iv.setImageDrawable(ContextCompat.getDrawable(context,iconId));
        holder1.tv.setText(title);
        holder1.itemView.setTag(holder1);
    }

    public void onClick(View view){
        Holder holder = (Holder) view.getTag();
      holder.iv.setImageDrawable(ContextCompat.getDrawable(context,(R.drawable.ic_baseline_arrow_left_24)));
        holder.tv.setTextColor(Color.BLACK);
    }

    static class Holder extends ViewHolder{
        ImageView iv;
        TextView tv;
        public Holder(@NotNull View view) {
            super(view);
            iv = view.findViewById(R.id.material_drawer_icon);
            tv = view.findViewById(R.id.material_drawer_name);
         }
    }
}
