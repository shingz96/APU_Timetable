package com.shing.aputimetable.model;

import com.shing.aputimetable.entity.ApuClass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

}
