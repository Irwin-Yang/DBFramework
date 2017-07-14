package com.irwin.database.upgrade;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ARES on 2017/7/14.
 */

public abstract class BaseUpgrader {
    protected SQLiteDatabase mDB;
    protected final String TAG;

    public BaseUpgrader() {
        TAG = getClass().getSimpleName();
    }

    public BaseUpgrader setDatabase(SQLiteDatabase db) {
        mDB = db;
        return this;
    }

    public void upgrade(int oldVersion, int newVersion, List<Table> tables) {
        Log.i(TAG, "DB upgrade: " + oldVersion + " ---> " + newVersion);
        onUpgrade(mDB, oldVersion, newVersion, tables);
    }

    protected abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, List<Table> tables);

    /**
     * @param columns
     * @param srcTable
     * @param destTable
     */
    public void copyData(List<Table.Column> columns, String srcTable, String destTable) {
        //Example: <code>INSERT INTO UserTmp(_id,_name,_age,_role) SELECT _id,_name,_age,'Programmer' FROM User</code>
        String columnStatement = convertColumnStatement(columns);
        if (TextUtils.isEmpty(columnStatement)) {
            return;
        }
        String sql = "INSERT INTO " + destTable + columnStatement + " SELECT " + columnStatement.substring(1, columnStatement.length() - 1) + " FROM " + srcTable;
        mDB.execSQL(sql);
    }

    public static String convertColumnStatement(List<Table.Column> columns) {
        if (columns == null || columns.size() == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (Table.Column item : columns) {
            builder.append(item.Name).append(",");
        }
        builder.deleteCharAt(builder.length() - 1).append(")");
        return builder.toString();
    }

    public List<Table.Column> getCommonColumn(List<Table.Column> listL, List<Table.Column> listR) {
        listL = new ArrayList<>(listL);
        listR = new ArrayList<>(listR);
        Iterator<Table.Column> iteratorL = listL.iterator();
        Table.Column column;
        Iterator<Table.Column> iteratorR;
        boolean matched;
        while (iteratorL.hasNext()) {
            column = iteratorL.next();
            iteratorR = listR.iterator();
            matched = false;
            while (iteratorR.hasNext()) {
                //We only consider name of column.
                if (column.equalsName(iteratorR.next())) {
                    iteratorR.remove();
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                iteratorL.remove();
            }
        }
        return listL;
    }

    public void alterTableName(String tableName, String newName) {
        mDB.execSQL("ALTER TABLE " + tableName + " RENAME TO " + newName);
    }

    public void deleteTable(String tableName) {
        mDB.execSQL("DROP TABLE " + tableName);
    }

    public Table getTable(String tableName) {
        final String table = "sqlite_master";
        final String[] columns = {"sql"};
        Cursor cursor = mDB.query(table, columns, "type='table' AND name='" + tableName + "'", null, null, null, null);
        Table ret = null;
        if (cursor.moveToNext()) {
            ret = new Table(tableName, cursor.getString(0));
        }
        cursor.close();
        return ret;
    }

    public Map<String, Table> getOldTables() {
        final String table = "sqlite_master";
        final String[] columns = {"name", "sql"};
        Cursor cursor = mDB.query(table, columns, "type='table'", null, null, null, null);
        HashMap<String, Table> ret = null;
        if (cursor.getCount() > 0) {
            ret = new HashMap<String, Table>(cursor.getCount());
            Table tmp;
            while (cursor.moveToNext()) {
                tmp = new Table(cursor.getString(0), cursor.getString(1));
                ret.put(tmp.Name, tmp);
            }
        }
        cursor.close();
        return ret == null ? Collections.EMPTY_MAP : ret;
    }

    public void deleteObsoleteTables(Collection<Table> tables) {
        //May delete system table such as android_metadata, sqlite_sequence etc
        for (Table item : tables) {
            if (item.Name.toUpperCase().startsWith("ANDROID") || item.Name.toUpperCase().startsWith("SQLITE")) {
                //Avoid to delete system tables.
                continue;
            }
            deleteTable(item.Name);
            Log.i(TAG, "Delete obsolete table: " + item.Name);
        }
    }
}
