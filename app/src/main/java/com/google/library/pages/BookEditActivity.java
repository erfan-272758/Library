package com.google.library.pages;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.library.ui.AnimationListener;
import com.google.library.ui.ChooseData;
import com.google.library.DataBase;
import com.google.library.R;
import com.google.library.dataclass.Book;
import com.google.library.dataclass.Person;
import com.google.library.ui.Design;
import com.google.library.ui.ProgressHandle;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class BookEditActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
    int id;
    int type,major;
    boolean insert;
    Holder holder;
    DataBase db;
    boolean img = false;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_edit);
        db = new DataBase(this);
        id = -1;
        if (getIntent() != null)
            id = getIntent().getIntExtra("id",-1);
        holder = new Holder();
        initHolder();
        if (id != -1){
            fill();
        }else {
            insert();
        }
    }
    private void initHolder(){
        holder.btn_barcode.setOnClickListener(this);
        holder.btn_plus.setOnClickListener(this);
        holder.btn_send.setOnClickListener(this);
        holder.spinner.setOnItemSelectedListener(this);
        holder.cb_barcode.setOnCheckedChangeListener(this);
        holder.spinner_major.setOnItemSelectedListener(this);
        holder.btn_his.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void fill(){
        holder.ll_content.setVisibility(View.GONE);
        insert = false;
        holder.cb_barcode.setVisibility(View.GONE);
        holder.tl_barcode.setVisibility(View.GONE);
        holder.btn_barcode.setEnabled(true);
        ProgressHandle.sendAnimOut(this,holder.btn_send,holder.pb, new AnimationListener.Anim(){
            @Override
            public void onEnd() {
                db.getBook(id,holder);
            }
        });
    }

    private void insert(){
        insert = true;
        holder.btn_barcode.setEnabled(false);
        holder.ll_person.setVisibility(View.GONE);
        holder.ll_land.setVisibility(View.GONE);
        holder.ll_borrow.setVisibility(View.GONE);
        holder.ll_state.setVisibility(View.GONE);
        holder.btn_his.setVisibility(View.GONE);
    }

    private void createBarcode(){
        if (Objects.requireNonNull(holder.edit_name.getText()).toString().trim().isEmpty() ||
                Objects.requireNonNull(holder.edit_year.getText()).toString().trim().isEmpty())
            return;
        String sb = String.valueOf(major * 100)
                + (type * 10)
                + holder.edit_year.getText().toString().trim()
                + id;
        generateBarcode(sb);
    }

    private void generateBarcode(String productId) {
        try {
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            Writer codeWriter;
            codeWriter = new Code128Writer();
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            BitMatrix byteMatrix = codeWriter.encode(productId, BarcodeFormat.CODE_128, (int) (300 * dm.density), (int) (150 * dm.density), hintMap);
            int width = byteMatrix.getWidth();
            int height = byteMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            save(bitmap,productId);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void save(Bitmap bit,String barcode){
        if (bit != null){
            File ff = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Library");
            if (!ff.exists()){
                if (!ff.mkdir())
                    Log.i("file_file","could not create");
                else
                    Log.i("file_file","could create");
            }
            File f = new File(ff,"barcode");
            if (!f.exists()){
                if (!f.mkdir())
                    Log.i("file_file","could not create");
                else
                    Log.i("file_file","could create");
            }
            OutputStream out;
            try {
                out = new FileOutputStream(new File(f,barcode+".png"));
                bit.compress(Bitmap.CompressFormat.PNG,100,out);
                Toast.makeText(this,f.getAbsolutePath()+"/"+barcode+".png",Toast.LENGTH_LONG).show();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(this,"no barcode",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_send:
                ProgressHandle.sendAnimOut(BookEditActivity.this, holder.btn_send, holder.pb, new AnimationListener.Anim() {
                    @Override
                    public void onEnd() {
                        if (Objects.requireNonNull(holder.edit_name.getText()).toString().trim().isEmpty() ||
                                Objects.requireNonNull(holder.edit_year.getText()).toString().trim().isEmpty()) {
                            ProgressHandle.sendAnimIn(BookEditActivity.this, holder.btn_send, holder.pb,null);
                            Toast.makeText(BookEditActivity.this, "اطلاعات ناقص است", Toast.LENGTH_SHORT).show();
                        }else {
                            if (insert){
                                db.insertBook(createBook(),holder.pb,holder.btn_send,holder);
                            }else {
                                if (img){
                                    db.updateBookAll(createBook(),holder.iv_book,holder.btn_send,holder.pb);
                                }else {
                                    db.updateBookData(createBook(),holder.btn_send,holder.pb);
                                }
                            }

                        }
                    }
                });
                break;
            case R.id.btn_plus:
                img = true;
                ChooseData.startCroppingRequest(this,null,false);
                break;
            case R.id.btn_barcode:
                createBarcode();
                break;
            case R.id.btn_history:
                Intent intent = new Intent(BookEditActivity.this,HistoryActivity.class);
                intent.putExtra("id",String.valueOf(id));
                intent.putExtra("bookHistory",true);
                startActivity(intent);
                break;
        }
    }

    private Book createBook() {

        String name = Objects.requireNonNull(holder.edit_name.getText()).toString().trim();
        String year = Objects.requireNonNull(holder.edit_year.getText()).toString().trim();
        Book b = new Book(name,type,major,year,null,id);

        if (holder.cb_barcode.isChecked())
            b.barcode = Objects.requireNonNull(holder.edit_barcode.getText()).toString().trim();
        else
            b.barcode = null;

        return b;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        ChooseData.handleResult(null,this,requestCode,resultCode,data,holder.iv_book);
        super.onActivityResult(requestCode, resultCode, data);
    }
//    private void saveImage(){
//        Bitmap bit;
//        if (holder.iv_book.getDrawable() == null){
//            holder.iv_book.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.book));
//        }
//        bit = ((BitmapDrawable)(holder.iv_book.getDrawable())).getBitmap();
//        if (bit == null){
//            bit = ((BitmapDrawable)(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.book)))).getBitmap();
//        }
//        File f = new File(getFilesDir().getAbsolutePath()+"/images");
//        if (!f.exists())
//            f.mkdir();
//        OutputStream out;
//        try {
//            out = new FileOutputStream(new File(f,id+".png"));
//            bit.compress(Bitmap.CompressFormat.PNG,80,out);
//            Toast.makeText(this,f.getAbsolutePath()+"/"+id+".png",Toast.LENGTH_LONG).show();
//            out.close();
//            book.imageUri = f.getAbsolutePath()+"/"+id+".png";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (adapterView.getId() == R.id.spinner){
            type = position +1;
        }else if (adapterView.getId() == R.id.spinner_type){
            major = position +1;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        holder.tl_barcode.setEnabled(b);
    }

    public class Holder{
        ProgressBar pb;
        MaterialButton btn_send,btn_plus,btn_barcode,btn_his;
        CheckBox cb_barcode;
        TextInputLayout tl_barcode;
        TextInputEditText edit_name,edit_year,edit_barcode;
        Spinner spinner, spinner_major;
        ImageView iv_book;
        TextView tv_borrow,tv_person,tv_land,tv_state;
        LinearLayout ll_borrow,ll_person,ll_land,ll_state,ll_content;
        Holder(){
            ll_content = findViewById(R.id.content);
            btn_send = findViewById(R.id.btn_send);
            btn_barcode = findViewById(R.id.btn_barcode);
            btn_plus = findViewById(R.id.btn_plus);
            edit_name = findViewById(R.id.edit_name);
            spinner = findViewById(R.id.spinner);
            iv_book = findViewById(R.id.iv_book);
            tv_borrow = findViewById(R.id.tv_timeBorrow);
            tv_land = findViewById(R.id.tv_timeLand);
            tv_person = findViewById(R.id.tv_personName);
            tv_state = findViewById(R.id.tv_state);
            ll_borrow = findViewById(R.id.ll_borrow);
            ll_land = findViewById(R.id.ll_land);
            ll_state = findViewById(R.id.ll_state);
            ll_person = findViewById(R.id.ll_person);
            spinner_major = findViewById(R.id.spinner_type);
            edit_year = findViewById(R.id.edit_year);
            btn_his = findViewById(R.id.btn_history);
            pb = findViewById(R.id.pb);
            tl_barcode = findViewById(R.id.tl_barcode);
            edit_barcode = findViewById(R.id.edit_barcode);
            cb_barcode = findViewById(R.id.ch_barcode);
            SpinnerAdapter adapter = ArrayAdapter.createFromResource(BookEditActivity.this, R.array.spinner_type_book,
                    android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            SpinnerAdapter adapter1 = ArrayAdapter.createFromResource(BookEditActivity.this, R.array.spinner_major,
                    android.R.layout.simple_spinner_dropdown_item);
            spinner_major.setAdapter(adapter1);
        }
        public void fill(Book book){
            Design.showImage(BookEditActivity.this,iv_book,book.imageUri,iv_book,R.drawable.book,null);
            holder.edit_name.setText(book.name);
            holder.spinner.setSelection(book.type - 1);
            if (!book.exist){
                holder.tv_borrow.setText(book.borrowLand.timeBorrow.toString());
                holder.ll_land.setVisibility(View.GONE);
                if (book.borrowLand != null)db.getPerson(book.borrowLand.idPerson,tv_person);
                holder.tv_state.setText("موجود نیست");
            }else if (book.borrowLand != null){
                holder.tv_borrow.setText(book.borrowLand.timeBorrow.toString());
                holder.tv_land.setText(book.borrowLand.timeLand.toString());
                if (book.borrowLand != null)db.getPerson(book.borrowLand.idPerson,tv_person);
                holder.tv_state.setText("موجود است");
            }else {
                holder.ll_person.setVisibility(View.GONE);
                holder.ll_land.setVisibility(View.GONE);
                holder.ll_borrow.setVisibility(View.GONE);
                holder.tv_state.setText("موجود است");
            }
            holder.edit_year.setText(book.year.trim());
            holder.spinner_major.setSelection(book.major - 1);
            ProgressHandle.sendAnimIn(BookEditActivity.this, btn_send, pb, new AnimationListener.Anim() {
                @Override
                public void onEnd() {
                    ll_content.setVisibility(View.VISIBLE);
                }
            });
        }
        public void save(int id){
            BookEditActivity.this.id = id;
            createBarcode();
        }
    }
}