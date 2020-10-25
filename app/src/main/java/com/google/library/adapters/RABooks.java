package com.google.library.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.library.DataBase;
import com.google.library.pages.BookEditActivity;
import com.google.library.ui.Design;
import com.google.library.R;
import com.google.library.dataclass.Book;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RABooks extends RecyclerView.Adapter<RABooks.Holder> implements View.OnClickListener, View.OnLongClickListener, Design.PopupAct {
    AppCompatActivity context;
    DataBase db;
    ArrayList<Book> books;
    public RABooks(AppCompatActivity context) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.book_list,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Book book = books.get(position);
        Design.showImage(context,holder.iv,book.imageUri,holder.iv,R.drawable.book,null);
        holder.tv_name.setText(book.name);
        holder.tv_barcode.setText(book.barcode);
        if (book.exist){
            holder.tv_state.setText("موجود است");
            holder.tv_state.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
        }else {
            holder.tv_state.setTextColor(ContextCompat.getColor(context, R.color.colorDarkRead));
            holder.tv_state.setText("موجود نیست");
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
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    @Override
    public void onClick(View view) {
        int pos = (int) view.getTag();
        Intent intent = new Intent(context, BookEditActivity.class);
        intent.putExtra("id",books.get(pos).id);
        context.startActivity(intent);
    }

    @Override
    public boolean onLongClick(View view) {
        Design.createMenu(context,view,this).show();
        return false;
    }

    @Override
    public void remove(int pos) {
        Book book = books.get(pos);
        db.deleteBook(book.id,this,pos);
      }
    public void delete(int index,boolean suc){
        Book book = books.get(index);
        if (suc){
            Toast.makeText(context,book.name + "delete correctly",Toast.LENGTH_SHORT).show();
            books.remove(index);
            notifyDataSetChanged();
        }else {
            Toast.makeText(context,book.name + "could'nt delete correctly",Toast.LENGTH_SHORT).show();
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tv_name,tv_barcode,tv_state,tv_type,tv_year, tv_major;
        public Holder(@NonNull View itemView) {
            super(itemView);
            iv = itemView.findViewById(R.id.image);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_barcode = itemView.findViewById(R.id.tv_barcode);
            tv_state = itemView.findViewById(R.id.tv_state);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_year = itemView.findViewById(R.id.tv_year);
            tv_major = itemView.findViewById(R.id.tv_major);
        }
    }
}
