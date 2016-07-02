package com.ssn.cobble.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lingminjun on 16/5/16.
 */
public final class KeyStack<K extends Object,T extends Object> {
    private Object[] _keys;
    private Map<K,T> _stack;
    private int _idx;
    private int MAX_SIZE;

    public KeyStack(int size) {
        MAX_SIZE = size < 0 ? 10 : size;
        _keys = new Object[MAX_SIZE];
        _stack = new HashMap<>();
    }

    /**
     * 压入栈顶
     * @param key 不允许为空
     * @param obj 不允许为空
     * @return
     */
    public T push(K key,T obj) {
        if (key == null || obj == null) {return null;}

        K oldKey = (K)_keys[_idx%MAX_SIZE];
        if (oldKey != null) {
            _stack.remove(oldKey);
        }

        _keys[_idx%MAX_SIZE] = key;//替换栈顶元素
        _stack.put(key,obj);

        _idx = ((_idx + 1)%MAX_SIZE);
        return obj;
    }

    /**
     * 移除栈顶
     * @return
     */
    public T pop() {
        _idx = (_idx + MAX_SIZE - 1)%MAX_SIZE;
        K key = (K)_keys[_idx%MAX_SIZE];//取出栈顶元素
        _keys[_idx%MAX_SIZE] = null;

        T obj = null;
        if (key != null) {
            obj = _stack.get(key);
            _stack.remove(key);
        }
        return obj;
    }

    /**
     * 栈顶元素
     * @return
     */
    public T top() {
        int idx = (_idx + MAX_SIZE - 1)%MAX_SIZE;
        K key = (K)_keys[idx%MAX_SIZE];//取出栈顶元素

        T obj = null;
        if (key != null) {
            obj = _stack.get(key);
        }
        return obj;
    }

    /**
     * 移除栈顶
     * @return
     */
    public T pop(K key) {
        if (key == null) {return null;}

        boolean fond = false;
        for (int i = 1; i <= MAX_SIZE; i++) {

            int idx = (_idx + MAX_SIZE - i)%MAX_SIZE;
            K akey = (K)_keys[idx%MAX_SIZE];//取出栈顶元素

            if (key == null) {break;}

            //找到对应的数据
            if (!fond && akey.equals(key)) {
                fond = true;

                //开始移位排序
                for (int j = 1; j < i; j++) {
                    _keys[(idx + j - 1 + MAX_SIZE)%MAX_SIZE] = _keys[(idx + j)%MAX_SIZE];
                }
                _keys[(idx + i - 1)%MAX_SIZE] = null;//最后将数据提前

                break;
            }
        }

        T obj = null;
        if (fond) {//取出栈顶元素
            _idx = (_idx + MAX_SIZE - 1) % MAX_SIZE;
            _keys[_idx%MAX_SIZE] = null;
            obj = _stack.get(key);
            _stack.remove(key);
        }

        return obj;
    }

    /**
     * 表示栈满
     * @return
     */
    public boolean isFull() {
        return _keys[_idx%MAX_SIZE] != null;//表示是满栈
    }

    /**
     * 清空栈
     * @return list 是 fifo
     */
    public List<T> clear() {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < MAX_SIZE; i++) {
            T obj = pop();
            if (obj == null) {break;}

            list.add(0,obj);//保持原有顺序
        }
        return list;
    }

    /**
     * 复制栈
     * @return list 是 fifo
     */
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        for (int i = 1; i <= MAX_SIZE; i++) {

            int idx = (_idx + MAX_SIZE - i)%MAX_SIZE;
            K key = (K)_keys[idx%MAX_SIZE];//取出栈顶元素

            if (key == null) {break;}

            T obj = _stack.get(key);
            if (obj != null) {
                list.add(0, obj);//保持原有顺序
            }
        }
        return list;
    }

    /**
     * 逆向复制栈
     * @return list与栈顺序一致
     */
    public List<T> toReverseList() {
        List<T> list = new ArrayList<>();
        for (int i = 1; i <= MAX_SIZE; i++) {

            int idx = (_idx + MAX_SIZE - i)%MAX_SIZE;
            K key = (K)_keys[idx%MAX_SIZE];//取出栈顶元素

            if (key == null) {break;}

            T obj = _stack.get(key);
            if (obj != null) {
                list.add(obj);//保持原有顺序
            }
        }
        return list;
    }

    /**
     * 包含某个元素
     * @param key
     * @return
     */
    public boolean contains(K key) {
        if (key == null) {return false;}

        return _stack.containsKey(key);
    }

    /**
     * 获取某个元素
     * @param key
     * @return
     */
    public T get(K key) {
        if (key == null) {return null;}

        return _stack.get(key);
    }

//    public static void main(String[] arg) {
//        CycleStack<String> stack = new CycleStack<>(5);
//        stack.push("1");
//        stack.push("2");
//        stack.push("3");
//        stack.push("4");
//        stack.push("5");
//        stack.push("6");
//        String str = stack.toList().toString();
//        stack.update_push("7");
//        str = stack.toList().toString();//System.console().format("[%s]", stack.toList().toString());
//        stack.pop();
//        str = stack.toList().toString();//System.console().format("[%s]", stack.toList().toString());
//        stack.update_push("4");
//        str = stack.toList().toString();//System.console().format("[%s]", stack.toList().toString());
//        stack.update_push("3");
//        str = stack.toList().toString();//System.console().format("[%s]", stack.toList().toString());
//    }
}
