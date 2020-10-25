package com.google.library.pages;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.library.DataBase;
import com.google.library.R;
import com.google.library.adapters.RAPersons;
import com.google.library.ui.ProgressHandle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PersonActivity extends AppCompatActivity implements View.OnClickListener, SearchView.OnQueryTextListener, DialogInterface.OnDismissListener, ProgressHandle.ChangeState {
    Holder holder;
    DataBase db;
    DialogPerson dialog;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DataBase(this);
        dialog = new DialogPerson(this,db);
        dialog.setOnDismissListener(this);
        holder = new Holder();
        holder.rv.setAdapter(holder.adapter);
        holder.rv.setLayoutManager(new LinearLayoutManager(this));
        holder.fab.setOnClickListener(this);
        ProgressHandle ph = new ProgressHandle(this,this,holder.pb_top,null,holder.rv);
        holder.rv.setOnTouchListener(ph);
        holder.sv.setOnQueryTextListener(this);
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

    }
    @Override
    protected void onResume() {
        super.onResume();
        if (holder.adapter.getItemCount() == 0){
            ProgressHandle.animIn(this,holder.pb_top);
            db.getAllPersons(holder.pb_top,holder.adapter,DataBase.LIMIT);
        }}

    @Override
    public void onClick(View view) {
        dialog.show();
       }

    @Override
    public void update() {
        db.getAllPersons(holder.pb_top,holder.adapter,DataBase.LIMIT);
    }

    @Override
    public void extra() {
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        db.getQueryPersons(query,holder.adapter,DataBase.LIMIT);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        db.getQueryPersons(newText,holder.adapter,DataBase.LIMIT);
        return false;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        db.getAllPersons(holder.pb_top,holder.adapter,DataBase.LIMIT);
    }


    class Holder{
        SearchView sv;
        RecyclerView rv;
        ProgressBar pb_top;
        FloatingActionButton fab;
        RAPersons adapter;
        Holder(){
            sv = findViewById(R.id.search);
            rv = findViewById(R.id.re_view);
            pb_top = findViewById(R.id.pb_top);
            fab = findViewById(R.id.fab);
            adapter = new RAPersons(PersonActivity.this);
        }
    }
}