package com.irwin.database;

import android.content.ContentValues;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Irwin on 2015/12/16.
 * <ENTITY> - Entity type
 * <ID> - ID type
 */
public abstract class ObservableDao<ENTITY, ID> extends AbstractDao<ENTITY, ID> {
    public static final int INSERT = 1;

    public static final int DELETE = 2;

    public static final int UPDATE = 3;

    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    private ArrayList<IDBObserver> mObservers = new ArrayList<>();

    public void registerObserver(IDBObserver observer) {
        mObservers.add(observer);
    }

    public void unregisterObserver(IDBObserver observer) {
        synchronized (mObservers) {
            mObservers.remove(observer);
        }
    }


    @Override
    public int update(ContentValues values, String where, String[] whereArgs) {
        return update(values, where, whereArgs, null, SuperObserver);
    }

    public int update(ContentValues values, String where, String[] whereArgs, Object tag, IDBObserver observer) {
        int ret = super.update(values, where, whereArgs);
        if (ret > 0 && observer != null) {
            observer.onChange(UPDATE, values.get(ID), null, ret, tag);
        }
        return ret;
    }

    @Override
    public int updateByID(ID id, ContentValues values) {
        return updateByID(id, values, null, SuperObserver);
    }

    public int updateByID(ID id, ContentValues values, Object tag, IDBObserver observer) {
        int ret = super.updateByID(id, values);
        if (ret > 0 && observer != null) {
            observer.onChange(UPDATE, id, null, ret, tag);
        }
        return ret;
    }


    @Override
    public int delete(String where, String[] whereArgs) {
        return delete(where, whereArgs, null, SuperObserver);
    }

    public int delete(String where, String[] whereArgs, Object tag, IDBObserver observer) {
        int ret = super.delete(where, whereArgs);
        if (ret > 0 && observer != null) {
            observer.onChange(DELETE, null, null, ret, tag);
        }
        return ret;
    }

    @Override
    public int deleteByID(ID id) {
        return deleteByID(id, null, SuperObserver);
    }

    public int deleteByID(ID id, Object tag, IDBObserver observer) {
        int ret = super.deleteByID(id);
        if (ret > 0 && observer != null) {
            observer.onChange(DELETE, id, null, ret, tag);
        }
        return ret;
    }

    @Override
    public int deleteByIDs(ID... ids) {
        return deleteByIDs(null, SuperObserver, ids);
    }

    public int deleteByIDs(Object tag, IDBObserver observer, ID... ids) {
        int ret = super.deleteByIDs(ids);
        if (ret > 0 && observer != null) {
            observer.onChange(DELETE, null, null, ret, tag);
        }
        return ret;
    }

    @Override
    public int deleteAll() {
        return deleteAll(null, SuperObserver);
    }

    public int deleteAll(Object tag, IDBObserver observer) {
        int ret = super.deleteAll();
        if (ret > 0 && observer != null) {
            observer.onChange(DELETE, null, null, ret, tag);
        }
        return ret;
    }

    @Override
    public int insert(List<ENTITY> list) {
        return insert(list, null, SuperObserver);
    }

    public int insert(List<ENTITY> list, Object tag, IDBObserver observer) {
        int ret = super.insert(list);
        if (ret > 0 && observer != null) {
            observer.onChange(INSERT, null, null, list.size(), tag);
        }
        return ret;
    }

    @Override
    public long insert(ENTITY entity) {
        return insert(entity, null, SuperObserver);
    }

    @Override
    public long insert(ContentValues values) {
        return insert(values, null, SuperObserver);
    }

    public long insert(ContentValues values, Object tag, IDBObserver observer) {
        long ret = super.insert(values);
        if (ret > 0 && observer != null) {
            observer.onChange(INSERT, null, null, 1, tag);
        }
        return ret;
    }

    public long insert(ENTITY data, Object tag, IDBObserver observer) {
        long ret = super.insert(data);
        if (ret > 0 && observer != null) {
            observer.onChange(INSERT, null, data, 1, tag);
        }
        return ret;
    }

    @Override
    public void execSql(String sql, Object[] bindArgs) {
        execSql(sql, bindArgs, null, SuperObserver);
    }

    public void execSql(String sql, Object[] bindArgs, Object tag, IDBObserver observer) {
        super.execSql(sql, bindArgs);
        sql = sql.toUpperCase();
        if (observer != null) {
            int type = -1;
            if (sql.startsWith("INSERT")) {
                type = INSERT;
            } else if (sql.startsWith("UPDATE")) {
                type = UPDATE;
            } else if (sql.startsWith("DELETE")) {
                type = DELETE;
            }
            if (type != -1) {
                observer.onChange(type, null, null, 0, tag);
            }
        }
    }

    public void notifyDBChange(final int type, final Object id, final Object entity, final int count, final Object tag) {
        synchronized (mObservers) {
            Iterator<IDBObserver> iterator = mObservers.iterator();
            while (iterator.hasNext()) {
                final IDBObserver observer = iterator.next();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        observer.onChange(type, id, entity, count, tag);
                    }
                });
            }
        }
    }

    /**
     * DBObserver for this dao.
     */
    protected final IDBObserver SuperObserver = new IDBObserver() {
        @Override
        public void onChange(int type, Object id, Object entity, int count, Object tag) {
            notifyDBChange(type, id, entity, count, tag);
        }
    };

}
