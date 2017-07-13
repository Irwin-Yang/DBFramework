package com.irwin.dbframework;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.irwin.dbframework.beans.Employee;
import com.irwin.dbframework.beans.User;
import com.irwin.dbframework.daos.EmployeeDao;
import com.irwin.dbframework.daos.UserDao;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DBTest";
    final String DB_NAME = "TestDB.db";
    final int DB_VERSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new MyDBHelper(this, DB_NAME, null, DB_VERSION);
        User user = new User();
        user.setAge(25);
        user.setName("Irwin");
        UserDao.getInstance().insert(user);

        Employee employee = new Employee();
        employee.setAge(28);
        employee.setName("Jack");
        employee.setSalary(15000);
        employee.setPost(1);
        employee.setCode("20160820");
        EmployeeDao.getInstance().insert(employee);

        //We provided many convenient methods for using in common database development. See {@link com.irwin.database.AbstractDao} for more information.
        User savedUser = (User) UserDao.getInstance().queryByID(1L);
        Log.i(TAG, "Saved user:\n" + savedUser);

        List<Employee> list = EmployeeDao.getInstance().queryAll();
        ((TextView) findViewById(R.id.InfoView)).setText("Saved employee: \r\n" + list.get(0));
    }
}
