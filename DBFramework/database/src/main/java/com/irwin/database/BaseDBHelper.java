package com.irwin.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.irwin.database.upgrade.BaseUpgrader;
import com.irwin.database.upgrade.DefaultUpgrader;
import com.irwin.database.upgrade.StrictUpgrader;
import com.irwin.database.upgrade.Table;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Irwin on 2016/7/2.
 */
public abstract class BaseDBHelper extends SQLiteOpenHelper {

    public abstract List<AbstractDao> getTables();

    private BaseUpgrader mUpgrader;

    private Context mContext;

    private boolean mCacheDirSet = false;

    public BaseDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context.getApplicationContext();
        initializeTables();
    }

    public BaseDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        mContext = context.getApplicationContext();
        initializeTables();
    }


    public void initializeTables() {
        List<AbstractDao> list = getTables();
        if (list == null) {
            return;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            list.get(i).setDBHelper(this);
        }
    }

    public BaseUpgrader getUpgrader() {
        return mUpgrader;
    }

    public void setUpgrader(BaseUpgrader upgrader) {
        this.mUpgrader = upgrader;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        List<AbstractDao> list = getTables();
        if (list == null) {
            return;
        }
        int size = list.size();
        AbstractDao dao;
        for (int i = 0; i < size; i++) {
            dao = list.get(i);
            db.execSQL(dao.getCreateSql());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        BaseUpgrader upgrader = getUpgrader();
        if (upgrader == null) {
            upgrader = new DefaultUpgrader();
        }
        upgrader.setDatabase(db);
        List<AbstractDao> list = getTables();
        if (list == null) {
            return;
        }
        int size = list.size();
        AbstractDao dao;
        ArrayList<Table> tableList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            dao = list.get(i);
            tableList.add(new Table(dao.getTableName(), dao.getCreateSql()));
        }
        try {
            //Catch unexpected exceptions.
            upgrader.upgrade(oldVersion, newVersion, tableList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        checkCreateCacheDir(mContext);
        return super.getReadableDatabase();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        checkCreateCacheDir(mContext);
        return super.getWritableDatabase();
    }

    protected synchronized void checkCreateCacheDir(Context context) {
        if (!mCacheDirSet) {
            mCacheDirSet = true;
            File dir = new File(context.getFilesDir(), "/db/main");
            dir.mkdirs();
            super.getReadableDatabase().execSQL("PRAGMA temp_store_directory = '" + dir.getAbsolutePath() + "'");
        }
    }

    @Override
    public synchronized void close() {
        //Shall we close the daos?
        closeDaos();
        super.close();
    }

    void closeDaos() {
        List<AbstractDao> list = getTables();
        if (list == null) {
            return;
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            list.get(i).close();
        }
    }
}
