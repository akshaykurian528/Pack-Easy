package com.example.pack_easy.Data;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.example.pack_easy.Constants.MyConstants;
import com.example.pack_easy.Models.Items;

import com.example.pack_easy.Database.RoomDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AppData extends Application {

    RoomDB database;
    String category;
    Context context;

    public static final String LAST_VERSION = "LAST_VERSION";
    public static final int NEW_VERSION = 1;

    public AppData(RoomDB database) {
        this.database = database;
    }

    public AppData(RoomDB database, Context context) {
        this.database = database;
        this.context = context;
    }

    public List<Items> getBasicData(){
        category = "Basic Needs";
        List<Items> basicItem = new ArrayList<>();
        basicItem.add(new Items("Visa",category,false));
        basicItem.add(new Items("Passport",category,false));
        return basicItem;
    }

    public List<Items> getPersonalCareData(){
        String[] data = {"Tooth brush","Paste","Bottle"};
        return prepareItemsList(MyConstants.PERSONAL_CARE_CAMEL_CASE, data);
    }

    public List<Items> getClothingData(){
        String[] data = {"Tooth brush","Paste"};
        return prepareItemsList(MyConstants.CLOTHING_CAMEL_CASE, data);
    }

    public List<Items> getBabyNeedData(){
        String[] data = {"Tooth brush","Paste"};
        return prepareItemsList(MyConstants.BABY_NEEDS_CAMEL_CASE, data);
    }

    public List<Items> getHealthData(){
        String[] data = {"Tooth brush","Paste"};
        return prepareItemsList(MyConstants.HEALTH_CAMEL_CASE, data);
    }

    public List<Items> getTechnologyData(){
        String[] data = {"Tooth brush","Paste"};
        return prepareItemsList(MyConstants.TECHNOLOGY_CAMEL_CASE, data);
    }

    public List<Items> getFoodNeedData(){
        String[] data = {"Tooth brush","Paste"};
        return prepareItemsList(MyConstants.FOOD_CAMEL_CASE, data);
    }

    public List<Items> getBeachNeedData(){
        String[] data = {"Tooth brush","Paste"};
        return prepareItemsList(MyConstants.BEACH_SUPPLIES_CAMEL_CASE, data);
    }

    public List<Items> getCarNeedData(){
        String[] data = {"Tooth brush","Paste"};
        return prepareItemsList(MyConstants.CAR_SUPPLIES_CAMEL_CASE, data);
    }

    public List<Items> getNeedsData(){
        String[] data = {"Tooth brush","Paste"};
        return prepareItemsList(MyConstants.NEEDS_CAMEL_CASE, data);
    }

    public List<Items> prepareItemsList(String category, String[] data) {
        List<String> list = Arrays.asList(data);
        List<Items>dataList = new ArrayList<>();
        dataList.clear();

        for (int i = 0; i< list.size(); i++){
            dataList.add(new Items(list.get(i),category,false));
        }
        return dataList;
    }

    public List<List<Items>> getAllData(){
        List<List<Items>> listofAllItems = new ArrayList<>();
        listofAllItems.clear();
        listofAllItems.add(getBasicData());
        listofAllItems.add(getClothingData());
        listofAllItems.add(getPersonalCareData());
        listofAllItems.add(getBabyNeedData());
        listofAllItems.add(getHealthData());
        listofAllItems.add(getTechnologyData());
        listofAllItems.add(getFoodNeedData());
        listofAllItems.add(getBeachNeedData());
        listofAllItems.add(getCarNeedData());
        listofAllItems.add(getNeedsData());
        return listofAllItems;
    }


    public void persistAllData(){
        List<List<Items>> listofAllItems = getAllData();
        for(List<Items> list: listofAllItems){
            for(Items items:list){
                database.mainDao().saveItem(items);
            }
        }
        System.out.println("Data Added");
    }

    public void persistDataByCategory(String category,Boolean onlyDelete){
        try{
            List<Items> list = deleteAndGetListByCategory(category, onlyDelete);
            if (!onlyDelete) {
                for (Items item: list){
                    database.mainDao().saveItem(item);
                }
                Toast.makeText(context, category + "Reset Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, category + "Reset Successfully", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private List<Items> deleteAndGetListByCategory(String category,Boolean onlyDelete){
        if(onlyDelete){
            database.mainDao().deleteAllByCategory(category,MyConstants.SYSTEM_SMALL);
        } else {
            database.mainDao().deleteAllByCategory(category);
        }

        switch (category){
            case MyConstants.BASIC_NEEDS_CAMEL_CASE:
                return getBasicData();

            case MyConstants.CLOTHING_CAMEL_CASE:
                return getClothingData();

            case MyConstants.PERSONAL_CARE_CAMEL_CASE:
                return getPersonalCareData();

            case MyConstants.BABY_NEEDS_CAMEL_CASE:
                return getBabyNeedData();

            case MyConstants.HEALTH_CAMEL_CASE:
                return getHealthData();

            case MyConstants.TECHNOLOGY_CAMEL_CASE:
                return getTechnologyData();

            case MyConstants.FOOD_CAMEL_CASE:
                return getFoodNeedData();

            case MyConstants.BEACH_SUPPLIES_CAMEL_CASE:
                return getBeachNeedData();

            case MyConstants.CAR_SUPPLIES_CAMEL_CASE:
                return getCarNeedData();

            case MyConstants.NEEDS_CAMEL_CASE:
                return getNeedsData();

            default:
                return new ArrayList<>();


        }
    }

}
