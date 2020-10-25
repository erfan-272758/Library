package com.google.library.pages;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.library.DataBase;
import com.google.library.R;
import com.google.library.adapters.RABooks;
import com.google.library.ui.ProgressHandle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.widget.MaterialDrawerSliderView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kotlin.jvm.functions.Function3;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener, ProgressHandle.ChangeState {
    Holder holder;
    DataBase db;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},123);
        }
        db = new DataBase(this);
        holder = new Holder();
        holder.rv.setAdapter(holder.adapter);
        holder.rv.setLayoutManager(new LinearLayoutManager(this));
        ProgressHandle ph = new ProgressHandle(this,this,holder.pb_top,null,holder.rv);
        holder.rv.setOnTouchListener(ph);
        holder.fab.setOnClickListener(this);
        holder.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0){
                    holder.fab.hide();
                }else if (dy < 0){
                    holder.fab.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        holder.sv.setOnQueryTextListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (holder.adapter.getItemCount() == 0){
            ProgressHandle.animIn(this,holder.pb_top);
            db.getAllBooks(holder.pb_top,holder.adapter,DataBase.LIMIT);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MainActivity.this,BookEditActivity.class);
        intent.putExtra("id",-1);
        startActivity(intent);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        db.getQueryBooks(query,holder.adapter,DataBase.LIMIT);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        db.getQueryBooks(newText,holder.adapter,DataBase.LIMIT);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 123){
            if (resultCode != AppCompatActivity.RESULT_OK)
                finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void update() {
        db.getAllBooks(holder.pb_top,holder.adapter,DataBase.LIMIT);
    }

    @Override
    public void extra() {

    }

    class Holder{
        SearchView sv;
        RecyclerView rv;
        ProgressBar pb_top;
        FloatingActionButton fab;
        RABooks adapter;
        Holder(){
            sv = findViewById(R.id.search);
            rv = findViewById(R.id.re_view);
            pb_top = findViewById(R.id.pb_top);
            fab = findViewById(R.id.fab);
            adapter = new RABooks(MainActivity.this);
        }
    }
}