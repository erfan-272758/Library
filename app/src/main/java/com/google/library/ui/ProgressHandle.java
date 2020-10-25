package com.google.library.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.library.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ProgressHandle implements View.OnTouchListener {
    ChangeState cs;
    float oldY =0;
    boolean visible_up = false,visible_down = false;
    ProgressBar pb_down,pb_top;
    RecyclerView rv;
    Animation anim_in;
    public ProgressHandle(Context context, ChangeState cs, @NonNull ProgressBar pb_top, @Nullable ProgressBar pb_down, RecyclerView rv){
        this.cs = cs;
        this.pb_top = pb_top;
        this.pb_down = pb_down;
        this.rv = rv;
        anim_in = AnimationUtils.loadAnimation(context,R.anim.anim_in);
    }

    public static void animOut(Context context, final ProgressBar pb){
        if (pb.getVisibility() != View.VISIBLE)
            return;
        pb.clearAnimation();
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_out);
        anim.setAnimationListener(new AnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                pb.invalidate();
                pb.setVisibility(View.GONE);
                super.onAnimationEnd(animation);
            }
        });
        pb.startAnimation(anim);
    }
    public static void animIn(Context context,ProgressBar pb){
        if (pb.getVisibility() != View.GONE)
            return;
        pb.clearAnimation();
        pb.setVisibility(View.VISIBLE);
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.anim_in);
        pb.startAnimation(anim);
        pb.invalidate();
    }
    private void animIn(ProgressBar pb, final ChangeState cs, final boolean update){
        pb.clearAnimation();
        pb.setVisibility(View.VISIBLE);
        anim_in.setAnimationListener(new AnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                if (update)
                    cs.update();
                else
                    cs.extra();
                super.onAnimationEnd(animation);
            }
        });
        pb.startAnimation(anim_in);
        pb.invalidate();
    }

    public static void sendAnimIn(Context context, Button btn_send, final ProgressBar pb, final AnimationListener.Anim act){
        if (btn_send.getVisibility() == View.VISIBLE)
            return;
        btn_send.clearAnimation();
        pb.clearAnimation();
        Animation animIn = AnimationUtils.loadAnimation(context,R.anim.translate_in);
        Animation animOut = AnimationUtils.loadAnimation(context,R.anim.translate_out);
        btn_send.setVisibility(View.VISIBLE);
        animOut.setAnimationListener(new AnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                pb.setVisibility(View.GONE);
                if (act != null)act.onEnd();
                super.onAnimationEnd(animation);
            }
        });
        btn_send.startAnimation(animIn);
        pb.startAnimation(animOut);
    }
    public static void sendAnimOut(Context context, final Button btn_send, final ProgressBar pb, final AnimationListener.Anim act){
        if (pb.getVisibility() == View.VISIBLE)
            return;
        btn_send.clearAnimation();
        pb.clearAnimation();
        Animation animIn = AnimationUtils.loadAnimation(context,R.anim.translate_in);
        Animation animOut = AnimationUtils.loadAnimation(context, R.anim.translate_out);
        pb.setVisibility(View.VISIBLE);
        animOut.setAnimationListener(new AnimationListener(){
            @Override
            public void onAnimationEnd(Animation animation) {
                btn_send.setVisibility(View.GONE);
                if (act != null)act.onEnd();
                super.onAnimationEnd(animation);
            }
        });
        btn_send.startAnimation(animOut);
        pb.startAnimation(animIn);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        float minDis = (float) rv.getHeight() /6;
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                oldY = motionEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dis = motionEvent.getY() - oldY;
                if (pb_top.getVisibility() == View.GONE){
                    if (!rv.canScrollVertically(-1) && dis > minDis){
                        visible_up = true;
                    }
                }
                if (pb_down != null){
                    if (pb_down.getVisibility() == View.GONE){
                        if (!rv.canScrollVertically(1) && -dis > minDis){
                            visible_down = true;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (pb_down != null){
                    if (visible_down){
                        animIn(pb_down,cs,false);
                        visible_down = false;
                    }
                }
                if (visible_up){
                    animIn(pb_top,cs,true);
                    visible_up = false;
                }
        }
        return false;
    }
    public interface ChangeState{
        void update();
        void extra();
    }
}
