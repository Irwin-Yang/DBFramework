package com.irwin.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

import static com.irwin.database.Preconditions.checkArgument;


/**
 * Created by Irwin on 2016/6/2.
 * Observable dao decorator.You can use it to decorate a AbstractDao\BaseDao exclude ObservableDao to implement observable dao .
 */
public class ObservableDaoDecor<ENTITY, ID> extends ObservableDao<ENTITY, ID> {

    private final AbstractDao<ENTITY, ID> mHostDao;

    public ObservableDaoDecor(AbstractDao<ENTITY, ID> dao) {
        checkArgument(dao instanceof ObservableDao, "Can not decorate a ObservableDao with ObservableDao Decorator.");
        mHostDao = dao;
        setDBHelper(dao.getDBHelper());
    }

    @Override
    public List<Property> getPropertyList() {
        return mHostDao.getPropertyList();
    }

    @Override
    public String getTableName() {
        return mHostDao.getTableName();
    }

    @Override
    public ENTITY convert(Cursor cursor) {
        return mHostDao.convert(cursor);
    }

    @Override
    public ContentValues convertValues(ENTITY data) {
        return mHostDao.convertValues(data);
    }

    @Override
    public int insert(List<ENTITY> list) {
        return super.insert(list);
    }

    @Override
    public AbstractDao setDBHelper(SQLiteOpenHelper dbHelper) {
        mHostDao.setDBHelper(dbHelper);
        return super.setDBHelper(dbHelper);
    }
}
