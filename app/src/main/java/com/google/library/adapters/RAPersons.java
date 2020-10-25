package com.google.library.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.library.DataBase;
import com.google.library.dataclass.Book;
import com.google.library.ui.Design;
import com.google.library.pages.DialogPerson;
import com.google.library.R;
import com.google.library.dataclass.Person;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class RAPersons extends RecyclerView.Adapter<RAPersons.Holder> implements View.OnClickListener, View.OnLongClickListener, Design.PopupAct {
    AppCompatActivity context;
    ArrayList<Person> people;
    DataBase db;
    public RAPersons(AppCompatActivity context) {
        this.context = context;
        people = new ArrayList<>();
        db = new DataBase(context);
    }
    public void update(ArrayList<Person> newPeople){
        people.clear();
        people.addAll(newPeople);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.person_list,parent,false);
        return new Holder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Person person = people.get(position);
        holder.tv_name.setText(person.firstName + " " + person.lastName);
        holder.tv_idCard.setText(person.idCard);
        holder.tv_phone.setText(person.telPhone);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
        holder.itemView.setOnLongClickListener(this);
    }

    @Override
    public int getItemCount() {
        return people.size();
    }

    @Override
    public void onClick(View view) {
        int pos = (int) view.getTag();
        new DialogPerson(context,db,people.get(pos)).show();
    }

    @Override
    public boolean onLongClick(View view) {
        Design.createMenu(context,view,this).show();
        return false;
    }

    @Override
    public void remove(int pos) {
        Person person = people.get(pos);
        db.deletePerson(person.idCard,this,pos);
    }

    public void delete(int index,boolean suc){
        Person person = people.get(index);
        if (suc){
            Toast.makeText(context,person.firstName + " "+person.lastName + "delete correctly",Toast.LENGTH_SHORT).show();
            people.remove(index);
            notifyDataSetChanged();
        }else {
            Toast.makeText(context,person.firstName + " "+person.lastName + "could'nt delete correctly",Toast.LENGTH_SHORT).show();
        }
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_idCard,tv_phone;
        public Holder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_nameP);
            tv_idCard = itemView.findViewById(R.id.tv_idCard);
            tv_phone = itemView.findViewById(R.id.tv_phone);
        }
    }
}
