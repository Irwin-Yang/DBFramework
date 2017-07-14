package com.irwin.dbframework;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.irwin.database.upgrade.StrictUpgrader;
import com.irwin.dbframework.beans.Employee;
import com.irwin.dbframework.beans.User;
import com.irwin.dbframework.daos.EmployeeDao;
import com.irwin.dbframework.daos.UserDao;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DBTest";
    final String DB_NAME = "TestDB.db";
    final int DB_VERSION = 7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new MyDBHelper(this, DB_NAME, null, DB_VERSION);
        Random random = new Random(System.currentTimeMillis());
        User user = new User();
        user.setAge(random.nextInt(80));
        user.setName("Irwin[" + random.nextInt(100) + "]");
        UserDao.getInstance().insert(user);

        Employee employee = new Employee();
        employee.setAge(random.nextInt(60));
        employee.setName("Jack[" + random.nextInt(100) + "]");
        employee.setSalary(random.nextInt(50000));
        employee.setPost(random.nextInt(8));
        employee.setCode(String.valueOf(random.nextInt()));
        employee.setScore(random.nextInt(100));
        EmployeeDao.getInstance().insert(employee);

        //We provided many convenient methods for using in common database development. See {@link com.irwin.database.AbstractDao} for more information.
        User savedUser = (User) UserDao.getInstance().queryByID(1L);
        Log.i(TAG, "Saved user:\n" + savedUser);

        List<Employee> list = EmployeeDao.getInstance().queryAll();
        ((TextView) findViewById(R.id.InfoView)).setText("Saved employee: \r\n" + list);
    }
}
