package com.irwin.database;

import java.util.List;

/**
 * Created by Irwin on 2015/12/15.  数据库相应字段对应表
 */
public class Property {
    public static final String QUESTION_MARK = "?";

    public static final String IN = " IN ";

    public static final String OR = " OR ";

    public static final String AND = " AND ";

    public static final String TYPE_INTEGER = "INTEGER";

    public static final String TYPE_TEXT = "TEXT";

    public static final String TYPE_REAL = "REAL";

    public static final String PRIMARY_KEY = "PRIMARY KEY";

    public static final String NOT_NULL = "NOT NULL";

    public static final String AUTOINCREMENT = "AUTOINCREMENT";

    public static final String DEFAULT = "DEFAULT";

    public static final String SPACE = " ";

    public static final String BRACKET_START = "(";

    public static final String BRACKET_END = ")";

    public static final String COMMA = ",";

    public static final String NULL_VALUE = "NULL";

    private String mField;

    private int mIndex;

    private String mType;

    private boolean mPrimaryKey = false;

    private boolean mAutoIncrement = false;

    private boolean mNotNull = false;

    private String mDefaultValue;

    public static String createSQL(List<Property> list, String tableName) {
        int size = list.size();
        StringBuilder builder = new StringBuilder("CREATE TABLE ");
        builder.append(tableName).append(BRACKET_START);
        Property property;
        for (int i = 0; i < size; i++) {
            property = list.get(i);
            if (property.isPrimaryKey()) {
                builder.append(property.getField()).append(SPACE)
                        .append(property.getType()).append(SPACE)
                        .append(PRIMARY_KEY);
                if (property.isAutoIncrement()) {
                    builder.append(SPACE).append(AUTOINCREMENT);
                }
            } else {
                builder.append(property.getField()).append(SPACE).append(property.getType());
            }

            if (property.getDefaultValue() != null) {
                builder.append(SPACE).append(DEFAULT).append(SPACE).append(NULL_VALUE);
            }

            if (property.isNotNull()) {
                builder.append(SPACE).append(NOT_NULL);
            }

            builder.append(COMMA);
        }
        //Delete last comma.
        builder.deleteCharAt(builder.length() - 1);
        builder.append(BRACKET_END);
        return builder.toString();
    }

    public Property(String field, String type) {
        this(field, type, 0);
    }

    public Property(String field, String type, int index) {
        mField = field;
        mType = type;
        mIndex = index;
    }

    public String getField() {
        return mField;
    }

    public Property setField(String field) {
        mField = field;
        return this;
    }

    public int getIndex() {
        return mIndex;
    }

    public Property setIndex(int index) {
        mIndex = index;
        return this;
    }

    public String getType() {
        return mType;
    }

    public Property setType(String type) {
        mType = type;
        return this;
    }

    public boolean isPrimaryKey() {
        return mPrimaryKey;
    }

    public Property setPrimaryKey(boolean asPrimaryKey) {
        mPrimaryKey = asPrimaryKey;
        return this;
    }

    public boolean isAutoIncrement() {
        return mAutoIncrement;
    }

    public Property setAutoIncrement(boolean autoIncrement) {
        mAutoIncrement = autoIncrement;
        return this;
    }

    public boolean isNotNull() {
        return mNotNull;
    }

    public Property setNotNull(boolean notNull) {
        mNotNull = notNull;
        return this;
    }

    public String getDefaultValue() {
        return mDefaultValue;
    }

    public Property setDefaultValue(String defaultValue) {
        mDefaultValue = defaultValue;
        return this;
    }
}
