package com.irwin.database.upgrade;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ARES on 2017/7/13.
 * Database upgrader which will check the change of tables/columns and upgrade them if need.. Unlike {@link DefaultUpgrader}, this implementation will
 * treat tables/columns changes strictly, and it may take extra expenses.
 */

public class StrictUpgrader extends DefaultUpgrader {

    @Override
    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, List<Table> tableList) {
        Map<String, Table> oldMap = getOldTables();
        Iterator<Table> iterator = tableList.iterator();
        Table table;
        String createSql;
        Table oldTable;
        Table tempTable;
        String tempTableName;
        while (iterator.hasNext()) {
            table = iterator.next();
            if ((oldTable = oldMap.get(table.Name)) == null) {
                //New table, create directly.
                db.execSQL(table.CreateSql);
                Log.i(TAG, "Table " + table.Name + " is a new table. Create it directly");
                continue;
            }
            //Remove hit table.
            oldMap.remove(table.Name);
            //Create new table as temp table.
            tempTableName = table.Name + TEMP_SUFFIX;
            createSql = table.CreateSql.replaceFirst(table.Name, tempTableName);
            db.execSQL(createSql);
            tempTable = getTable(tempTableName);
            if (tempTable == null) {
                Log.w(TAG, "Create temp table fail: " + tempTable);
                deleteTable(tempTableName);
                deleteTable(table.Name);
                db.execSQL(table.CreateSql);
                continue;
            }
            if (oldTable.equalsColumns(tempTable)) {
                //Table not change. Delete temp table.
                Log.i(TAG, "Table " + table.Name + " doesn't need update");
                deleteTable(tempTableName);
                continue;
            }
            Log.i(TAG, "Update table: " + table.Name);
            //Table changed.
            copyData(getCommonColumn(oldTable.getColumns(), tempTable.getColumns()), oldTable.Name, tempTableName);
            //Delete old table
            deleteTable(oldTable.Name);
            //Alter new created temp table to its original name.
            alterTableName(tempTableName, table.Name);
        }
        //Delete obsolete tables not hit.
        deleteObsoleteTables(oldMap.values());
    }


}
