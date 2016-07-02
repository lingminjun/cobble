package com.ssn.cobble.kit;

import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lingminjun on 15/11/12.
 * 严格的单实例对象管理器（一个key仅仅生产一个实例），实例若无人引用，将被gc回收，线程安全
 */
public final class RigidFactory<T> {

    public RigidFactory(SingletonCreator<T> creator) {
        _creator = creator;
        _cache = new HashMap<>();
    }

    /**
     * 构造器
     * @param <T>
     */
    public static interface SingletonCreator<T> {
        public T onCreate(final String key, final Map<String, Object> params);
    }


    /**
     * 获取实例
     * @param key
     * @return
     */
    public T get(String key) {
        return get(key,null);
    }

    /**
     * 获取实例
     * @param key
     * @param params
     * @return
     */
    public synchronized T get(String key,Map<String,Object> params) {
        if (TextUtils.isEmpty(key)) {return null;}

        WeakReference<T> weak = _cache.get(key);
        if (weak != null) {
            T obj = weak.get();
            if (obj != null) {
                return obj;
            }
        }

        if (_creator != null) {
            T obj = _creator.onCreate(key, params);
            if (obj != null) {
                _cache.put(key,new WeakReference<T>(obj));
                return obj;
            }
        }

        return null;
    }

    private Map<String,WeakReference<T> > _cache;
    private SingletonCreator<T> _creator;
}
