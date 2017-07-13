package com.irwin.dbframework.daos;

import android.content.ContentValues;
import android.database.Cursor;

import com.irwin.database.BaseDao;
import com.irwin.database.Property;
import com.irwin.dbframework.beans.User;

import java.util.List;

/**
 * Created by ARES on 2017/7/13.
 */

public class UserDao<USER extends User> extends BaseDao<USER, Long> implements User.Columns {
    private static final UserDao INSTANCE = new UserDao();

    public static UserDao getInstance() {
        return INSTANCE;
    }

    UserDao() {
    }

    @Override
    public int addProperties(List<Property> list, int offset) {
        //Add columns here.
        list.add(new Property(ID, Property.TYPE_INTEGER).setPrimaryKey(true));
        list.add(new Property(NAME, Property.TYPE_TEXT));
        list.add(new Property(AGE, Property.TYPE_INTEGER));
        return 0;
    }

    @Override
    public USER createEntity() {
        //Create entity instance.
        return (USER)new User();
    }

    @Override
    public int fillEntity(USER user, Cursor cursor, int offset) {
        //Fill entity instance with data from cursor in the order of add columns to increase efficiency.
        user.setId(cursor.getLong(offset++));
        user.setName(cursor.getString(offset++));
        user.setAge(cursor.getInt(offset++));
        return offset;
    }

    @Override
    public String getTableName() {
        //Return table name.
        return "User";
    }

    @Override
    public ContentValues convertValues(USER user) {
        //Convert entity to content values for inserting or updating.
        ContentValues values = new ContentValues();
        putAutoIncrementID(values, user.getId());
        values.put(NAME, user.getName());
        values.put(AGE, user.getAge());
        return values;
    }
}
