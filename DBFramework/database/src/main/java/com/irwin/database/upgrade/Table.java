package com.irwin.database.upgrade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by ARES on 2017/7/14.
 */

public class Table {
    public String Name;
    public String CreateSql;
    private List<Column> mColumns;

    public Table(String name) {
        Name = name;
    }

    public Table(String name, String createSql) {
        Name = name;
        CreateSql = createSql;
    }


    public List<Column> getColumns() {
        if ((mColumns == null || mColumns.size() == 0) && CreateSql != null) {
            mColumns = resolveColumns(CreateSql);
        }
        return mColumns == null ? Collections.EMPTY_LIST : mColumns;
    }

    public static List<Column> resolveColumns(String createSql) {
        ArrayList<Column> list = null;
        String[] array = getColumnsStatement(createSql).split(",");
        if (array.length > 0) {
            list = new ArrayList<>(array.length);
            Column column;
            for (String item : array) {
                column = Column.valueOf(item);
                if (column != null) {
                    list.add(column);
                }
            }
        }
        return list == null ? Collections.EMPTY_LIST : list;
    }

    public static String getColumnsStatement(String createSql) {
        final char BRACKET_START = '(';
        final char BRACKET_END = ')';
        int startLoc = createSql.indexOf(BRACKET_START);
        int endLoc = createSql.lastIndexOf(BRACKET_END);
        if (startLoc >= 0 && endLoc >= 0) {
            return createSql.substring(startLoc + 1, endLoc);
        }
        return createSql;
    }

    @Override
    public String toString() {
        return "Table{" +
                "Name='" + Name + '\'' +
                ", CreateSql='" + CreateSql + '\'' +
                ", mColumns=" + mColumns +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Table table = (Table) o;

        if (Name != null ? !Name.equals(table.Name) : table.Name != null) return false;
        return CreateSql != null ? CreateSql.equals(table.CreateSql) : table.CreateSql == null;

    }

    public boolean equalsColumns(Table table) {
        String myColumns = getColumnsStatement(CreateSql).toUpperCase();
        String columns = getColumnsStatement(table.CreateSql).toUpperCase();
        return (myColumns.equals(columns));
    }

    @Override
    public int hashCode() {
        int result = Name != null ? Name.hashCode() : 0;
        result = 31 * result + (CreateSql != null ? CreateSql.hashCode() : 0);
        return result;
    }

    public static class Column {
        public String Name;
        public String Type;
        public String Extra;

        public static Column valueOf(String column) {
            final char BLANK = ' ';
            int loc = column.indexOf(BLANK);
            while (loc == 0) {
                column = column.substring(1);
                loc = column.indexOf(BLANK);
            }
            Column ret = null;
            if (loc > 0) {
                ret = new Column();
                ret.Name = column.substring(0, loc);
                column = column.substring(loc + 1);
                loc = column.indexOf(BLANK);
                if (loc > 0) {
                    ret.Type = column.substring(0, loc);
                    ret.Extra = column.substring(loc + 1);
                } else {
                    ret.Type = column;
                }
            }
            return ret;
        }

        public boolean equalsName(Column column) {
            return (Name != null && Name.equals(column.Name));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Column column = (Column) o;

            if (Name != null ? !Name.equals(column.Name) : column.Name != null) return false;
            return Type != null ? Type.equals(column.Type) : column.Type == null;

        }

        @Override
        public int hashCode() {
            int result = Name != null ? Name.hashCode() : 0;
            result = 31 * result + (Type != null ? Type.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Column{" +
                    "Name='" + Name + '\'' +
                    ", Type='" + Type + '\'' +
                    ", Extra='" + Extra + '\'' +
                    '}';
        }
    }
}
