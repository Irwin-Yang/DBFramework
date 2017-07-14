package com.irwin.database.upgrade;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ARES on 2017/7/14.
 * Database upgrader which will check the change of tables/columns and upgrade them if need.
 */
public class DefaultUpgrader extends BaseUpgrader {

    protected static final String TEMP_SUFFIX = "_tmp";

    @Override
    protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, List<Table> tableList) {
        Map<String, Table> oldMap = getOldTables();
        Iterator<Table> iterator = tableList.iterator();
        Table table;
        Table oldTable;
        String tempTable;
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
            if (oldTable.equalsColumns(table)) {
                //Table not change.
                Log.i(TAG, "Table " + table.Name + " doesn't need update");
                continue;
            }
            tempTable = table.Name + TEMP_SUFFIX;
            Log.i(TAG, "Update table: " + table.Name);

            //Table changed.
            //Alter old table as temp.
            alterTableName(oldTable.Name, tempTable);
            //Create new table.
            db.execSQL(table.CreateSql);
            //Copy data.
            copyData(getCommonColumn(oldTable.getColumns(), table.getColumns()), tempTable, table.Name);
            //Delete old table
            deleteTable(tempTable);
        }
        //Delete obsolete tables not hit.
        deleteObsoleteTables(oldMap.values());
    }


}
