package com.irwin.database;

/**
 * Created by Irwin on 2016/1/8.
 */
public interface IDBObserver {
    /**
     * Called back on database changed.
     *
     * @param type   Change type.
     * @param id     Entity id,may be null.
     * @param entity Entity, may be null.
     * @param count  Affected count.
     * @param tag    Operation tag,may be null.
     */
    public void onChange(int type, Object id, Object entity, int count, Object tag);
}
