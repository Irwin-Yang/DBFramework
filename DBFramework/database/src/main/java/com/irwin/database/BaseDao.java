package com.irwin.database;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Irwin on 2016/6/2.
 */
public abstract class BaseDao<ENTITY, ID> extends AbstractDao<ENTITY, ID> {

    public abstract int addProperties(List<Property> list, int offset);

    public abstract ENTITY createEntity();

    public abstract int fillEntity(ENTITY entity, Cursor cursor, int offset);


    @Override
    public final List<Property> getPropertyList() {
        ArrayList<Property> list = new ArrayList<>();
        addProperties(list, 0);
        return list;
    }

    @Override
    public final ENTITY convert(Cursor cursor) {
        ENTITY ret = createEntity();
        fillEntity(ret, cursor, 0);
        return ret;
    }
}
