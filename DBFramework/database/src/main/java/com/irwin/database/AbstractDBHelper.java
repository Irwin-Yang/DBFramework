package com.irwin.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by Irwin on 2015/11/8.
 * Use BaseDBHelper instead.
 */
@Deprecated
public abstract class AbstractDBHelper extends BaseDBHelper {

    public AbstractDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        initDaos();
    }

    public AbstractDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        initDaos();
    }

    void initDaos() {
        List<AbstractDao> list = getTables();
        if (list == null) {
            return;
        }
        int size = list.size();
        AbstractDao dao;
        for (int i = 0; i < size; i++) {
            dao = list.get(i);
            dao.setDBHelper(this);
        }
    }
}
