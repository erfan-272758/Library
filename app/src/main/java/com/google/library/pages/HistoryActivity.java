package com.google.library.pages;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.library.DataBase;
import com.google.library.R;
import com.google.library.adapters.RAHistory;
import com.google.library.dataclass.Book;
import com.google.library.dataclass.BorrowLand;
import com.google.library.dataclass.Person;
import com.google.library.dataclass.Share;
import com.google.library.ui.ProgressHandle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {
    boolean historyBook;
    boolean historyShare;
    String id;
    Holder holder;
    DataBase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        db = new DataBase(this);
        initIntent();
        holder = new Holder();
        holder.rv.setAdapter(holder.adapter);
        holder.rv.setLayoutManager(new LinearLayoutManager(this));
        initAppBar();
    }

    private void initAppBar() {
        holder.btn_pdf.setOnClickListener(this);
        if (historyBook){
            new DataBase(this).setBookImage(Integer.parseInt(id),holder.iv);
            }else {
            holder.iv.setVisibility(View.GONE);
        }
    }

    private void initIntent() {
        id = getIntent().getStringExtra("id");
        historyBook = getIntent().getBooleanExtra("bookHistory",false);
        historyShare = getIntent().getBooleanExtra("shareHistory",false);
    }

    @Override
    protected void onResume() {
        if (holder.adapter.getItemCount() == 0){
            ProgressHandle.animIn(this,holder.pb);
            if (!historyShare)
                db.getBorrow(holder.pb,holder.adapter,id,historyBook);
            else
                db.getShares(holder.pb,holder.adapter,id);
        }
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        stringToText(makeText());
    }
    public void stringToText(String data)  {
        File ff = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Library");
        if (!ff.exists()){
            if (!ff.mkdirs())
                Log.i("file_file","could not create");
            else
                Log.i("file_file","could create");
        }
        File f = new File(ff,"text");
        if (!f.exists()){
            if (!f.mkdirs())
                Log.i("file_file","could not create");
            else
                Log.i("file_file","could create");
        }
        try {
            File fp = new File(f,id+".txt");
            FileOutputStream fOut = new FileOutputStream(fp);
            fOut.write(data.getBytes());
            fOut.close();
            Toast.makeText(this, fp.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            Log.i("error", Objects.requireNonNull(e.getLocalizedMessage()));
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private String makeText() {
        StringBuilder str = new StringBuilder();
        if (historyBook){
            for (int i = 0; i < holder.adapter.borrowLands.size(); i++) {
                BorrowLand bl = holder.adapter.borrowLands.get(i);
                Person p = holder.adapter.people.get(i);
                str.append("مشخصات شخص: ")
                        .append(p.firstName)
                        .append(" ")
                        .append(p.lastName)
                        .append("   ")
                        .append(p.celPhone)
                        .append("   ")
                        .append(p.idCard)
                        .append(" ")
                        .append(p.parentName)
                        .append("   ")
                        .append(p.parentPhone)
                        .append("   ")
                        .append(p.schoolName)
                        .append("   ")
                        .append(p.address)
                        .append('\n');
                str.append("زمان قرض: ")
                        .append(bl.timeBorrow.toString())
                        .append('\n');
                if (bl.timeLand != null){
                    str.append("زمان تحویل: ")
                            .append(bl.timeLand.toString())
                            .append('\n');
                }
                str.append('\n');
            }
        }else if (!historyShare){
            for (int i = 0; i < holder.adapter.borrowLands.size(); i++) {
                BorrowLand bl = holder.adapter.borrowLands.get(i);
                Book b = holder.adapter.books.get(i);
                str.append("مشخصات کتاب: ")
                        .append(b.name)
                        .append("    ")
                        .append(b.barcode)
                        .append('\n');
                str.append("زمان قرض: ")
                        .append(bl.timeBorrow.toString())
                .append('\n');
                if (bl.timeLand != null){
                    str.append("زمان تحویل: ")
                            .append(bl.timeLand.toString())
                    .append('\n');
                }
                str.append('\n');
            }
        }else {
            for (Share share:holder.adapter.shares) {
                str.append("مشخصات اشتراک:")
                        .append("    ")
                        .append("مدت زمان: ")
                        .append(share.type)
                        .append(" ماهه")
                        .append("    ")
                        .append("زمان شروع: ")
                        .append(share.timeStart)
                        .append("    ")
                        .append("زمان پایان: ")
                        .append(share.timeEnd)
                        .append('\n');
            }
        }
        return str.toString();
    }
    class Holder{
        ImageView iv;
        RecyclerView rv;
        RAHistory adapter;
        FloatingActionButton btn_pdf;
        ProgressBar pb;
        Holder(){
            rv = findViewById(R.id.re_view);
            adapter = new RAHistory(HistoryActivity.this,historyBook,historyShare,id);
            iv = findViewById(R.id.image);
            btn_pdf = findViewById(R.id.btn_pdf);
            pb = findViewById(R.id.pb);
        }
    }
}