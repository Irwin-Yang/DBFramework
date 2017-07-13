package com.irwin.database;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Irwin on 2016/5/20.
 */
public class TableBuilder extends ArrayList<Property> {

    private final String mTableName;

    public TableBuilder(String name) {
        super();
        mTableName = name;
    }

    public TableBuilder(String name, int capacity) {
        super(capacity);
        mTableName = name;
    }

    public String getTableName() {
        return mTableName;
    }

    public TableBuilder addProperty(Property property) {
        add(property);
        return this;
    }

    public void removeProperty(Property property) {
        remove(property);
    }

    public Property removeByField(String name) {
        if (size() == 0) {
            return null;
        }
        Iterator<Property> iterator = iterator();
        Property p;
        while (iterator.hasNext()) {
            p = iterator.next();
            if (p.getField().equals(name)) {
                iterator.remove();
                return p;
            }
        }
        return null;
    }

    public String toSQL() {
        return Property.createSQL(this, mTableName);
    }

    @Override
    public String toString() {
        return "Table "+mTableName+": "+(size() > 0 ? toSQL() : "No fields");
    }
}
