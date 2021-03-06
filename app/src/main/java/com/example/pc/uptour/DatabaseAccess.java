package com.example.pc.uptour;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by PC on 1/17/2018.
 */

public class DatabaseAccess  {
    private MyDatabaseOpenHelper myDatabaseOpenHelper;
    private SQLiteDatabase mSqLiteDatabase;

    /**
     * constructor for creating
     *
     * @param context
     */
    public DatabaseAccess(Context context) {
        this.myDatabaseOpenHelper = new MyDatabaseOpenHelper(context);
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.mSqLiteDatabase = myDatabaseOpenHelper.getWritableDatabase();
    }

    public  void close(){
        if (mSqLiteDatabase!=null)
            mSqLiteDatabase.close();
    }

    public boolean insertInCities(String placeID,String cityName){
        ContentValues cv=new ContentValues();
        cv.put("city_place_id",placeID);
        long check=mSqLiteDatabase.update("cities",cv,"city_name=?",new String[]{cityName});
        return check>0;
    }

    //check name present in database
    public  String checkCityNameForInsert(String name){
        Cursor cursor=mSqLiteDatabase.rawQuery("SELECT * from cities WHERE city_name='"+name +"'",null);
        if( cursor != null && cursor.moveToFirst() ){
            if (cursor.getString(cursor.getColumnIndex("city_name")).equals(name)&&
                    cursor.getString(cursor.getColumnIndex("city_place_id"))==null) {
                return cursor.getString(0);
            }
            else if (cursor.getString(cursor.getColumnIndex("city_name")).equals(name)&&
                    cursor.getString(cursor.getColumnIndex("city_place_id"))!=null){
                return "false";
            }
        }
        return null;
    }
    //check name present in database
    public  String checkCityName(String name){
        Cursor cursor=mSqLiteDatabase.rawQuery("SELECT * from cities WHERE city_name='"+name +"'",null);
        if( cursor != null && cursor.moveToFirst() ){
            if (cursor.getString(cursor.getColumnIndex("city_name")).equals(name)&&
                    cursor.getString(cursor.getColumnIndex("city_place_id"))!=null) {
                return cursor.getString(0);
            }
        }
        return null;
    }

    //match the address with city
    public String checkSubString(String address){
        Cursor cursor=mSqLiteDatabase.rawQuery("select city_id,city_name from cities",null);
        while (cursor.moveToNext()){
            String city=cursor.getString(cursor.getColumnIndex("city_name"));
            if (address.contains(city)){
               return cursor.getString(cursor.getColumnIndex("city_id"));
            }
        }
        cursor.close();
        return null;
    }

    //inserting hotel
    public boolean insertHotel(String hotePlacelID,String hotelName,String cityID){
        ContentValues cv=new ContentValues();
        cv.put("hotel_name",hotelName);
        cv.put("city_id",cityID);
        cv.put("hotel_place_id",hotePlacelID);
        long check=mSqLiteDatabase.insert("hotels",null,cv);
        return check>0;
    }

    //inserting place
    public boolean insertPlace(String placePlaceID,String placeName,String cityID){
        ContentValues cv=new ContentValues();
        cv.put("hotel_name",placeName);
        cv.put("city_id",cityID);
        cv.put("hotel_place_id",placePlaceID);
        long check=mSqLiteDatabase.insert("places",null,cv);
        return check>0;
    }

    //inserting food
    public boolean insertFood(String foodPlaceID,String foodName,String cityID){
        ContentValues cv=new ContentValues();
        cv.put("food_name",foodName);
        cv.put("food_place_id",foodPlaceID);
        long check=mSqLiteDatabase.insert("food",null,cv);
        //'check' is actually the last_insert_rowID
        if (check>0)
            insertInJunction(check,cityID);
        return false;
    }

    //inserting food
    private boolean insertInJunction(long foodID,String cityID){
        ContentValues cv=new ContentValues();
        cv.put("food_id",foodID);
        cv.put("city_id",cityID);
        long check=mSqLiteDatabase.insert("jun_city_food",null,cv);
        return check>0;
    }


    protected List<HotelDetails> getHotels(String cityID){
        Cursor cursor=mSqLiteDatabase.rawQuery("SELECT * from hotels WHERE hotels.city_id='"+cityID +"'",null);
        List<HotelDetails> hotelList=new LinkedList<>();
        while (cursor.moveToNext()){
            String hotelPlaceID=cursor.getString(cursor.getColumnIndex("hotel_place_id"));
            if (hotelPlaceID!=null) {
                String hotelName=cursor.getString(cursor.getColumnIndex("hotel_name"));
                String hotelID=cursor.getString(cursor.getColumnIndex("hotel_id"));
                HotelDetails hotel = new HotelDetails(hotelName,hotelID,hotelPlaceID);
                hotelList.add(hotel);
            }
        }
        cursor.close();
        return hotelList;
    }

    protected List<CityDetails> getCities(){
        List<CityDetails> cityList=new ArrayList<>();
        Cursor cursor=mSqLiteDatabase.rawQuery("select * from cities",null);
        while (cursor.moveToNext()){
            int cityID=cursor.getInt(cursor.getColumnIndex("city_id"));
            String cityName=cursor.getString(cursor.getColumnIndex("city_name"));
            String cityPlaceID=cursor.getString(cursor.getColumnIndex("city_place_id"));
            CityDetails city=new CityDetails(Integer.toString(cityID),cityName,cityPlaceID);
            cityList.add(city);
        }
        cursor.close();
        return cityList;
    }
}
