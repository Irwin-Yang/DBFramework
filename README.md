# DBFramework
Lite sqlite Database framework on Android which can handle table hierarchies elegantly and efficiently. It support upgrade database  seamlessly too.
</br>

# Features
 1.Handle table hierarchies elegantly.
 </br>
 2.Support upgrade database seamlessly.
  </br>
 3.Structured, Efficient and Easy to use, save you out of a lot of redundant code.
 </br>

# How to use
#### 1.Download or clone project and import sub module called database into your project. Then implement your daos which should extends from BaseDao：
```Java
public class UserDao extends BaseDao<User, Long> implements User.Columns {

     @Override
    public int addProperties(List<Property> list, int offset) {
        //Add columns here.
        list.add(new Property(ID, Property.TYPE_INTEGER).setPrimaryKey(true));
        list.add(new Property(NAME, Property.TYPE_TEXT));
        list.add(new Property(AGE, Property.TYPE_INTEGER));
        return 0;
    }

    @Override
    public User createEntity() {
        //Create entity instance.
        return new User();
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
    public ContentValues convertValues(User user) {
        //Convert entity to content values for inserting or updating.
        ContentValues values = new ContentValues();
        putAutoIncrementID(values, user.getId());
        values.put(NAME, user.getName());
        values.put(AGE, user.getAge());
        return values;
    }
}
```
</br>

#### 2.Implement your own DatabaseHelper extends from BaseDBHelper：
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

#### 3.Use daos to access data,Enjoy it.
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

        List<Employee> list = EmployeeDao.getInstance().queryAll();
```

</br>

# Future
#### Maybe we can add support for annotation-processing so we can generate daos according to entities like [GreenDao](https://github.com/greenrobot/greenDAO/).

</br>
Any advice will be appreciated:D

</br>
Email:zhpngyang52@gmail.com
