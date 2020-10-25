package com.google.library.dataclass;



import com.google.library.ui.Design;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import saman.zamani.persiandate.PersianDate;

public class BorrowLand {

    public PersianDate timeBorrow,timeLand;
    public int idBook;
    public String idPerson;
    public boolean show = false;

    public BorrowLand(Long timeBorrow, int idBook,String idPerson) {
        this.timeBorrow = new PersianDate(timeBorrow);
        Design.changeDate(this.timeBorrow);
        this.idBook = idBook;
        this.idPerson = idPerson;
    }
    public void setTimeLand(Long timeLand){
        this.timeLand = new PersianDate(timeLand);
        Design.changeDate(this.timeLand);
    }
    public static JSONObject getValues(BorrowLand bl){
        JSONObject jo = new JSONObject();
        try {
            jo.put("borrow",bl.timeBorrow.getTime())
                    .put("person_id",bl.idPerson)
                    .put("book_id",bl.idBook);

            if (bl.timeLand != null)
                jo.put("land",bl.timeLand.getTime());
            else
                jo.put("land",-1L);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }
    public static ArrayList<BorrowLand> makeBorrows(JSONArray ja, boolean historyBook){
        ArrayList<BorrowLand> bls = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            try {
                JSONObject jo = ja.getJSONObject(i);
                if (jo != null){
                    Long timeBorrow = jo.getLong("borrow");
                    long timeLand = jo.getLong("land");
                    BorrowLand bl;
                    if (historyBook){
                        String idPerson = jo.getString("person_id");
                        bl = new BorrowLand(timeBorrow,-1,idPerson);
                    }else {
                        int idBook = jo.getInt("book_id");
                        bl = new BorrowLand(timeBorrow,idBook,"");
                    }
                    if (timeLand != -1){
                        bl.setTimeLand(timeLand);
                    }
                    bls.add(bl);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return bls;
    }
}
