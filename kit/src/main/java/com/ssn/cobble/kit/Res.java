package com.ssn.cobble.kit;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

/**
 * Created by lingminjun on 15/7/11.
 * 为应用一些资源访问提供便利的接口
 */
public class Res {

    private static Application _application;
    public static boolean initialization(Application application) {
        if (_application != null) {
            Log.w("Res", "Res 初始化方法 务必在Application onCreate方法中调用，调用一次后失效！！！");
            return false;
        }

        if (application == null) {
            Log.w("Res", "Res 初始化方法 务必在Application onCreate方法中调用，调用一次后失效！！！");
            return false;
        }

        _application = application;

        return true;
    }

    /**
     * 获取文案（本地化文案）
     * @param id
     * @return
     */
    public static String localized(int id) {return application().getString(id);}
    public static String localized(int resId, Object... formatArgs){
        return application().getString(resId, formatArgs);
    }

    /**
     * 获取颜色，主要用于控件颜色赋值
     * @param id
     * @return
     */
    public static int color(int id) {
        return application().getResources().getColor(id);
    }

    /**
     * 获取颜色，主要用于控件颜色赋值
     * @param id
     * @return
     */
    public static ColorStateList colorState(int id) {
        return application().getResources().getColorStateList(id);
    }

    /**
     * 构造颜色，主要用于控件颜色赋值
     * @param normal
     * @param pressed
     * @param unable
     * @return
     */
    public static ColorStateList colorState(int normal, int pressed, int unable) {
        return colorState(normal, pressed, pressed, unable);
    }

    /**
     * 构造颜色，主要用于控件颜色赋值
     * @param normal
     * @param pressed
     * @param focused
     * @param unable
     * @return
     */
    public static ColorStateList colorState(int normal, int pressed, int focused, int unable) {
        int[] colors = new int[] { pressed, focused, normal, focused, unable, normal };
        int[][] states = new int[6][];
        states[0] = new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled };
        states[1] = new int[] { android.R.attr.state_enabled, android.R.attr.state_focused };
        states[2] = new int[] { android.R.attr.state_enabled };
        states[3] = new int[] { android.R.attr.state_focused };
        states[4] = new int[] { android.R.attr.state_window_focused };
        states[5] = new int[] {};
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    /**
     * 获取图片
     * @param id
     * @return
     */
    public static Drawable image(int id) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            return application().getResources().getDrawable(id, null);
        } else {
            return application().getResources().getDrawable(id);
        }
    }

    /**
     * 获取图片bitmap
     * @param imageId
     * @return
     */
    public static Bitmap bitmap(int imageId) {
        return BitmapFactory.decodeResource(Res.resources(), imageId);
    }

    /**
     * 按钮状态背景列表
     * btn.setBackgroundDrawable(imageState(R.drawable.btn_normal, R.drawable.btn_selected));
     * @param idNormal
     * @param idPressed
     * @param idUnable
     * @return
     */
    public static StateListDrawable imageState(int idNormal, int idPressed,int idUnable) {
        return imageState(idNormal, idPressed, idPressed, idUnable);
    }

    /**
     * 按钮状态背景列表
     * btn.setBackgroundDrawable(imageState(R.drawable.btn_normal, R.drawable.btn_selected));
     * @param idNormal
     * @param idPressed
     * @param idFocused
     * @param idUnable
     * @return
     */
    public static StateListDrawable imageState(int idNormal, int idPressed, int idFocused, int idUnable) {
        Context context = Res.context();
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
        Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
        Drawable focused = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
        Drawable unable = idUnable == -1 ? null : context.getResources().getDrawable(idUnable);

        // View.PRESSED_ENABLED_STATE_SET
        bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed);
        // View.ENABLED_FOCUSED_STATE_SET
        bg.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_focused }, focused);
        // View.ENABLED_STATE_SET
        bg.addState(new int[] { android.R.attr.state_enabled }, normal);
        // View.FOCUSED_STATE_SET
        bg.addState(new int[] { android.R.attr.state_focused }, focused);
        // View.WINDOW_FOCUSED_STATE_SET
        bg.addState(new int[] { android.R.attr.state_window_focused }, unable);
        // View.EMPTY_STATE_SET
        bg.addState(new int[] {}, normal);
        return bg;
    }

    /**
     * 获取资源包
     * @return
     */
    public static Resources resources() {
        return application().getResources();
    }

    /**
     * 获取上下文
     * @return
     */
    public static Context context() {
        return application();
    }

    /**
     * 取 version name
     * @return
     */
    public static final String appVersion() {
        PackageManager pm = application().getPackageManager();
        PackageInfo pi = null;
        String version = null;
        try {
            pi = pm.getPackageInfo(application().getPackageName(), PackageManager.GET_INSTRUMENTATION);
            version = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return TR.string(version);
    }

    /**
     * 取 version name，1.2.311 ==> 001 002 311
     * @return
     */
    private static int _int_version = 0;
    @Deprecated
    public static final int appIntVersion() {
        if (_int_version > 0) {return _int_version;}

        String string = appVersion();

        String[] sections = string.split("\\.");
        int len = sections.length;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3 && i < len; i++) {
            String str = sections[i];
            int str_len = str.length();
            builder.append(str);
            for (int j = 0; j < 3-str_len; j++) {
                builder.append("0");
            }
        }

        try {
            _int_version = Integer.parseInt(builder.toString());
        } catch (Throwable e) {}
        return _int_version;

    }

    /**
     * 取 build 号，等同于version code
     * @return
     */
    public static final int buildNumber() {
        PackageManager pm = application().getPackageManager();
        PackageInfo pi = null;
        int number = 0;
        try {
            pi = pm.getPackageInfo(application().getPackageName(), PackageManager.GET_INSTRUMENTATION);
            number = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return number;
    }

    /**
     * 取包名
     * @return
     */
    public static final String packageName() {
        return application().getPackageName();
    }

    /**
     * 获取预信息获取，配置在Manifest的Application meta-data中的数据
     * @param key
     * @return
     */
    public static final String metaData(final String key) {
        if (TextUtils.isEmpty(key)) {
            return TR.string(null);
        }
        try {
            ApplicationInfo appInfo = application().getPackageManager().getApplicationInfo(packageName(), PackageManager.GET_META_DATA);

            Object obj = appInfo.metaData.get(key);
            if (obj == null) {
                return TR.string(null);
            }
            if (obj instanceof String) {
                return (String)obj;
            }
            else {
                return obj.toString();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return TR.string(null);
    }

    private static Application application() {
        if (_application == null) {
            Log.e("Res", "必须在 application 启动中初始化Res");
            System.exit(-1);
        }
        return _application;
    }


    /**
     * 删除此时间点以前的缓存文件
     * @param outdated (毫秒)
     */
    public static void clearApplicationCache(long outdated) {
        File dir = Res.context().getCacheDir();
        clearFolder(dir, outdated);
    }

    // clear the cache before time numDays
    private static void clearFolder(File dir, long numDays) {
        if (dir!= null && dir.isDirectory()) {
            try {
                for (File child:dir.listFiles()) {
                    if (child.isDirectory()) {
                        clearFolder(child, numDays);
                    }
                    if (child.lastModified() < numDays) {
                        child.delete();
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 清除WebView缓存
     */
    public static void clearWebViewCache(){

        //清理Webview缓存数据库
        Context context = Res.context();
        try {
            context.deleteDatabase("webview.db");
            context.deleteDatabase("webviewCache.db");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //WebView 缓存文件
        File appCacheDir = new File(context.getFilesDir().getAbsolutePath()+"/webcache");
        Log.e("res", "appCacheDir path=" + appCacheDir.getAbsolutePath());

        File webviewCacheDir = new File(context.getCacheDir().getAbsolutePath()+"/webviewCache");
        Log.e("res", "webviewCacheDir path=" + webviewCacheDir.getAbsolutePath());

        //删除webview 缓存目录
        if(webviewCacheDir.exists()){
            context.deleteFile(webviewCacheDir.getAbsolutePath());
        }

        //删除webview 缓存 缓存目录
        if(appCacheDir.exists()){
            context.deleteFile(appCacheDir.getAbsolutePath());
        }
    }

    /**
     * make true current connect service is wifi
     * @return
     */
    public static boolean isWIFI() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }


    /**
     * 从RGB数组，
     * @param object int[],string,JSONArray
     * @return
     */
    public static int colorFromRGB(Object object) {
        if (object == null) {
            return 0;
        }

        if (object instanceof String) {
            String string = (String)object;
            if (string.startsWith("#")) {}
            else if (string.startsWith("0x")) {
                string = string.substring(2);
                string = "#"+string;
            }
            else {
                string = "#"+string;
            }
            try {
                return Color.parseColor(string);
            }
            catch (Throwable e) {}
            return 0;
        }
        else if (object instanceof Number) {
            return ((Number) object).intValue();
        }
        else if (object instanceof JSONArray) {
            JSONArray ary = (JSONArray)object;
            if (ary.length() == 3) {
                try {
                    return Color.rgb(ary.getInt(0), ary.getInt(1), ary.getInt(2));
                } catch (JSONException e) {
                    return 0;
                }
            }
            else if (ary.length() == 4) {
                try {
                    float alpha = (float)ary.getDouble(3);
                    int a = (int)(255 * alpha);
                    return Color.argb(a, ary.getInt(0), ary.getInt(1), ary.getInt(2));
                } catch (JSONException e) {
                    return 0;
                }
            }
            else {
                return 0;
            }
        } else if (object instanceof int[]) {
            int[] ary = (int[])object;
            if (ary.length == 3) {
                return Color.rgb(ary[0], ary[1], ary[2]);
            }
            else if (ary.length == 4) {
                float alpha = (float)ary[3];
                int a = (int)(255 * alpha);
                return Color.argb(a, ary[0], ary[1], ary[2]);
            }
            else {
                return 0;
            }
        }
        else {
            return 0;
        }
    }

    public static void refreshSystemImages(String dirPath) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            MediaScannerConnection.scanFile(Res.context(), new String[]{dirPath}, new String[]{"image/*"}, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Res.context().sendBroadcast(new Intent(android.hardware.Camera.ACTION_NEW_PICTURE, uri));
                    Res.context().sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
                }
            });

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri uri = Uri.fromFile(new File(dirPath));
            intent.setData(uri);
            Res.context().sendBroadcast(intent);

//            scanPhotos(outFile.getAbsolutePath(), Res.context()); // 实际起作用的方法
        } else {
            Res.context().sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

    /**
     * 获取设备imei，需要电话权限，不建议使用
     * {@link android.Manifest.permission#READ_PHONE_STATE READ_PHONE_STATE}
     * @return
     */
    public static String getImei(){
        try {
            TelephonyManager tm = (TelephonyManager) context().getSystemService(Activity.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        } catch (Throwable e) {
        }
        return "";
    }

//    /**
//     * 获取设备mac地址
//     * @return
//     */
//    public static String getMacAddress()
//    {
//        return getLocalMacAddressFromIp();
//    }
//
//    private static String getLocalMacAddressFromIp() {
//        String mac_s = "";
//        try {
//            byte[] mac;
//            String ip = getLocalIpAddress();
//            if (!InetAddressUtils.isIPv4Address(ip)) {
//                return mac_s;
//            }
//            InetAddress ipAddress = InetAddress.getByName(ip);
//            if (ipAddress == null) {
//                return mac_s;
//            }
//            NetworkInterface ne = NetworkInterface.getByInetAddress(ipAddress);
//            mac = ne.getHardwareAddress();
//            if (mac.length > 0) {
//                mac_s = byte2mac(mac);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally{
//            return mac_s;
//        }
//    }
//
//    public static String getLocalIpAddress() {
//        try {
//            String ipv4 = null;
//            List<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
//            for (NetworkInterface ni : nilist) {
//                List<InetAddress> ialist = Collections.list(ni.getInetAddresses());
//                for (InetAddress address : ialist) {
//                    ipv4 = address.getHostAddress();
//                    if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4)) {
//                        return ipv4;
//                    }
//                }
//            }
//        } catch (SocketException ex) {
//
//        }
//        return "0.0.0.0";
//    }

    private static String byte2mac(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1) {
                hs = hs.append("0").append(stmp);
            } else {
                hs = hs.append(stmp);
            }
        }
        StringBuffer str = new StringBuffer(hs);
        for (int i = 0; i < str.length(); i++) {
            if (i % 3 == 0) {
                str.insert(i, ':');
            }
        }
        return str.toString().substring(1);
    }

    public static Drawable getDrawable(int id, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return resources().getDrawable(id, context.getTheme());
        } else {
            return resources().getDrawable(id);
        }
    }

    //TODO 要防止大图 TransferDrawable
    public static Bitmap getBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        // canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    private static String cpu_abi = null;
    public static String cpu_abi() {
        if (cpu_abi != null) {
            return cpu_abi;
        }
        try {
            cpu_abi = System.getProperty("os.arch");
        } catch (Throwable e) {}
        if (cpu_abi == null) {
            cpu_abi = "";
        }
        return cpu_abi;
    }


    public static Drawable getRoundDrawable(int color) {

        float[] outerR = new float[] { 200, 200, 200, 200, 200, 200, 200, 200 };
        // 内部矩形与外部矩形的距离
//        RectF inset = new RectF(100, 100, 50, 50);
        // 内部矩形弧度
//        float[] innerRadii = new float[] { 20, 20, 20, 20, 20, 20, 20, 20 };

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        //指定填充颜色
        drawable.getPaint().setColor(color);
        // 指定填充模式
        drawable.getPaint().setStyle(Paint.Style.FILL);

        return drawable;
    }

    public static Drawable getRoundDrawable(int color, int rate) {

        float[] outerR = new float[] { rate, rate, rate, rate, rate, rate, rate, rate };
        // 内部矩形与外部矩形的距离
//        RectF inset = new RectF(100, 100, 50, 50);
        // 内部矩形弧度
//        float[] innerRadii = new float[] { 20, 20, 20, 20, 20, 20, 20, 20 };

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        //指定填充颜色
        drawable.getPaint().setColor(color);
        // 指定填充模式
        drawable.getPaint().setStyle(Paint.Style.FILL);

        return drawable;
    }
}
