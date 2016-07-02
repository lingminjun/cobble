package com.ssn.cobble.kit;


import java.util.List;

/**
 * Created by lingminjun on 16/6/7.
 * “量子播发器”，经连续的任务差分成非连续的，更具最大等待时长，或者最大播发量来差分
 * 注意：非线程安全
 */
public final class Quantum<T extends Object> {

    /**
     * 播发回调
     * @param <T>
     */
    public static interface Express <T extends Object> {
        void express(Quantum quantum, List<T> objs);
    }

    /**
     * 构造函数
     * @param count
     * @param interval
     */
    public Quantum(int count, int interval) {
        _maxCount = count <= 0 ? DEFAULT_MAX_COUNT : count;
        _interval = interval <= 0 ? DEFAULT_INTERVAL : interval;
        _stack = new Stack<>(_maxCount);
    }

    /**
     * push数据
     * @param obj
     */
    public void push(T obj) {
        if (obj == null) {return;}

        TaskQueue.mainQueue().execute(delayExpress);
        _stack.push(obj);

        if (_stack.isFull()) {//直接播发
            List<T> objs = _stack.toList();
            _stack.clear();
            express(objs);
        } else {//延后播发
            TaskQueue.mainQueue().executeDelayed(delayExpress, _interval);
        }
    }

    /**
     * 若一次push数据超过 maxCount，播发数据将会立即出发，数量为当前数量（大于maxCount）
     * @param objs
     */
    public void pushAll(List<T> objs) {
        if (objs == null && objs.size() == 0) {return;}

        TaskQueue.mainQueue().execute(delayExpress);

        List<T> list = null;
        for (T obj : objs) {
            if (list != null) {//说明栈已经满，为了节约播发次数，不做拆分，一次播放
                list.add(obj);
                continue;
            }

            _stack.push(obj);

            if (_stack.isFull()) {//满了后，直接赋值给list
                list = _stack.toList();
                _stack.clear();
            }
        }

        if (list != null) {
            express(list);
        } else {//延后播发
            TaskQueue.mainQueue().executeDelayed(delayExpress, _interval);
        }
    }

    /**
     * 最大播发数量
     * @return
     */
    public int getMaxCount() {return _maxCount;}

    /**
     * 间隔时间
     * @return
     */
    public int getInterval() {return _interval;}

    /**
     * 设置播放器实现
     * @param express
     */
    public void setExpress(Express<T> express) {
        _express = express;
    }


    private void express(List<T> objs) {
        if (objs == null || objs.size() == 0) {
            return;
        }

        if (_express != null) { try {
            _express.express(this,objs);
        } catch (Throwable e) {}}
    }


    Runnable delayExpress = new Runnable() {
        @Override
        public void run() {
            List<T> objs = _stack.toList();
            _stack.clear();
            express(objs);
        }
    };


    private static final int DEFAULT_MAX_COUNT = 100;
    private static final int DEFAULT_INTERVAL = 100;//0.1毫秒

    private int _maxCount;
    private int _interval;//mis

    private Stack<T> _stack;//采用循环栈存储数据

    private Express<T> _express;
}
