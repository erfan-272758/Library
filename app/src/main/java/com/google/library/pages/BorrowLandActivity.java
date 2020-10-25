package com.google.library.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.library.DataBase;
import com.google.library.R;
import com.google.library.adapters.RABorrows;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BorrowLandActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, View.OnClickListener {
    DataBase db;
    Holder holder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.borrow_land);
        db = new DataBase(this);
        holder = new Holder();
        initHolder();
    }

    private void initHolder() {
        holder.sv.setOnQueryTextListener(this);
        holder.btn_scan.setOnClickListener(this);
        holder.rv.setAdapter(holder.adapter);
        holder.rv.setLayoutManager(new LinearLayoutManager(this));
        holder.fab.setOnClickListener(this);
        holder.rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0){
                    holder.fab.shrink();
                }else if (dy < 0){
                    holder.fab.extend();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_scan:
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setCameraId(0);
                intentIntegrator.initiateScan();
                break;
            case R.id.fab:
                holder.dialog.show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            String productId = intentResult.getContents();
            holder.sv.setQuery(productId,true);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class Holder{
        SearchView sv;
        MaterialButton btn_scan;
        ExtendedFloatingActionButton fab;
        RecyclerView rv;
        RABorrows adapter;
        DialogPerson dialog;
        Holder(){
            sv = findViewById(R.id.search);
            btn_scan = findViewById(R.id.btn_scan);
            fab = findViewById(R.id.fab);
            rv = findViewById(R.id.re_view);
            adapter = new RABorrows(BorrowLandActivity.this);
            dialog = new DialogPerson(BorrowLandActivity.this,db);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (holder != null)
            outState.putString("query",holder.sv.getQuery().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        String str = savedInstanceState.getString("query","");
        if (!str.isEmpty() && holder != null)
            holder.sv.setQuery(str,true);
        super.onRestoreInstanceState(savedInstanceState);
    }
}