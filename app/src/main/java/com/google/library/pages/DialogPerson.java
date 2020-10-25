package com.google.library.pages;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.library.DataBase;
import com.google.library.R;
import com.google.library.dataclass.Person;
import com.google.library.dataclass.Share;
import com.google.library.ui.Design;

import java.util.Calendar;
import java.util.Objects;

import androidx.annotation.NonNull;

public class DialogPerson extends Dialog implements TextWatcher, View.OnClickListener {
    boolean insert;
    Person person;
    DataBase db;
    Holder holder;

    public DialogPerson(@NonNull Context context,DataBase db) {
        super(context, R.style.DialogStyle);
        setContentView(R.layout.person_edit);
        this.db = db;
        insert = true;
        person = new Person("","","","",
                "","","","","",false);
        holder = new Holder();
        initHolder();
    }

    public DialogPerson(@NonNull Context context,DataBase db,Person person) {
        super(context, R.style.DialogStyle);
        setContentView(R.layout.person_edit);
        this.person = person;
        insert = false;
        this.db = db;
        holder = new Holder();
        initHolder();
    }

    @SuppressLint("SetTextI18n")
    private void initHolder() {

        holder.edit_celPhone.addTextChangedListener(this);
        holder.edit_idCard.addTextChangedListener(this);
        holder.edit_lastName.addTextChangedListener(this);
        holder.edit_firstName.addTextChangedListener(this);
        holder.edit_telPhone.addTextChangedListener(this);
        holder.edit_parentPhone.addTextChangedListener(this);
        holder.edit_parentName.addTextChangedListener(this);
        holder.edit_schoolName.addTextChangedListener(this);
        holder.edit_address.addTextChangedListener(this);

        holder.btn_his.setOnClickListener(this);
        holder.btn_save.setOnClickListener(this);
        holder.btn_his_s.setOnClickListener(this);
        holder.btn_renewal.setOnClickListener(this);

        if (insert){

            holder.btn_his.setVisibility(View.GONE);
            holder.btn_his_s.setVisibility(View.GONE);
            holder.spinner.setEnabled(false);
            holder.btn_renewal.setEnabled(false);
            holder.btn_save.setEnabled(false);

        }else {

            holder.btn_his.setVisibility(View.VISIBLE);
            holder.btn_his_s.setVisibility(View.VISIBLE);
            holder.spinner.setEnabled(!person.hasSharing);
            holder.btn_renewal.setEnabled(!person.hasSharing);
            holder.btn_save.setEnabled(true);
            holder.tl.setEnabled(false);
            holder.edit_idCard.setEnabled(false);

            holder.edit_firstName.setText(person.firstName);
            holder.edit_lastName.setText(person.lastName);
            holder.edit_idCard.setText(person.idCard);
            holder.edit_celPhone.setText(person.celPhone);
            holder.edit_telPhone.setText(person.telPhone);
            holder.edit_parentName.setText(person.parentName);
            holder.edit_parentPhone.setText(person.parentPhone);
            holder.edit_schoolName.setText(person.schoolName);
            holder.edit_address.setText(person.address);

        }
    }

    @Override
    public void show() {
        super.show();
        if (insert){
            Objects.requireNonNull(holder.edit_firstName.getText()).clear();
            Objects.requireNonNull(holder.edit_lastName.getText()).clear();
            Objects.requireNonNull(holder.edit_idCard.getText()).clear();
            Objects.requireNonNull(holder.edit_celPhone.getText()).clear();
            Objects.requireNonNull(holder.edit_telPhone.getText()).clear();
            Objects.requireNonNull(holder.edit_parentName.getText()).clear();
            Objects.requireNonNull(holder.edit_parentPhone.getText()).clear();
            Objects.requireNonNull(holder.edit_schoolName.getText()).clear();
            Objects.requireNonNull(holder.edit_address.getText()).clear();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        holder.btn_save.setEnabled(checkPersonInfo());
    }

    private boolean checkPersonInfo() {
        return !Objects.requireNonNull(holder.edit_firstName.getText()).toString().trim().isEmpty() &&
                !Objects.requireNonNull(holder.edit_lastName.getText()).toString().trim().isEmpty() &&
                Objects.requireNonNull(holder.edit_idCard.getText()).toString().trim().length() == 10 &&
                Objects.requireNonNull(holder.edit_celPhone.getText()).toString().trim().length() == 11 &&
                !Objects.requireNonNull(holder.edit_parentName.getText()).toString().trim().isEmpty() &&
                !Objects.requireNonNull(holder.edit_schoolName.getText()).toString().trim().isEmpty() &&
                Objects.requireNonNull(holder.edit_telPhone.getText()).toString().trim().length() == 11 &&
                Objects.requireNonNull(holder.edit_parentPhone.getText()).toString().trim().length() == 11 &&
                !Objects.requireNonNull(holder.edit_address.getText()).toString().trim().isEmpty();
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_save:
                btnSave();
                break;
            case R.id.btn_history:
                btnHistory();
                break;
            case R.id.btn_historyS:
                btnHistoryS();
                break;
            case R.id.btn_renewal:
                btnRenewal();
                break;
        }
    }

    private void btnRenewal() {

        holder.spinner.setEnabled(false);
        holder.btn_renewal.setEnabled(false);
        int type = 1;
        switch (holder.spinner.getSelectedItemPosition()){
            case 0:
                type = 1;
                break;
            case 1:
                type = 2;
                break;
            case 2:
                type = 3;
                break;
            case 3:
                type = 6;
                break;
            case 4:
                type = 9;
                break;
            case 5:
                type = 12;
                break;
        }
        Share s = new Share(type, Calendar.getInstance().getTimeInMillis(),person.idCard);
        db.updateShare(s,holder.btn_renewal,holder.spinner);
    }

    private void btnHistoryS() {

        Intent intent = new Intent(getContext(),HistoryActivity.class);
        intent.putExtra("id",person.idCard);
        intent.putExtra("shareHistory",true);
        getContext().startActivity(intent);

    }

    private void btnSave(){

        person.firstName = Objects.requireNonNull(holder.edit_firstName.getText()).toString().trim();
        person.lastName = Objects.requireNonNull(holder.edit_lastName.getText()).toString().trim();
        person.idCard = Objects.requireNonNull(holder.edit_idCard.getText()).toString().trim();
        person.celPhone = Objects.requireNonNull(holder.edit_celPhone.getText()).toString().trim();
        person.telPhone = Objects.requireNonNull(holder.edit_telPhone.getText()).toString().trim();
        person.parentName = Objects.requireNonNull(holder.edit_parentName.getText()).toString().trim();
        person.parentPhone = Objects.requireNonNull(holder.edit_parentPhone.getText()).toString().trim();
        person.schoolName = Objects.requireNonNull(holder.edit_schoolName.getText()).toString().trim();
        person.address = Objects.requireNonNull(holder.edit_address.getText()).toString().trim();

        if (insert){
            holder.btn_save.setEnabled(false);
            db.insetPerson(person,holder.btn_save,this);
        }else {
            holder.btn_save.setEnabled(false);
            db.updatePerson(person,holder.btn_save,this);
        }
    }

    private void btnHistory(){

        Intent intent = new Intent(getContext(),HistoryActivity.class);
        intent.putExtra("id",person.idCard);
        intent.putExtra("bookHistory",false);
        getContext().startActivity(intent);

    }

    public class Holder{
        TextInputLayout tl;
        TextInputEditText edit_firstName,edit_lastName,edit_idCard,edit_celPhone;
        TextInputEditText edit_parentName,edit_parentPhone,edit_telPhone,edit_address,edit_schoolName;
        Spinner spinner;
        MaterialButton btn_save,btn_his,btn_renewal,btn_his_s;

        Holder(){
            tl = findViewById(R.id.tl);
            edit_firstName = findViewById(R.id.edit_firstName);
            edit_lastName = findViewById(R.id.edit_lastName);
            edit_idCard = findViewById(R.id.edit_idCard);
            edit_celPhone = findViewById(R.id.edit_celPhone);
            edit_parentName = findViewById(R.id.edit_parentName);
            edit_parentPhone = findViewById(R.id.edit_parentPhone);
            edit_telPhone = findViewById(R.id.edit_telPhone);
            edit_address = findViewById(R.id.edit_address);
            edit_schoolName = findViewById(R.id.edit_schoolName);
            spinner = findViewById(R.id.spinner_share);
            btn_renewal = findViewById(R.id.btn_renewal);
            btn_his_s = findViewById(R.id.btn_historyS);
            btn_save = findViewById(R.id.btn_save);
            btn_his = findViewById(R.id.btn_history);
            SpinnerAdapter adapter = ArrayAdapter.createFromResource(getContext(),R.array.spinner_share,
                    android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

    }
}
