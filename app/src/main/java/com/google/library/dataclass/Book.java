package com.google.library.dataclass;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class Book {
    public static final int KH_SABZ = 1;
    public static final int GAJ = 2;
    public static final int MEHR_O_MOH = 3;
    public static final int MOB_TAK = 4;
    public static final int NASHR_OLGO = 5;
    public static final int NASHR_DARYAFT = 6;
    public static final int KANOON = 7;
    public static final int OTHER = 8;

    public static final int RIAZI = 1;
    public static final int TAGROBI = 2;
    public static final int ENSANI = 3;
    public static final int RIAZI_TAGROBI = 4;
    public static final int OMOUMI = 5;
    public static final int MINI_T = 6;
    public static final int MINI_O = 7;

    public String name;
    public int type;
    public int major;
    public String year;
    public String imageUri;
    public int id;
    public String barcode;
    public boolean exist;
    public BorrowLand borrowLand = null;

    public Book(String name, int type, int major, String year, String imageUri, int id) {
        this.name = name;
        this.type = type;
        this.id = id;
        exist = true;
        this.imageUri = imageUri;
        this.year = year;
        this.major = major;
    }
    public static JSONObject getJSONValue(Book book){
        JSONObject jo = new JSONObject();
        try {
            jo.put("id",book.id)
                    .put("barcode",book.barcode)
                    .put("name",book.name)
                    .put("type",book.type)
                    .put("major",book.major)
                    .put("year",book.year)
                    .put("image",book.imageUri)
                    .put("existence",book.exist)
                    .put("borrow_time",-1L)
                    .put("land_time",-1L);
            if (book.borrowLand != null){
                jo.put("borrow_time",book.borrowLand.timeBorrow.getTime())
                        .put("person_id",book.borrowLand.idPerson);
                if (book.borrowLand.timeLand != null)
                    jo.put("land_time",book.borrowLand.timeLand.getTime());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }
    public static ArrayList<Book> makeBooks(JSONArray ja) {
        ArrayList<Book> books = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            try {
                JSONObject jo = ja.getJSONObject(i);
                if (jo != null){
                    int id = jo.getInt("id");
                    String barcode = jo.getString("barcode");
                    String name = jo.getString("name");
                    String imagePatch = jo.getString("image");
                    int type = jo.getInt("type");
                    boolean exist = jo.getBoolean("existence");
                    long borrowTime = jo.getLong("borrow_time");
                    long landTime = jo.getLong("land_time");
                    String idPerson = jo.getString("person_id");
                    String year = jo.getString("year");
                    int major = jo.getInt("major");
                    Book book = new Book(name,type,major,year,imagePatch,id);
                    book.barcode = barcode;
                    book.exist = exist;
                    if (borrowTime != -1L){
                        BorrowLand bl = new BorrowLand(borrowTime,id,idPerson);
                        if (landTime != -1L) {
                            bl.setTimeLand(landTime);
                        }
                        book.borrowLand = bl;
                    }
                    books.add(book);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return books;
    }
}
