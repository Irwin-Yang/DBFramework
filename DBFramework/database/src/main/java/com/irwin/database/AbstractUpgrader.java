package com.irwin.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.List;

/**
 * Created by Irwin on 2015/11/7.
 */
public abstract class AbstractUpgrader {

    public abstract List<AbstractDao> getNewTables();    //新表

    public abstract List<AbstractDao> getChangedTables();  //表改变

    public void upgrade(SQLiteDatabase db,List<AbstractDao> tables,int oldVersion,int newVersion) {
        createTables(db, getNewTables());
        upgradeTables(db, getChangedTables());
        Log.i("Info", "Upgrading...["+oldVersion+"]--->["+newVersion+"]");
    }

    protected void createTables(SQLiteDatabase db, List<AbstractDao> newTables) {
        if (newTables != null) {
            int size=newTables.size();
            AbstractDao dao=null;
            for (int i=0;i<size;i++) {
                dao=newTables.get(i);
                Log.i("Info", "Creating table: " + dao.getTableName());
                db.execSQL(dao.getCreateSql());
            }
        }
    }

    protected void upgradeTables(SQLiteDatabase db, List<AbstractDao> changedTables) {
        if (changedTables != null) {
            int size=changedTables.size();
            AbstractDao dao=null;
            for (int i=0;i<size;i++) {
                dao=changedTables.get(i);
                Log.i("Info","updating table: "+dao.getTableName());
                updateTable(db, dao);
            }
        }
    }

    /**
     * Example: <code>INSERT INTO UserTmp(_id,_name,_age,_role) SELECT _id,_name,_age,'Programmer' FROM User</code>
     *
     * @param db
     * @param dao    数据库升级 先存到临时表当中 然后在增加数据库字段  插入对应的数据 ，最后删除临时表
     */
    void updateTable(SQLiteDatabase db, AbstractDao dao) {
        String table = dao.getTableName();
        String tmp = table + "_tmp";
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " LIMIT 0,1", null);
        String[] columns = cursor.getColumnNames();
        db.execSQL("ALTER TABLE " + table + " RENAME TO " + tmp);
        db.execSQL(dao.getCreateSql());
        //TODO Maybe we can add supporting for excluding columns
        db.execSQL("INSERT INTO " + table + "(" + split(columns, ",") + ") SELECT * FROM " + tmp);
        db.execSQL("DROP TABLE " + tmp);
    }


    String split(String[] array, String separator) {
        StringBuilder builder = new StringBuilder();
        for (String str : array) {
            builder.append(str).append(separator);
        }
        return builder.substring(0, builder.length() - 1);
    }
}
