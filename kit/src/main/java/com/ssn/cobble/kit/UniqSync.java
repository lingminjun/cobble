package com.ssn.cobble.kit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lingminjun on 16/4/14.
 * 一个key只有一个Task任务，防止一个key产生多个Task
 */
public final class UniqSync {
    private static class Singleton {
        private static final UniqSync INSTANCE = new UniqSync();
    }

    private UniqSync() {

    }

    public static final UniqSync getInstance() {
        return Singleton.INSTANCE;
    }


    public static interface Process {
        public Object process(String key);
    }

    private static class Task {
        String key;
        Process process;
        Map map;
        Object obj;

        public Task(Process process, String key, Map map) {
            this.map = map;
            this.process = process;
            this.key = key;
        }

        public synchronized Object waitObject() {
            if (obj != null) {return obj;}

            try {
                obj = process.process(key);
            } catch (Throwable e) {}
            map.remove(key);

            return obj;
        }
    }

    private Map<String,Task> map = new ConcurrentHashMap<>();


    public Object sync(String key, Process process) {

        //此处稍微有一点无法去重，若在此处加锁，势必将成为多线程并发瓶颈，不想管的task创建被排队执行
        Task task = map.get(key);
        if (task == null) {
            task = new Task(process,key,map);
            map.put(key,task);
        }
        return task.waitObject();
    }
}
