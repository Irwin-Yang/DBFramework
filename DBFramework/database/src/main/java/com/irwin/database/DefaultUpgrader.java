package com.irwin.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Irwin on 2015/11/8.
 * Upgrader which  upgrade table and restore data by comparing table column name.
 */
public class DefaultUpgrader extends AbstractUpgrader {
    private List<AbstractDao> mNewTables = null;

    private List<AbstractDao> mChangedTables = null;

    @Override
    public void upgrade(SQLiteDatabase db, List<AbstractDao> tables, int oldVersion, int newVersion) {
        if (tables == null) {
            return;
        }
        Log.i("Info", "Upgrading...[" + oldVersion + "]--->[" + newVersion + "]");
        analyze(db, tables);
        createTables(db, mNewTables);
        upgradeTables(db, mChangedTables);
    }

    void analyze(SQLiteDatabase db, List<AbstractDao> tables) {
        int size = tables.size();
        ArrayList<AbstractDao> newList = null;
        ArrayList<AbstractDao> changedList = null;
        HashMap<String, Table> exsitTables = getExistTables(db);
        AbstractDao dao = null;
        for (int i = 0; i < size; i++) {
            dao = tables.get(i);
            //Table does not exist.
            if (!exsitTables.containsKey(dao.getTableName())) {
                if (newList == null) {
                    newList = new ArrayList<AbstractDao>();
                }
                newList.add(dao);
            }
            //Table has changed
            else if (hasChanged(dao, exsitTables.get(dao.getTableName()))) {
                if (changedList == null) {
                    changedList = new ArrayList<AbstractDao>();
                }
                changedList.add(dao);
            }
        }
        mNewTables = newList;
        mChangedTables = changedList;
    }

    boolean hasChanged(AbstractDao dao, Table table) {
        String[] newColumns = getColumnsFromSQL(dao.getCreateSql());
        String[] oldColumns = table.Columns;
        //TODO Fix it. We just handling change of columns count
        return (newColumns.length != oldColumns.length);
    }

    HashMap<String, Table> getExistTables(SQLiteDatabase db) {
        final String table = "sqlite_master";
        final String[] columns = {"name", "sql"};
        Cursor cursor = db.query(table, columns, "type='table'", null, null, null, null);
        HashMap<String, Table> ret = null;
        if (cursor.getCount() > 0) {
            ret = new HashMap<String, Table>(cursor.getCount());
            Table tmp = null;
            while (cursor.moveToNext()) {
                tmp = new Table(cursor.getString(0), getColumnsFromSQL(cursor.getString(1)));
                ret.put(tmp.Name, tmp);
            }
        }
        cursor.close();
        return ret;
    }

    String[] getColumnsFromSQL(String createSql) {
        createSql = createSql.substring(createSql.indexOf("("), createSql.length() - 1);
        String[] array = createSql.split(",");
        return array;
    }

    @Override
    public List<AbstractDao> getNewTables() {
        return mNewTables;
    }

    @Override
    public List<AbstractDao> getChangedTables() {
        return mChangedTables;
    }

    private class Table {
        public String Name;

        public String[] Columns;

        public Table() {
        }

        public Table(String name, String[] columns) {
            Name = name;
            Columns = columns;
        }
    }
}
