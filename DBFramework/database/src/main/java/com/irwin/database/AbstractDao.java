package com.irwin.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.CancellationSignal;

import java.util.ArrayList;
import java.util.List;

import static com.irwin.database.Property.BRACKET_END;
import static com.irwin.database.Property.BRACKET_START;
import static com.irwin.database.Property.COMMA;
import static com.irwin.database.Property.IN;

/**
 * Created by Irwin on 2015/11/5.
 *
 * @param <ENTITY> Entity type
 * @param <ID>     ID type
 */
public abstract class AbstractDao<ENTITY, ID> implements BaseColumns {

    public abstract List<Property> getPropertyList();

    public abstract String getTableName();

    /**
     * Convert form cursor to Data bean. To improve performance,Use Column index to obtain value. And this method need not to
     * handle cursor iterator.
     *
     * @param cursor
     * @return Data bean.
     */
    public abstract ENTITY convert(Cursor cursor);

    /**
     * Convert data bean to content values.
     *
     * @param entity
     * @return
     */
    public abstract ContentValues convertValues(ENTITY entity);

    private SQLiteOpenHelper mDBHelper = null;

    public SQLiteOpenHelper getDBHelper() {
        return mDBHelper;
    }

    public AbstractDao setDBHelper(SQLiteOpenHelper dbHelper) {
        this.mDBHelper = dbHelper;
        return this;
    }

    public String getCreateSql() {
        List<Property> list = getPropertyList();
        return Property.createSQL(list, getTableName());
    }

    /**
     * Tell if has entity. this method only query ID column.
     *
     * @param where     Where clause.
     * @param whereArgs
     * @return
     */
    public boolean hasEntity(String where, String[] whereArgs) {
        boolean ret = false;
        Cursor cursor = queryCursor(new String[]{ID}, where, whereArgs);
        if (cursor != null) {
            ret = cursor.getCount() >= 1;
            cursor.close();
        }
        return ret;
    }

    public ENTITY queryByID(ID id) {
        SQLiteDatabase db = getReadDB();
        if (db == null) {
            return null;
        }
        Cursor cursor = db.rawQuery("SELECT * FROM " + getTableName() + " WHERE " + ID + "='" + id + "'", null);
        ENTITY ret = null;
        if (cursor.moveToFirst()) {
            ret = convert(cursor);
        }
        cursor.close();
        return ret;
    }

    public List<ENTITY> queryByIds(ID... ids) {
        if (ids == null || ids.length == 0) {
            return null;
        }
        String[] array = new String[ids.length];
        int index = 0;
        for (ID value : ids) {
            array[index++] = String.valueOf(value);
        }
        StringBuilder builder = new StringBuilder();
        makeInQuery(builder, ID, array);
        return query(builder.toString(), null);
    }

    public List<ENTITY> queryByOtherIds(List<ID> ids,String otherIdName) {
        if (ids == null || ids.size() == 0) {
            return null;
        }
        String[] array = new String[ids.size()];
        int index = 0;
        for (ID value : ids) {
            array[index++] = String.valueOf(value);
        }
        StringBuilder builder = new StringBuilder();
        makeInQuery(builder, otherIdName, array);
        return query(builder.toString(), null);
    }

    public String queryNameID(String id) {
        SQLiteDatabase db = getReadDB();
        if (db == null) {
            return null;
        }
        Cursor cursor = db.rawQuery("SELECT name FROM " + getTableName() + " WHERE " + ID + "='" + id + "'", null);
        String ret = null;
        if (cursor.moveToFirst()) {
            ret = cursor.getString(0);
        }
        cursor.close();
        return ret;
    }

    /**
     * Tell whether the data bean with specified id exists.
     *
     * @param id
     * @return false if not exists.
     */
    public boolean hasEntity(ID id) {
        return hasEntity(ID + "=?", new String[]{String.valueOf(id)});
    }

    /**
     * Tell if has entity
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public boolean hasEntity(String fieldName, Object fieldValue) {
        return hasEntity(fieldName + "=?", new String[]{String.valueOf(fieldValue)});
    }


    public List<ENTITY> queryAll() {
        return query(null, null);
    }

    public List<ENTITY> query(String selection, String[] selectionArgs, long from, int count) {
        return query(false, selection, selectionArgs, null, null, null, makeLimitClause(from, count), null);
    }

    public List<ENTITY> query(String selection, String[] selectionArgs, long from, int count, String orderBy) {
        return query(false, selection, selectionArgs, null, null, orderBy, makeLimitClause(from, count), null);
    }

    /**
     * @param distinct
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy            How to order the rows, formatted as an SQL ORDER BY
     *                           clause (excluding the ORDER BY itself). Passing null
     *                           will use the default sort order, which may be unordered.
     * @param limit
     * @param cancellationSignal
     * @return
     */
    public List<ENTITY> query(boolean distinct, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
        SQLiteDatabase db = getReadDB();
        if (db == null) {
            return null;
        }
        String table = getTableName();
        Cursor cursor = null;
        if (beyondV16()) {
            cursor = db.query(distinct, table, null, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal);
        } else {
            cursor = db.query(distinct, table, null, selection, selectionArgs, groupBy, having, orderBy, limit);
        }
        return convertListCloseCursor(cursor);
    }

    /**
     * @param selection
     * @param selectionArgs
     * @return
     */
    public List<ENTITY> query(String selection, String[] selectionArgs) {
        SQLiteDatabase db = getWriteDB();
        if (db == null) {
            return null;
        }
        Cursor cursor = db.query(getTableName(), null, selection, selectionArgs, null, null, null);
        return convertListCloseCursor(cursor);
    }

    public Cursor queryCursor(String[] columns, String selection, String[] selectionArgs) {
        return queryCursor(false, columns, selection, selectionArgs, null, null, null, null, null);
    }

    /**
     * @param distinct
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy            How to order the rows, formatted as an SQL ORDER BY
     *                           clause (excluding the ORDER BY itself). Passing null
     *                           will use the default sort order, which may be unordered.
     * @param limit
     * @param cancellationSignal
     * @return
     */
    public Cursor queryCursor(boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
        SQLiteDatabase db = getReadDB();
        if (db == null) {
            return null;
        }
        Cursor cursor = null;
        String table = getTableName();
        if (beyondV16()) {
            cursor = db.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal);
        } else {
            cursor = db.query(distinct, table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        }
        return cursor;
    }

    public Cursor rawQueryCursor(String sql, String[] selectionArgs, CancellationSignal cancellationSignal) {
        SQLiteDatabase db = getReadDB();
        if (db == null) {
            return null;
        }
        Cursor cursor = null;
        if (beyondV16()) {
            cursor = db.rawQuery(sql, selectionArgs, cancellationSignal);
        } else {
            cursor = db.rawQuery(sql, selectionArgs);
        }
        return cursor;
    }

    /**
     * This method will close the cursor.
     *
     * @param cursor
     * @return
     */
    protected List<ENTITY> convertListCloseCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }
        ArrayList<ENTITY> ret = null;
        if (cursor.getCount() != 0) {
            ret = new ArrayList<ENTITY>(cursor.getCount());
            while (cursor.moveToNext()) {
                ret.add(convert(cursor));
            }
        }
        cursor.close();
        return ret;
    }

    /**
     * @param entity
     * @return the row ID of the newly inserted row OR
     * the primary key of the existing row if the input param
     * 'conflictAlgorithm' = CONFLICT_IGNORE OR -1 if any error
     */
    public long insert(ENTITY entity) {
        ContentValues values = convertValues(entity);
        return insert(values);
    }

    public long insert(ContentValues values) {
        SQLiteDatabase db = getWriteDB();
        if (db == null) {
            return -1;
        }
        return db.insertWithOnConflict(getTableName(), null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int insert(List<ENTITY> list) {
        SQLiteDatabase db = getWriteDB();
        if (db == null) {
            return -1;
        }
        int size = list.size();
        try {
            db.beginTransaction();
            ContentValues values = null;
            String table = getTableName();
            ENTITY item = null;
            for (int i = 0; i < size; i++) {
                item = list.get(i);
                values = convertValues(item);
                if (db.insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE) > 0) {
                    continue;
                }
                throw new Exception("Insert data fail: " + item);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            db.endTransaction();
        }
        return size;
    }

    /**
     * @param values
     * @param where
     * @param whereArgs
     * @return the number of rows affected
     */
    public int update(ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = getWriteDB();
        if (db == null) {
            return -1;
        }
        return db.updateWithOnConflict(getTableName(), values, where, whereArgs, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * @param id
     * @param values
     * @return the number of rows affected
     */
    public int updateByID(ID id, ContentValues values) {
        if (id == null) {
            throw new NullPointerException("ID can not be null");
        }
        SQLiteDatabase db = getWriteDB();
        if (db == null) {
            return -1;
        }
        return db.updateWithOnConflict(getTableName(), values, ID + "='" + id + "'", null, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * @param id
     * @param entity
     * @return the number of rows affected
     */
    public int updateByID(ID id, ENTITY entity) {
        ContentValues values = convertValues(entity);
        values.remove(ID);
        return updateByID(id, values);
    }

    /**
     * @param where
     * @param whereArgs
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise.
     * To remove all rows and get a count pass "1" as the whereClause.
     */
    public int delete(String where, String[] whereArgs) {
        SQLiteDatabase db = getWriteDB();
        if (db == null) {
            return -1;
        }
        return db.delete(getTableName(), where, whereArgs);
    }

    /**
     * @param id
     * @return the number of rows affected if a whereClause is passed in, 0 otherwise.
     * To remove all rows and get a count pass "1" as the whereClause.
     */
    public int deleteByID(ID id) {
        SQLiteDatabase db = getWriteDB();
        if (db == null) {
            return -1;
        }
        return db.delete(getTableName(), ID + "='" + id + "'", null);
    }

    /**
     * Delete by an ID list.
     *
     * @param ids
     * @return Affected count or -1 if error.
     */
    public int deleteByIDs(ID... ids) {
        SQLiteDatabase db = getWriteDB();
        if (db == null) {
            return -1;
        }
        String table = getTableName();
        try {
            db.beginTransaction();
            for (ID id : ids) {
                if (db.delete(table, ID + "='" + id + "'", null) > 0) {
                    continue;
                }
                throw new Exception("Delete row fails: " + id);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            db.endTransaction();
        }
        return ids.length;
    }

    /**
     * Delete all rows in table.
     *
     * @return row count deleted.
     */
    public int deleteAll() {
        SQLiteDatabase db = getWriteDB();
        if (db == null) {
            return -1;
        }
        return db.delete(getTableName(), "1", null);
    }

    /**
     * @return
     */
    public long count() {
        SQLiteDatabase db = getReadDB();
        if (db == null) {
            return -1;
        }
        long ret = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(1) FROM " + getTableName(), null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ret = cursor.getLong(0);
            }
            cursor.close();
        }
        return ret;
    }

    public long count(String where, String[] whereArgs) {
        SQLiteDatabase db = getReadDB();
        if (db == null) {
            return -1;
        }
        String sql = "SELECT COUNT(1) FROM " + getTableName();
        if (where != null) {
            sql += " WHERE " + where;
        }
        long ret = 0;
        Cursor cursor = db.rawQuery(sql, whereArgs);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                ret = cursor.getLong(0);
            }
            cursor.close();
        }
        return ret;
    }

    public void execSql(String sql, Object[] bindArgs) {
        SQLiteDatabase db = getWriteDB();
        if (db == null) {
            return;
        }
        db.execSQL(sql, bindArgs);
    }

    public static boolean beyondV16() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static String makeLimitClause(long from, int count) {
        return from + "," + count;
    }

    public static void putAutoIncrementID(ContentValues values, long id) {
        if (id > 0) {
            values.put(ID, id);
        } else {
            values.putNull(ID);
        }
    }

    /**
     * Make a type query from "field IN ("  to ")"
     *
     * @param values
     * @return
     */
    public static void makeInQuery(StringBuilder sqlBuilder, String field, String[] values) {
        sqlBuilder.append(field).append(IN).append(BRACKET_START);
        for (String type : values) {
            sqlBuilder.append("'").append(type).append("'").append(COMMA);
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1).append(BRACKET_END);
    }

    public SQLiteDatabase getWriteDB() {
        if (!assertDB()) {
            return null;
        }
        return mDBHelper.getWritableDatabase();
    }

    public SQLiteDatabase getReadDB() {
        if (!assertDB()) {
            return null;
        }
        return mDBHelper.getReadableDatabase();
    }

    public boolean assertDB() {
        return (mDBHelper != null && mDBHelper.getReadableDatabase().isOpen());
    }

    public void close() {
        mDBHelper = null;
    }

}
