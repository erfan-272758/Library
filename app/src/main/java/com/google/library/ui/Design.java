package com.google.library.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.LayoutDirection;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.library.R;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.NavOptions;
import saman.zamani.persiandate.PersianDate;
import saman.zamani.persiandate.PersianDateFormat;

public class Design {
    public static void showImage(final Context context, final View v , String uri, final ImageView im,
                                 @DrawableRes int idErrorLoad , @Nullable final ProgressBar pb){
        if (pb!= null)
            pb.setVisibility(View.VISIBLE);
        v.invalidate();
        Glide.with(context)
                .asBitmap()
                .load(Uri.parse(uri))
                .thumbnail(0.5f)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .addListener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        if (pb!=null)
                            pb.setVisibility(View.GONE);
                        v.invalidate();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (pb!=null){
                            pb.setVisibility(View.GONE);
                            v.invalidate();
                        }
                        Log.i("my-data","onReady");
                        return false;
                    }
                })
                .error(idErrorLoad)
                .into(im);
        v.invalidate();
    }

    public interface PopupAct{
        void remove(int pos);
    }
    public static PopupMenu createMenu(Context context, final View v, final PopupAct act){
        final PopupMenu pm = new PopupMenu(context,v);
        pm.inflate(R.menu.more_post);
        pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int pos = (int) v.getTag();
                if (item.getItemId() == R.id.remove){
                    act.remove(pos);
                    pm.dismiss();
                }
                return false;
            }
        });
        return pm;
    }
    public static void changeDate (PersianDate pd){
        PersianDateFormat pdf = new PersianDateFormat();
        pdf.format(pd);
    }
    public static NavOptions getNavOptionsTD() {

        return new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_top_center)
                .setExitAnim(R.anim.slide_center_down)
                .build();
    }
    public static NavOptions getNavOptionsDT() {

        return new NavOptions.Builder()
                .setEnterAnim(R.anim.slide_down_center)
                .setExitAnim(R.anim.slide_center_top)
                .build();
    }
    public static NavOptions getNavOptionsEtS(Context context) {

        boolean directionLtR = context.getResources().getConfiguration().getLayoutDirection() == LayoutDirection.LTR;
        int idEnter = directionLtR ? R.anim.slide_right_center : R.anim.slide_left_center;
        int idExit = directionLtR ? R.anim.slide_center_left : R.anim.slide_center_right;

        return new NavOptions.Builder()
                .setEnterAnim(idEnter)
                .setExitAnim(idExit)
                .build();
    }
    public static NavOptions getNavOptionsStE(Context context) {

        boolean directionLtR = context.getResources().getConfiguration().getLayoutDirection() == LayoutDirection.LTR;
        int idEnter = !directionLtR ? R.anim.slide_right_center : R.anim.slide_left_center;
        int idExit = !directionLtR ? R.anim.slide_center_left : R.anim.slide_center_right;

        return new NavOptions.Builder()
                .setEnterAnim(idEnter)
                .setExitAnim(idExit)
                .build();
    }

}
