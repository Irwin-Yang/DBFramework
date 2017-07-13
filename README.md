# DBFramework
Lite sqlite Database framework on Android which can handle table hierarchies elegantly and efficiently。It support upgrade database  seamlessly too.
</br>

#Features
 Handle table hierarchies elegantly.
 </br>
 Support upgrade database seamlessly.
  </br>
 Structured, Efficient and Easy to use, save you out of a lot of redundant code.
 </br>

# How to use
#### 1.Download or clone project and import sub module called database into your project。Then implement your daos which should extends from BaseDao：
</br>
```Java
public class UserDao extends BaseDao<User, Long> implements User.Columns {
...
}
```
</br>

#### Implement your own DatabaseHelper extends from BaseDBHelper：
<br>
```Java
public class MyDBHelper extends BaseDBHelper {
    @Override
    public List<AbstractDao> getTables() {
	    //Return your daos here then we can create/update table for you.
        ArrayList<AbstractDao> list = new ArrayList<>();
        list.add(UserDao.getInstance());
        list.add(EmployeeDao.getInstance());
        return list;
    }
...
}
```
</br>
#### Use daos to access data,Enjoy it.
```Java
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
```
</br>

# Future
#### Maybe we can add support for annotation-processing so we can generate daos according to entities like [GreenDao](https://github.com/greenrobot/greenDAO/).

</br>
Any advice will be appreciated:D

</br>
More information about SkinFramework:http://www.cnblogs.com/oxgen/p/7154699.html
</br>
Email:zhpngyang52@gmail.com
