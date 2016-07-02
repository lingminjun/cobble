package com.ssn.cobble.kit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingminjun on 16/5/16.
 */
public final class Stack<T extends Object> {
    private Object[] _stack;
    private int _idx;
    private int MAX_SIZE;

    public Stack(int size) {
        MAX_SIZE = size < 0 ? 10 : size;
        _stack = new Object[MAX_SIZE];
    }

    /**
     * 压入栈顶
     * @param obj
     * @return
     */
    public T push(T obj) {
        if (obj == null) {return null;}

        _stack[_idx%MAX_SIZE] = obj;//替换栈顶元素
        _idx = ((_idx + 1)%MAX_SIZE);
        return obj;
    }

    /**
     * 移除栈顶
     * @return
     */
    public T pop() {
        _idx = (_idx + MAX_SIZE - 1)%MAX_SIZE;
        T obj = (T)_stack[_idx%MAX_SIZE];//取出栈顶元素
        _stack[_idx%MAX_SIZE] = null;//
        return obj;
    }

    /**
     * 栈顶元素
     * @return
     */
    public T top() {
        int idx = (_idx + MAX_SIZE - 1)%MAX_SIZE;
        T obj = (T)_stack[idx%MAX_SIZE];//取出栈顶元素
        return obj;
    }

    /**
     * 表示栈满
     * @return
     */
    public boolean isFull() {
        return _stack[_idx%MAX_SIZE] != null;//表示是满栈
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
            T obj = (T)_stack[idx%MAX_SIZE];//取出栈顶元素

            if (obj == null) {break;}

            list.add(0,obj);//保持原有顺序
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
            T obj = (T)_stack[idx%MAX_SIZE];//取出栈顶元素

            if (obj == null) {break;}

            list.add(obj);//保持原有顺序
        }
        return list;
    }

    /**
     * 包含某个元素
     * @param obj
     * @return
     */
    public boolean contains(T obj) {
        if (obj == null) {return false;}

        //从栈顶开始取，直到取到为null为止
        for (int i = 1; i <= MAX_SIZE; i++) {
            int idx = (_idx + MAX_SIZE - i)%MAX_SIZE;
            Object o = _stack[idx%MAX_SIZE];//取出栈顶元素
            if (o == null) {break;}

            if (o.equals(obj)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 压入栈顶，并更新至栈顶
     * @param obj
     * @return
     */
    public T update_push(T obj) {
        if (obj == null) {return null;}

        for (int i = 1; i <= MAX_SIZE; i++) {
            int idx = (_idx + MAX_SIZE - i)%MAX_SIZE;
            Object o = _stack[idx%MAX_SIZE];//取出栈顶元素
            if (o == null) {break;}

            if (o.equals(obj)) {//开始移位排序
                for (int j = 1; j < i; j++) {
                    _stack[(idx + j - 1 + MAX_SIZE)%MAX_SIZE] = _stack[(idx + j)%MAX_SIZE];
                }
                _stack[(idx + i - 1)%MAX_SIZE] = o;//最后将数据提前
                return obj;
            }
        }

        //不包含则直接压入栈顶
        _stack[_idx%MAX_SIZE] = obj;//替换栈顶元素
        _idx = ((_idx + 1)%MAX_SIZE);
        return obj;
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
