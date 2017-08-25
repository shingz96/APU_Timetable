package com.shing.aputimetable.model;

import android.content.SharedPreferences;

import com.shing.aputimetable.entity.ApuClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Shing on 31-Jul-17.
 */

public class Database implements Serializable {

    private static List<List<ApuClass>> db = new ArrayList();
    private static List<ApuClass> mon = new ArrayList<>();
    private static List<ApuClass> tue = new ArrayList<>();
    private static List<ApuClass> wed = new ArrayList<>();
    private static List<ApuClass> thu = new ArrayList<>();
    private static List<ApuClass> fri = new ArrayList<>();
    private static Database database;

    private Database() {
    }

    public static Database getDatabaseInstance() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }

    public static void clearAll() {
        database = null;
        db.clear();
        mon.clear();
        tue.clear();
        wed.clear();
        thu.clear();
        fri.clear();
    }

    public void initData(List<ApuClass> data) {
        if (data != null) {
            for (ApuClass apuClass : data) {
                switch (apuClass.getDay()) {
                    case "Mon":
                        mon.add(apuClass);
                        break;
                    case "Tue":
                        tue.add(apuClass);
                        break;
                    case "Wed":
                        wed.add(apuClass);
                        break;
                    case "Thu":
                        thu.add(apuClass);
                        break;
                    case "Fri":
                        fri.add(apuClass);
                        break;
                }
            }
        }
        db.add(mon);
        db.add(tue);
        db.add(wed);
        db.add(thu);
        db.add(fri);
    }

    public List<ApuClass> getDataByDay(int i) {
        return db.get(i);
    }

    public List<ApuClass> filter(List<ApuClass> classDataset, SharedPreferences prefs) {
        List<ApuClass> filterDataset = new ArrayList<>();
        Set<String> filterSet = prefs.getStringSet("filter_list", null);
        if (filterSet != null) {
            List<String> filterList = new ArrayList<>(filterSet);
            for (int i = 0; i < filterList.size(); i++) {
                int type = Integer.parseInt(filterList.get(i));
                for (int j = 0; j < classDataset.size(); j++) {
                    ApuClass test = classDataset.get(j);
                    if (test.getType() == type) {
                        filterDataset.add(test);
                    }
                }
            }
            sortClassByTime(filterDataset);
        } else {
            filterDataset = classDataset;
        }
        return filterDataset;
    }

    private List<ApuClass> sortClassByTime(List<ApuClass> classes) {
        int n = classes.size();
        ApuClass temp;
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                float time1 = Float.parseFloat(classes.get(j - 1).getTime().split("-")[0].trim().replace(":", "."));
                float time2 = Float.parseFloat(classes.get(j).getTime().split("-")[0].trim().replace(":", "."));
                if (time1 > time2) {
                    //swap elements
                    temp = classes.get(j - 1);
                    classes.set(j - 1, classes.get(j));
                    classes.set(j, temp);
                }

            }
        }
        return classes;
    }


}
