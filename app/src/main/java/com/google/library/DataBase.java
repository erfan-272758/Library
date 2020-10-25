package com.google.library;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.library.adapters.RABooks;
import com.google.library.adapters.RABorrows;
import com.google.library.adapters.RAHistory;
import com.google.library.adapters.RAPersons;
import com.google.library.adapters.RAShare;
import com.google.library.dataclass.Book;
import com.google.library.dataclass.BorrowLand;
import com.google.library.dataclass.Person;
import com.google.library.dataclass.Share;
import com.google.library.pages.BookEditActivity;
import com.google.library.pages.DialogPerson;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class DataBase {
    private static final String LOG_TAG = "LibraryDbHelper";
    public static int LIMIT = 15;
    AppCompatActivity activityCompat;

    public DataBase(AppCompatActivity activityCompat) {
        this.activityCompat = activityCompat;
    }

    // book.barcode = null get id and then use method save
    public void insertBook(Book book, ProgressBar pb, Button btn,BookEditActivity.Holder holder){
        SQLiteDatabase dp = getWritableDatabase();
        long insertId = dp.insert(TB_BOOK,null,Book.getContentValue(book));
        if(insertId == -1) {
            Log.i(LOG_TAG, "data insertion failed. (item : " + book.name + ")");
        } else {
            Log.i(LOG_TAG, "data inserted with id : " + insertId);
        }
        if (dp.isOpen()) dp.close();
    }
    public void insetPerson(Person person, Button btn, DialogPerson dp){
        Toast.makeText(getContext(),person.firstName + " " + person.lastName + " correctly add",Toast.LENGTH_SHORT).show();
        SQLiteDatabase db = getWritableDatabase();
        long insertId = db.insert(TB_PERSON,null,Person.getContentValues(person));
        if(insertId == -1) {
            Log.i(LOG_TAG, "data insertion failed. (item : " + person.firstName + ")");
        } else {
            Log.i(LOG_TAG, "data inserted with id : " + insertId);
        }
        if (db.isOpen())db.close();
    }

    public void insetBorrow(BorrowLand bl, Button btn, RABorrows adapter){
        SQLiteDatabase db = getWritableDatabase();
        long insertId = db.insert(TB_BORROW_LAND,null,BorrowLand.getValues(bl));
        if(insertId == -1) {
            Log.i(LOG_TAG, "data insertion failed.");
        } else {
            Log.i(LOG_TAG, "data inserted with id : " + insertId);
        }
        if (db.isOpen())db.close();
    }
    public void insertShare(Share share, Button btn, Spinner spinner){

    }

    public void updateBookData(Book book,Button btn,ProgressBar pb){}
    public void updateBookAll(Book book, ImageView iv, Button btn, ProgressBar pb){
        SQLiteDatabase db = getWritableDatabase();
        int c = db.update(TB_BOOK,Book.getContentValue(book),"id = "+book.id,null);
        Log.i(LOG_TAG,"table "+TB_BOOK + " item "+c+" was update.");
        if (db.isOpen()) db.close();
    }
    public void updatePerson(Person person,Button btn,DialogPerson dp){
        Toast.makeText(getContext(),person.firstName + " " + person.lastName + " correctly update",Toast.LENGTH_SHORT).show();
        SQLiteDatabase db = getWritableDatabase();
        int c = db.update(TB_PERSON,Person.getContentValues(person),"id = "+person.idCard,null);
        Log.i(LOG_TAG,"table "+TB_PERSON + " item "+c+" was update.");
        if (db.isOpen()) db.close();
    }
    //after this call update on adapter
    public void updateBorrow(BorrowLand bl, Button btn, RABorrows adapter){
        SQLiteDatabase db = getWritableDatabase();
        int c = db.update(TB_BORROW_LAND,BorrowLand.getValues(bl),"idPerson = "+bl.idPerson+" AND idBook = "+bl.idBook,null);
        Log.i(LOG_TAG,"table "+TB_BORROW_LAND + " item "+c+" was update.");
        if (db.isOpen()) db.close();
    }
    public void updateShare(Share share, Button btn, Spinner spinner){}

    public int deleteBook(int id, RABooks adapter,int position){
        SQLiteDatabase db = getWritableDatabase();
        int c = db.delete(TB_BOOK,"id = "+id,null);
        db.delete(TB_BORROW_LAND,"idBook = '"+id+"'",null);
        Log.i(LOG_TAG,"table "+TB_BOOK+" item "+c+" was delete.");
        if (db.isOpen()) db.close();
        return c;
    }
    public void deletePerson(String id, RAPersons adapter,int index){
        SQLiteDatabase db = getWritableDatabase();
        int c = db.delete(TB_PERSON,"id = "+id,null);
        db.delete(TB_BORROW_LAND,"idPerson = '"+id+"'",null);
        Log.i(LOG_TAG,"table "+TB_PERSON+" item "+c+" was delete.");
        if (db.isOpen()) db.close();
    }

    public void getAllBooks(ProgressBar pb,RABooks adapter, int limit){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        if (selection != null)
            c = db.query(TB_BOOK,allColumnsBook,selection,args,null,null,"timeCreate DESC",limit);
        else{
//            c = db.query(TB_BOOK,allColumnsBook,null,args,null,null,"timeCreate DESC","20");
            c = db.rawQuery("SELECT * FROM '"+TB_BOOK+"'",null);
        }
        ArrayList<Book> b = Book.makeBooks(c);
        if (db.isOpen()) db.close();
        c.close();
        Log.i("find_book","number of books "+b.size());
        return b;
    }
    public void getQueryBooks(String text,RABooks adapter,int limit){}
    public void getQueryBooks(String text,RABorrows adapter,int limit){}
    public void getBook(int id, BookEditActivity.Holder holder){}
    public void setBookImage(int id,ImageView iv){}
    public void getAllPersons(ProgressBar pb,RAPersons adapter, int limit){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        if (selection != null)
            c = db.query(TB_BOOK,allColumnsBook,selection,args,null,null,"timeCreate DESC",limit);
        else{
//            c = db.query(TB_BOOK,allColumnsBook,null,args,null,null,"timeCreate DESC","20");
            c = db.rawQuery("SELECT * FROM '"+TB_BOOK+"'",null);
        }
        ArrayList<Book> b = Book.makeBooks(c);
        if (db.isOpen()) db.close();
        c.close();
        Log.i("find_book","number of books "+b.size());
        return b;
    }
    public void getQueryPersons(String text,RAPersons adapter,int limit){}
    //person.firstName + " "+ person.lastName + " "+person.idCard
    public void getPerson(String id, TextView tv){}
    public void getPerson(String id,DialogPerson.Holder holder){}
//    public ArrayList<Person> getPersons(String selection, String[] args, String limit){
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor c;
//        if (selection != null)
//            c = db.query(TB_PERSON,allColumnsPerson,selection,args,null,null,"firstName ASC",limit);
//        else
//            c = db.query(TB_PERSON,allColumnsPerson,null,args,null,null,"firstName ASC","20");
//        ArrayList<Person> p = Person.makeBooks(c);
//        if (db.isOpen()) db.close();
//        c.close();
//        return p;
//    }
    public void getBorrow(ProgressBar pb, RAHistory adapter,String id,boolean historyBook){
        SQLiteDatabase db = getReadableDatabase();
        Cursor c;
        c = db.query(TB_BORROW_LAND,allColumnsHistory,selection,null,null,null,"borrowTime DESC");
        ArrayList<BorrowLand> bls = BorrowLand.makeBorrows(c,historyBook);
        Log.i("check_borrow",""+bls.size());
        if (db.isOpen()) db.close();
        c.close();
        return bls;
    }
    public void getShares(ProgressBar pb, RAHistory adapter, String id){}
}
