package com.google.library.dataclass;

import com.google.library.ui.Design;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import saman.zamani.persiandate.PersianDate;

public class Share {
    /**
     *
    public static int ONE = 1;
    public static int TWO = 2;
    public static int THREE = 3;
    public static int FOUR = 4;
    public static int FIVE = 5;
    public static int SIX = 6;
    public static int SEVEN = 7;
    public static int EIGHT = 8;
    public static int NINE = 9;
    public static int TEN = 10;
    public static int ELEVEN = 11;
    public static int TWELVE = 12;
     */

    public int type;
    public PersianDate timeStart;
    public PersianDate timeEnd;
    public String idPerson;
    public boolean show = false;

    public Share(int type, long timeStart, String idPerson) {
        this.type = type;
        this.timeStart = new PersianDate(timeStart);
        Design.changeDate(this.timeStart);
        this.idPerson = idPerson;
        this.timeEnd = this.timeStart.addMonth(type);
    }
    public static JSONObject getValue(Share s){
        JSONObject jo = new JSONObject();
        try {
            jo.put("type",s.type)
                    .put("person_id",s.idPerson)
                    .put("start",s.timeStart)
                    .put("end",s.timeEnd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }
    public static ArrayList<Share> createShares(JSONArray ja){
        ArrayList<Share> shares = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            try {
            JSONObject jo = ja.getJSONObject(i);
                if (jo != null){
                    int type = jo.getInt("type");
                    String idPerson = jo.getString("person_id");
                    long timeStart = jo.getLong("start");
                    shares.add(new Share(type,timeStart,idPerson));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return shares;
    }
}