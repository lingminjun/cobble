package com.ssn.cobble.kit;

import android.text.TextUtils;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by lingminjun on 15/11/6.
 * 负责文件存储
 */
public final class Store {

    private static RigidFactory<Store> _factory = null;

    private static RigidFactory<Store> newFactory() {
        RigidFactory.SingletonCreator<Store> creator = new RigidFactory.SingletonCreator() {
            @Override
            public Store onCreate(String key, Map params) {
                return new Store(key);//构建实例
            }
        };
        return new RigidFactory<Store>(creator);
    }

    /**
     * Store工程
     *
     * @return
     */
    public static RigidFactory<Store> factory() {
        if (_factory != null) {
            return _factory;
        }
        synchronized (Store.class) {
            if (_factory == null) {
                _factory = newFactory();
            }
        }
        return _factory;
    }

    /**
     * 返回缓存目录
     *
     * @return
     */
    public static Store caches() {
        return factory().get("caches");
    }

    /**
     * 返回临时目录
     *
     * @return
     */
    public static Store temporary() {
        return factory().get("temp");
    }

    /**
     * 返回文件目录
     *
     * @return
     */
    public static Store documents() {
        return factory().get("documents");
    }

    /**
     * 返回库目录
     *
     * @return
     */
    public static Store library() {
        return factory().get("library");
    }

    private final static String STORE_FINDER = "/store/";
    private final static String STORE_TAIL = ".tail";
    private final static String STORE_READ_ONLY = "r";
    private final static String STORE_READ_WRITE = "rw";


    private String finder;//目录，不能包含“\ / : * ?”字符


    public Store(String finder) {
        this.finder = new StringBuilder(Res.context().getFilesDir().toString())
                .append(STORE_FINDER)
                .append(finder)
                .append("/").toString();
    }

    public Store(String path, String finder) {
        this.finder = new StringBuilder(path)
                .append(STORE_FINDER)
                .append(finder)
                .append("/").toString();
    }

    /**
     * 存储数据
     *
     * @param key  存储key
     * @param data 存储内容
     */
    public void store(String key, byte[] data) {
        store(key, data, 0);
    }

    /**
     * 存储数据
     *
     * @param key    存储的key
     * @param data   存储内容
     * @param expire 过期时间(毫秒)，小于等于零表示不过期
     * @throws java.io.IOException
     */
    public void store(String key, byte[] data, int expire) {
        if (data == null) {
            return;
        }

        String path = path(key);
        if (TextUtils.isEmpty(path)) {
            return;
        }

        try {
            _store(path, data, expire);
        } catch (Throwable e) {
        }
    }

    /**
     * 获取数据，更新时效，若数据过期，不返回数据，并删除过期文件
     *
     * @param key
     * @return
     */
    public byte[] data(String key) {
        return accessData(key, true);
    }

    /**
     * 获取数据，不更新时效，若数据过期，仍然返回数据，并删除过期文件
     *
     * @param key
     * @return
     */
    public byte[] accessData(String key) {
        return accessData(key, false);
    }

    public void deleteData(String key){
        _deleteData(key);
    }

    /**
     * 清除目录下的文件
     */
    public void clearData() {
        _clearData();
    }


    ////////////////////////////////////////////////////////////

    /*********************
     * 私有实现
     ******************************/
    ////////////////////////////////////////////////////////////
    private byte[] accessData(String key, boolean checkExpire) {

        String path = path(key);
        if (TextUtils.isEmpty(path)) {
            return null;
        }

        byte[] data = null;
        try {
            data = _accessData(path, checkExpire);
        } catch (Throwable e) {
        }

        return data;
    }

    private void _deleteData(String key){
        String path = path(key);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        String tail = path + STORE_TAIL;
        File fileExpireAble = new File(tail);
        File file = new File(path);
        if (file.exists()){
            file.delete();
        }
        if (fileExpireAble.exists()){
            fileExpireAble.delete();
        }
    }

    /**
     * 清除该文件夹的所有内容
     */
    private void _clearData() {
        deleteDirOrFile(finder);
    }

    private void deleteDirOrFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {//文件夹
            File[] list = file.listFiles();
            if (list == null) {
                return;
            }
            for (File fileInner : list) {
                deleteDirOrFile(fileInner.getAbsolutePath());
            }
            file.delete();//递归完成之后，把自己删除
        } else {//普通文件
            file.delete();
        }
    }

    private void _store(String path, byte[] data, int expire) throws Exception {

        File file = new File(path);
        if (file.exists()) { //删除已存在文件
            file.delete();
        }

        RandomAccessFile out = new RandomAccessFile(path, STORE_READ_WRITE);
        out.write(data);
        out.close();

        if (expire > 0) {
            String tail = path + STORE_TAIL;
            RandomAccessFile exp = new RandomAccessFile(tail, STORE_READ_WRITE);
            exp.writeLong(now());//存储访问时间
            exp.writeLong(expire);//过期时长
            exp.close();
        }
    }

    private byte[] _accessData(String path, boolean checkExpire) throws Exception {
        byte[] data = null;

        boolean isExpire = false;

        if (checkExpire) {

            String tail = path + STORE_TAIL;
            File file = new File(tail);

            if (file.exists()) {//存在tail文件
                long now = now();
                RandomAccessFile exp = new RandomAccessFile(tail, STORE_READ_WRITE);
                long latest = exp.readLong();//存储上次访问时间
                long expire = exp.readLong();//过期时长

                if (now >= latest + expire) {//过期，删除文件
                    exp.close();
                    isExpire = true;

                    //删除tail
                    file.delete();
                } else {
                    exp.seek(0);//回到其实位置
                    exp.writeLong(now);
                    exp.close();
                }
            }
        }

        File file = new File(path);

        if (file.exists()) {//文件存在再进行操作

            RandomAccessFile in = new RandomAccessFile(path, STORE_READ_ONLY);
            long len = in.length();
            data = new byte[(int) len];
            in.read(data);
            in.close();

            //删除原始数据
            if (isExpire) {//删除文件
                file.delete();

                if (checkExpire) {//过期不再返回数据
                    data = null;
                }
            }
        }

        return data;
    }

    private String path(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        String md5 = md5(key);
        if (TextUtils.isEmpty(md5)) {
            return null;
        }

        String sub = md5.substring(0, 2);

        String finder = this.finder + sub;
        ;

        File file = new File(finder);
        if (!file.exists()) {//判断文件夹是否存在,如果不存在则创建文件夹
            file.mkdirs();
        }

        return finder + "/" + md5;
    }


    private static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xff) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xff));
        }
        return hex.toString();
    }


    private static long now() {
        return System.currentTimeMillis();
    }
}
