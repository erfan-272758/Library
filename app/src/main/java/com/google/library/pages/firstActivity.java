package com.google.library.pages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import kotlin.jvm.functions.Function3;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.google.library.R;
import com.google.library.ui.Design;
import com.google.library.ui.ItemCustom;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView;

import java.util.Calendar;
import java.util.Objects;

public class firstActivity extends AppCompatActivity {
    DrawerLayout dl;
    NavHostFragment fragment;
    long lastBack = -1;
    public static final Long DOUBLE_CLICK = 800L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_activity);
        dl = findViewById(R.id.draw_layout);
        fragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_fragment_main);
        makeDrawer();
    }
    private void makeDrawer() {
        dl = findViewById(R.id.draw_layout);
        final MaterialDrawerSliderView sliderView = findViewById(R.id.slider);
        @SuppressLint("InflateParams")
        View v = LayoutInflater.from(this).inflate(R.layout.header_layout,sliderView,false);
        sliderView.setHeaderView(v);
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        sliderView.setCustomWidth((int) (dm.widthPixels * 0.65));

        String[] titles = getResources().getStringArray(R.array.draw_layout);
        int[] iconsId = getResources().getIntArray(R.array.icon_id);
        for (int i = 0; i < titles.length; i++) {
            ItemCustom item = new ItemCustom(this,titles[i],iconsId[i]);
            item.setIdentifier(i);
            sliderView.getItemAdapter().add(item);
        }
        sliderView.setOnDrawerItemClickListener(new Function3<View, IDrawerItem<?>, Integer, Boolean>() {
            @Override
            public Boolean invoke(View view, IDrawerItem<?> iDrawerItem, Integer integer) {
                ItemCustom itemCustom = (ItemCustom) iDrawerItem;
                itemCustom.onClick(view);
                switch ((int) iDrawerItem.getIdentifier()){
                    case 0:
                        fragment.getNavController().navigate(R.id.mainActivity,null, Design.getNavOptionsEtS(firstActivity.this));
                        break;
                    case 1:
                        fragment.getNavController().navigate(R.id.bookEditActivity,null, Design.getNavOptionsEtS(firstActivity.this));
                        break;
                    case 2:
                        fragment.getNavController().navigate(R.id.personActivity,null, Design.getNavOptionsEtS(firstActivity.this));
                        break;
                    case 3:
                        fragment.getNavController().navigate(R.id.borrowLandActivity,null, Design.getNavOptionsEtS(firstActivity.this));
                        break;
                }
                dl.closeDrawers();
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (dl.isDrawerOpen(GravityCompat.END)){
            dl.closeDrawers();
        }else {
            int id = Objects.requireNonNull(fragment.getNavController().getCurrentDestination()).getId();
            if (id != R.id.mainActivity){
                fragment.getNavController().navigate(R.id.mainActivity,null, Design.getNavOptionsStE(firstActivity.this));
            }else {
                exitApp();
            }
        }
    }

    public void exitApp(){
        if (lastBack != -1){
            lastBack = Calendar.getInstance().getTimeInMillis();
        }else {
            long l = Calendar.getInstance().getTimeInMillis();
            if (l -  lastBack <= DOUBLE_CLICK){
                finishAffinity();
            }else {
                Toast.makeText(this, R.string.des_exit, Toast.LENGTH_SHORT).show();
                lastBack = l;
            }
        }
    }
}