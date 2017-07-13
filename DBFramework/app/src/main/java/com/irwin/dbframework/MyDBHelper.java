package com.irwin.dbframework;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import com.irwin.database.AbstractDao;
import com.irwin.database.BaseDBHelper;
import com.irwin.dbframework.daos.EmployeeDao;
import com.irwin.dbframework.daos.UserDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARES on 2017/7/13.
 */

public class MyDBHelper extends BaseDBHelper {

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public List<AbstractDao> getTables() {
        ArrayList<AbstractDao> list = new ArrayList<>();
        list.add(UserDao.getInstance());
        list.add(EmployeeDao.getInstance());
        return list;
    }
}
