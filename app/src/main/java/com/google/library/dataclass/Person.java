package com.google.library.dataclass;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Person {

    public String firstName;
    public String lastName;
    public String celPhone;
    public String idCard;
    public String parentName;
    public String telPhone;
    public String parentPhone;
    public String address;
    public String schoolName;
    public boolean hasSharing;

    public Person(String firstName, String lastName, String celPhone, String idCard, String parentName,
                  String telPhone, String parentPhone, String address, String schoolName,boolean hasSharing) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.celPhone = celPhone;
        this.idCard = idCard;
        this.parentName = parentName;
        this.telPhone = telPhone;
        this.parentPhone = parentPhone;
        this.address = address;
        this.schoolName = schoolName;
        this.hasSharing = hasSharing;
    }

    public static JSONObject getJSONValue(Person person){
        JSONObject jo = new JSONObject();
        try {
            jo.put("idCard",person.idCard)
                    .put("firstName",person.firstName)
                    .put("lastName",person.lastName)
                    .put("celPhone",person.celPhone)
                    .put("parentName",person.parentName)
                    .put("parentPhone",person.parentPhone)
                    .put("telPhone",person.telPhone)
                    .put("address",person.address)
                    .put("schoolName",person.schoolName)
                    .put("has_sharing",person.hasSharing);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }
    public static ArrayList<Person> makePeople(JSONArray ja) {
        ArrayList<Person> people = new ArrayList<>();
        for (int i = 0; i < ja.length(); i++) {
            try {
                JSONObject jo = ja.getJSONObject(i);
                if (jo != null){
                    String firstName = jo.getString("first_name");
                    String lastName = jo.getString("last_name");
                    String celPhone = jo.getString("cellphone");
                    String idCard = jo.getString("id");
                    String parentName = jo.getString("parent_name");
                    String telPhone = jo.getString("telephone");
                    String parentPhone = jo.getString("parent_phone");
                    String address = jo.getString("address");
                    String schoolName = jo.getString("school");
                    boolean hasSharing = jo.getBoolean("has_sharing");
                    Person person = new Person(firstName,lastName,celPhone,idCard,parentName,telPhone,parentPhone,address,schoolName,hasSharing);
                    people.add(person);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return people;
    }
}
