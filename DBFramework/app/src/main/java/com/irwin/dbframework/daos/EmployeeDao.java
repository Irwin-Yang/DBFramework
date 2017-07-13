package com.irwin.dbframework.daos;

import android.content.ContentValues;
import android.database.Cursor;

import com.irwin.database.Property;
import com.irwin.dbframework.beans.Employee;
import com.irwin.dbframework.beans.User;

import java.util.List;

/**
 * Created by ARES on 2017/7/13.
 */

public class EmployeeDao extends UserDao<Employee> implements Employee.Columns {
    private static final EmployeeDao INSTANCE = new EmployeeDao();

    public static EmployeeDao getInstance() {
        return INSTANCE;
    }

    EmployeeDao() {
    }


    @Override
    public int addProperties(List<Property> list, int offset) {
        super.addProperties(list, offset);
        list.add(new Property(SALARY, Property.TYPE_INTEGER));
        list.add(new Property(POST, Property.TYPE_INTEGER));
        list.add(new Property(CODE, Property.TYPE_TEXT));
        return 0;
    }

    @Override
    public Employee createEntity() {
        return new Employee();
    }

    @Override
    public int fillEntity(Employee employee, Cursor cursor, int offset) {
        offset = super.fillEntity(employee, cursor, offset);
        employee.setSalary(cursor.getInt(offset++));
        employee.setPost(cursor.getInt(offset++));
        employee.setCode(cursor.getString(offset++));
        return offset;
    }

    @Override
    public String getTableName() {
        return "Employee";
    }

    @Override
    public ContentValues convertValues(Employee employee) {
        ContentValues values = super.convertValues(employee);
        values.put(SALARY, employee.getSalary());
        values.put(POST, employee.getPost());
        values.put(CODE, employee.getCode());
        return values;
    }
}
