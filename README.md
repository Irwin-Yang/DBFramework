# DBFramework
Lite sqlite Database framework on Android which can handle table hierarchies elegantly and efficiently. It support upgrade database  seamlessly and subscribe events
of table change too.
</br>

# Features
 1.Handle table hierarchies elegantly.
 </br>
 2.Support upgrade database seamlessly.
  </br>
 3.Support subscribe events of table change.
  </br>
 4.Structured, Efficient and Easy to use, save you out of a lot of redundant code.
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

#### 3.Use daos to access data.
```Java
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
```

</br>

#### 4.We use `DefaultUpgrader` to upgrade the database as default. You can choose `StrictUpgrader` which will treat columns changes strictly, or implement your own upgrader and tell your Database helper:

```Java
 new MyDBHelper(this, DB_NAME, null, DB_VERSION).setUpgrader(YOUR CHOOSEN/IMPLEMENTATION);
```

</br>

#### 5.Implement your dao as ObservableDao or Decorate a dao with ObservableDaoDecor so you can subscribe events of table change by `registerObserver(IDBObserver observer)`, Enjoy it.

</br>

# Future
#### Maybe we can add support for annotation-processing so we can generate daos according to entities like [GreenDao](https://github.com/greenrobot/greenDAO/).

</br>
Any advice will be appreciated:D

</br>
More information about DBFramework:http://www.cnblogs.com/oxgen/p/7161986.html
</br>
Email:zhpngyang52@gmail.com
