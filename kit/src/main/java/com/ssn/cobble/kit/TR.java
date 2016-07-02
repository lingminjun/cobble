package com.ssn.cobble.kit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by lingminjun on 15/7/15.
 * 用于一些常用对象变量因为空指针而异常处理
 */
public class TR {
    private static final String EMPTY_STRING = "";

    /**
     * 取字符串
     * @param str
     * @return 返回非空字符串
     */
    public final static String string(String str) {return str != null ? str : EMPTY_STRING;}

    /**
     * 取字符串
     * @param str
     * @param def 默认值
     * @return 返回非空字符串
     */
    public final static String string(String str,String def) {return str != null ? str : (def != null ? def : EMPTY_STRING);}


    /**
     * 取字数字
     * @param v
     * @return 返回数字
     */
    public final static int integer(Integer v) {return v != null ? v : 0;}

    /**
     * 取字数字
     * @param v
     * @param def 默认值
     * @return 返回数字
     */
    public final static int integer(Integer v, int def) {return v != null ? v : def;}

    /**
     * 转int
     * @param v
     * @param def
     * @return
     */
    public final static int integer(String v, int def) {
        try {
            return Integer.parseInt(v);
        } catch (Throwable e) {}
        return def;
    }

    /**
     * 从bundle中获取整形，支持
     * @param bundle
     * @param name
     * @param def
     * @return
     */
    public final static int integer(Bundle bundle, String name, int def) {
        if (bundle == null || TextUtils.isEmpty(name) || !bundle.containsKey(name)) {return def;}
        Object obj = bundle.get(name);
        try {
            return Integer.parseInt(obj.toString());
        }catch (Exception e){
            return def;
        }
    }

    /**
     * 从intent中取字数字，兼容字符串方式，主要兼容h5传参
     * @param intent
     * @param name
     * @param def
     * @return
     */
    public final static int integer(Intent intent, String name, int def) {
        if (intent == null || TextUtils.isEmpty(name) || !intent.hasExtra(name)) {return def;}

        try {
            int v = intent.getIntExtra(name, (int) def);
            if (v != def) {return v;}

            String obj  = intent.getStringExtra(name);//兼容h5
            if (!TextUtils.isEmpty(obj)) {
                try {
                    return Integer.parseInt(obj);
                } catch (Throwable eee) {}
            }
        } catch (Throwable e) {}
        return def;
    }

    /**
     * 取字长整形数字
     * @param v
     * @return 返回数字
     */
    public final static long longNumber(Long v) {return v != null ? v : 0;}

    /**
     * 取字长整形数字
     * @param v
     * @param def 默认值
     * @return 返回数字
     */
    public final static long longNumber(Long v, long def) {return v != null ? v : def;}

    /**
     * 转long
     * @param v
     * @param def
     * @return
     */
    public final static long longNumber(String v, long def) {
        try {
            return Long.parseLong(v);
        } catch (Throwable e) {}
        return def;
    }

    /**
     * 从intent中取字数字，兼容字符串方式，主要兼容h5传参
     * @param bundle
     * @param name
     * @param def
     * @return
     */
    public final static long longNumber(Bundle bundle, String name, long def) {
        if (bundle == null || TextUtils.isEmpty(name) || !bundle.containsKey(name)) {return def;}
        Object obj = bundle.get(name);
        try {
            return Long.parseLong(obj.toString());
        }catch (Exception e){
            return def;
        }
    }

    /**
     * 从intent中取字数字，兼容字符串方式，主要兼容h5传参
     * @param intent
     * @param name
     * @param def
     * @return
     */
    public final static long longNumber(Intent intent, String name, long def) {
        if (intent == null || TextUtils.isEmpty(name) || !intent.hasExtra(name)) {return def;}

        try {
            return longNumber(intent.getExtras(),name,def);
        } catch (Throwable e) {}
        return def;
    }

    /**
     * 取字数字
     * @param v
     * @return 返回数字
     */
    public final static float floatNumber(Float v) {return v != null ? v : 0;}

    /**
     * 取字数字
     * @param v
     * @param def 默认值
     * @return 返回数字
     */
    public final static float floatNumber(Float v, float def) {return v != null ? v : def;}

    /**
     * 取字数字
     * @param v
     * @param def
     * @return
     */
    public final static float floatNumber(String v, float def) {
        try {
            return Float.parseFloat(v);
        } catch (Throwable e) {}
        return def;
    }

    /**
     * 取浮点数据
     * @param bundle
     * @param name
     * @param def
     * @return
     */
    public final static float floatNumber(Bundle bundle, String name, float def) {
        if (bundle == null || TextUtils.isEmpty(name) || !bundle.containsKey(name)) {return def;}
        Object obj = bundle.get(name);
        try {
            return Float.parseFloat(obj.toString());
        }catch (Exception e){
            return def;
        }
    }

    /**
     * 从intent中取字数字
     * @param intent
     * @param name
     * @param def
     * @return
     */
    public final static float floatNumber(Intent intent, String name, float def) {
        if (intent == null || TextUtils.isEmpty(name) || !intent.hasExtra(name)) {return def;}

        try {
            return floatNumber(intent.getExtras(),name,def);
        } catch (Throwable e) {}
        return def;
    }

    /**
     * 取字数字
     * @param v
     * @return 返回数字
     */
    public final static double doubleNumber(Double v) {return v != null ? v : 0;}

    /**
     * 取字数字
     * @param v
     * @param def 默认值
     * @return 返回数字
     */
    public final static double doubleNumber(Double v, double def) {return v != null ? v : def;}

    /**
     * 取字数字
     * @param v
     * @param def
     * @return
     */
    public final static double doubleNumber(String v, float def) {
        try {
            return Double.parseDouble(v);
        } catch (Throwable e) {}
        return def;
    }

    /**
     * 取浮点数据
     * @param bundle
     * @param name
     * @param def
     * @return
     */
    public final static double doubleNumber(Bundle bundle, String name, double def) {
        if (bundle == null || TextUtils.isEmpty(name) || !bundle.containsKey(name)) {return def;}
        Object obj = bundle.get(name);
        try {
            return Double.parseDouble(obj.toString());
        }catch (Exception e){
            return def;
        }
    }

    /**
     * 从intent中取字数字
     * @param intent
     * @param name
     * @param def
     * @return
     */
    public final static double doubleNumber(Intent intent, String name, double def) {
        if (intent == null || TextUtils.isEmpty(name) || !intent.hasExtra(name)) {return def;}

        try {
            return doubleNumber(intent.getExtras(),name,def);
        } catch (Throwable e) {}
        return def;
    }

    /**
     * 取字数字
     * @param v
     * @return 返回数字
     */
    public final static boolean bool(Boolean v) {return v != null ? v : false;}

    /**
     * 取字符串中的bool值
     * @param v
     * @return
     */
    public final static boolean bool(String v) {
        return bool(v,false);
    }

    /**
     * 取字符串中的bool值，取不到时返回默认值
     * @param v
     * @param def
     * @return
     */
    public final static boolean bool(String v, boolean def) {
        if (TextUtils.isEmpty(v)) {return def;}
        if ("1".equalsIgnoreCase(v)
                || "yes".equalsIgnoreCase(v)
                || "true".equalsIgnoreCase(v)
                || "on".equalsIgnoreCase(v)) {
            return true;
        }
        return def;
    }

    /**
     * 从intent中取字数字，兼容字符串方式，主要兼容h5传参
     * @param intent
     * @param name
     * @param def
     * @return
     */
    public final static boolean bool(Intent intent, String name, boolean def) {
        if (intent == null || TextUtils.isEmpty(name) || !intent.hasExtra(name)) {return def;}

        try {
            boolean v = intent.getBooleanExtra(name, def);
            if (v != def) {
                return v;
            }

            String str = intent.getStringExtra(name);
            if (!TextUtils.isEmpty(str)) {
                try {
                    return bool(str, def);//做字符串兼容
                } catch (Throwable ee) {}
            }
        } catch (Throwable e) {}
        return def;
    }


    /**
     * 从intent中取字数字，兼容字符串方式，主要兼容h5传参
     * @param bundle
     * @param name
     * @param def
     * @return
     */
    public final static boolean bool(Bundle bundle, String name, boolean def) {
        if (bundle == null || TextUtils.isEmpty(name) || !bundle.containsKey(name)) {return def;}
        Object obj = bundle.get(name);
        try {
            return bool(obj.toString(),def);
        }catch (Exception e){
            return def;
        }
    }
}
