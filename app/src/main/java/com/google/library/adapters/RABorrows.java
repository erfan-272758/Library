package com.google.library.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.library.DataBase;
import com.google.library.R;
import com.google.library.dataclass.Book;
import com.google.library.dataclass.BorrowLand;
import com.google.library.dataclass.Person;
import com.google.library.ui.Design;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RABorrows extends RecyclerView.Adapter<RABorrows.Holder> implements View.OnClickListener {
    AppCompatActivity context;
    ArrayList<Book> books;
    DataBase db;
    public RABorrows(AppCompatActivity context) {
        this.context = context;
        books = new ArrayList<>();
        db = new DataBase(context);
    }
    public void update(ArrayList<Book> newBooks){
        books.clear();
        books.addAll(newBooks);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.borrow_list,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {
        holder.pos = position;
        Book book = books.get(position);
        Design.showImage(context,holder.iv,book.imageUri,holder.iv,R.drawable.book,null);
        holder.tv_name.setText(book.name);
        holder.tv_barcode.setText(book.barcode);

        if (book.exist){
            holder.tv_state.setText("موجود است");
            holder.btn_land.setVisibility(View.GONE);
            holder.ll_borrow.setVisibility(View.VISIBLE);
            holder.edit_idPerson.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() == 10)
                        holder.btn_borrow.setEnabled(true);
                    else
                        holder.btn_borrow.setEnabled(false);
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            holder.tv_state.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
            holder.btn_borrow.setTag(holder);
            holder.btn_borrow.setOnClickListener(this);
        }else {
            holder.tv_state.setTextColor(ContextCompat.getColor(context, R.color.colorDarkRead));
            holder.tv_state.setText("موجود نیست");
            holder.ll_borrow.setVisibility(View.GONE);
            holder.btn_land.setVisibility(View.VISIBLE);
            holder.btn_land.setTag(holder);
            holder.btn_land.setOnClickListener(this);
        }
        String[] arr = context.getResources().getStringArray(R.array.spinner_type_book);
        String type = "انتشارات "+arr[book.type -1];
        String major = "رشته ";
        arr = context.getResources().getStringArray(R.array.spinner_major);
        if (book.major != Book.MINI_O && book.major != Book.MINI_T && book.major !=Book.OMOUMI){
            major += arr[book.major -1];
        }else {
            major = arr[book.major -1];
        }
        holder.tv_major.setText(major);
        holder.tv_year.setText(book.year.trim());
        holder.tv_type.setText(type);

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    @Override
    public void onClick(View view) {
        Holder holder = (Holder) view.getTag();
        switch (view.getId()){
            case R.id.btn_borrow:
                borrowBook(holder);
                break;
            case R.id.btn_land:
                landBook(holder);
                break;
        }
    }

    private void borrowBook(Holder holder) {
        String idCard =  Objects.requireNonNull(holder.edit_idPerson.getText()).toString().trim();
        if (idCard.length() != 10){
//            Toast.makeText(context, "شماره ملی ناقص است", Toast.LENGTH_SHORT).show();
            holder.edit_idPerson.setError("شماره ملی ناقص است");
            return;
        }
        holder.btn_borrow.setEnabled(false);
        BorrowLand bl = new BorrowLand(Calendar.getInstance().getTimeInMillis(),books.get(holder.pos).id,idCard);
        db.insetBorrow(bl,holder.btn_borrow,this);
    }
    private void landBook(Holder holder) {
        holder.btn_land.setEnabled(false);
        BorrowLand bl = new BorrowLand(Calendar.getInstance().getTimeInMillis(),books.get(holder.pos).id,
                books.get(holder.pos).borrowLand.idPerson);
        db.updateBorrow(bl,holder.btn_land,this);
    }
    public static class Holder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv_name,tv_barcode,tv_state,tv_type,tv_year, tv_major;
        MaterialButton btn_land,btn_borrow;
        TextInputEditText edit_idPerson;
        LinearLayout ll_borrow;
        int pos;
        public Holder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.image);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_barcode = itemView.findViewById(R.id.tv_barcode);
            tv_state = itemView.findViewById(R.id.tv_state);
            tv_type = itemView.findViewById(R.id.tv_type);
            btn_land = itemView.findViewById(R.id.btn_land);
            btn_borrow = itemView.findViewById(R.id.btn_borrow);
            edit_idPerson = itemView.findViewById(R.id.edit_idPerson);
            ll_borrow = itemView.findViewById(R.id.ll_borrow);
            tv_year = itemView.findViewById(R.id.tv_year);
            tv_major = itemView.findViewById(R.id.tv_major);
        }
    }
}
