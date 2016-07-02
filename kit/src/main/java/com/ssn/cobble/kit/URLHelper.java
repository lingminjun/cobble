package com.ssn.cobble.kit;

import android.content.res.AssetManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * url 的一些通用处理
 */
public class URLHelper {
    private static final String ALLOWED_URI_CHARS = "";//"!*'();:@&=+$,/?%#[]";


    /**
     * url保留字encode，发起网络请求前确保做一次encode
     * 图片下载尽量做一次
     * @param url
     * @return
     */
    public static String encodeURL(final String url) {
        if (TextUtils.isEmpty(url)) {return "";}

        //url编码问题修正
        return Uri.encode(url, "!*'();:@&=+$,/?%#[]");//!*'();:@&=+$,/?%#[]
        //不需要反解析
//        return Uri.encode(Uri.decode(url), "!*'();:@&=+$,/?%#[]");//!*'();:@&=+$,/?%#[]
    }

    /**
     * 将params key value编码，
     * 其中Object 类型限制余下:
     * T = [String,short,int,long,float,double,boolean,char]
     * 支持类型为T+List<T>+T[]
     * @param params，key将根据字符串排序执行
     * @return URL Query string , values url encode(UTF-8)
     */
    public static String URLQueryString(HashMap<String,Object> params) {

        if (params == null) {
            return "";
        }

        if (params.size() == 0) {
            return "";
        }

        StringBuilder queryBuilder = new StringBuilder();
        try {
            List<String> keys = new ArrayList<String>(params.keySet());
            Collections.sort(keys);

            for (String key : keys) {
                Object obj = params.get(key);
                if (obj.getClass().isArray()) {//数组支持
                    int len = Array.getLength(obj);
                    for (int i = 0; i < len; i++) {
                        Object v = Array.get(obj, i);
                        appendQuery(queryBuilder,key,v);//添加到builder
                    }
                } else if (obj instanceof List) {//列表支持
                    List list = (List)obj;
                    for (Object value : list) {
                        appendQuery(queryBuilder,key,value); //添加到builder
                    }
                } else {//其他数据
                    appendQuery(queryBuilder,key,obj); //添加到builder
                }
            }
        } catch (Throwable e) {
            Log.e("URLHelper", e.toString());
        }

        return queryBuilder.toString();
    }

    /**
     * 过滤非法字符
     * @param stream
     * @param characters 需要被过滤的字符
     * @return
     */
    public static String trim(String stream, String characters) {//

        // null或者空字符串的时候不处理
        if (stream == null || stream.length() == 0 || characters == null || characters.length() == 0) {
            return stream;
        }

        int begin = 0;
        int end = stream.length();

        while (begin < end) {
            if (characters.contains(stream.substring(begin,begin+1))) {
                begin++;
            }
            else {
                break;
            }
        }

        String target = stream.substring(begin);
        if (target.length() <= 1) {
            return target;
        }

        end = target.length() - 1;
        while (end > 0) {
            if (characters.contains(target.substring(end,end+1))) {
                end--;
            }
            else {
                break;
            }
        }

        return target.substring(0, end + 1);
    }


    /**
     * 取URL query 中的参数
     * @param queryString 只要是key=value&key1=value2方式
     * @return Object只有可能是String或者List<String>
     */
    public static HashMap<String,Object> URLParams(String queryString, boolean decode) {

        if (queryString == null || TextUtils.isEmpty(queryString)) {
            return new HashMap<String, Object>();
        }

        HashMap<String, Object> params = new HashMap<String, Object>();

        String string = trim(queryString,"?#;!&");
        try {

            String[] comps = string.split("&");
            for (int i = 0; i < comps.length; i++) {
                String str = comps[i];
                if (TextUtils.isEmpty(str)) {
                    continue;
                }

                Object obj = params.get(str);

                if (!str.contains("=")) {
                    if (obj == null) {//非空的情况才能加入
                        params.put(str,"");
                    }
                }
                else {
                    String[] innr = str.split("=",2);
                    String key = innr[0];
                    String value = innr[1];//必然有元素

                    if (TextUtils.isEmpty(key)) {//key本身是非法的，直接不要了
                        continue;
                    }

                    if (decode) {
                        value = Uri.decode(value);//RFC-2396标准
//                        value = URLDecoder.decode(value,"UTF-8");//RFC-1738标准
                    }

                    if (obj == null) {
                        params.put(key,value);
                    }
                    else {
                        if (obj instanceof List) {
                            ((List<String>)obj).add(value);
                        }
                        else {
                            List<String> vls = new ArrayList<String>();
                            vls.add((String)obj);
                            vls.add(value);
                            params.put(key,vls);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            Log.e("URLHelper", e.toString());
        }

        return params;
    }

    /**
     * 取URL “<scheme>://<net_loc>/<path>;<params>” 内容，既#<fragment>以前的部分
     * @param url 只要是key=value&key1=value2方式
     * @return
     */
    public static String URLURI(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }

        Uri uri = Uri.parse(url);
        if (uri == null) {
            return "";
        }

        Uri.Builder builder = new Uri.Builder();
        String str = uri.getScheme();
        if (str != null) {
            builder.scheme(str);
        }

        str = uri.getEncodedAuthority();
        if (str != null) {
            builder.encodedAuthority(str);
        }

        str = uri.getHost();
        if (str != null) {
            int port = uri.getPort();
            if (port > 0) {
                str = str+":"+ Integer.toString(port);
            }

            builder.encodedOpaquePart(str);
        }

        str = uri.getEncodedPath();
        if (str != null) {
            builder.encodedPath(str);
        }

        return builder.build().toString();
    }

    /**
     * 取URL “<scheme>://<net_loc>/<path>;<params>?<query>” 内容，既#<fragment>以前的部分
     * @param url 只要是key=value&key1=value2方式
     * @return
     */
    public static String URLSource(String url) {
        if (!url.contains("#")) {
            return url;
        }

        int pos = url.indexOf("#");

        return url.substring(0, pos);
    }

    /**
     * url host
     * @return
     */
    public static String URLHost(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }

        Uri uri = Uri.parse(url);
        if (uri == null) {
            return "";
        }

        return TR.string(uri.getHost());
    }

    /**
     * 解析url中的query 参数 value url decode
     * @param url，注意此方法url必须是已经encode方法，否则可能出现解析参数出错
     * @return
     */
    public static HashMap<String,Object> URLQuery(String url) {
        if (url == null) {
            return new HashMap<String, Object>();
        }

        Uri uri = Uri.parse(url);
        if (uri == null) {
            return new HashMap<String, Object>();
        }

//        HashMap<String, Object> params = null;// new HashMap<String, Object>();

        String queryString = uri.getEncodedQuery();
        return URLHelper.URLParams(queryString, true);
    }

    /**
     * 重置 url 的query
     * @param originURL 原始url
     * @param query query参数
     * @return
     */
    public static String URLResetQuery(String originURL, HashMap<String,Object> query) {
        return URLHelper.URLResetQuery(originURL,query,null);
    }

    /**
     * 重置 url 的query
     * @param originURL 原始url
     * @param query query参数，切记，参数采用明文传入，内部做encode
     * @param fragments 分段信息（采用&分割，切记，参数采用明文传入，内部做encode），可为空
     * @return
     */
    public static String URLResetQuery(String originURL, HashMap<String,Object> query, HashMap<String,Object> fragments) {
        if (TextUtils.isEmpty(originURL)) {
            return "";
        }

        Uri uri = Uri.parse(originURL);
        if (uri == null) {
            return "";
        }

        Uri.Builder builder = new Uri.Builder();
        String str = uri.getScheme();
        if (str != null) {
            builder.scheme(str);
        }

        str = uri.getEncodedAuthority();
        if (str != null) {
            builder.encodedAuthority(str);
        }

        str = uri.getHost();
        if (str != null) {
            int port = uri.getPort();
            if (port > 0) {
                str = str+":"+ Integer.toString(port);
            }

            builder.encodedOpaquePart(str);
        }

        str = uri.getEncodedPath();
        if (str != null) {
            builder.encodedPath(str);
        }

        if (query != null) {
            str = URLHelper.URLQueryString(query);
        }
        else {
            str = uri.getEncodedQuery();
        }
        if (str != null) {
            builder.encodedQuery(str);
        }

        if (fragments != null) {
            str = URLHelper.URLQueryString(fragments);
        } else {
            str = uri.getEncodedFragment();
        }
        if (str != null && !TextUtils.isEmpty(str)) {
            if (!str.startsWith("!&")) {//新的协议支持
                str = "!&" + str;
            }
            builder.encodedFragment(str);
        }

        return builder.build().toString();
    }

    /**
     * 重置uri的分段字符串，URI不包涵 # 后面的参数
     * @param originURL
     * @param query
     * @return
     */
    public static String URIResetQuery(String originURL, HashMap<String,Object> query) {
        if (TextUtils.isEmpty(originURL)) {
            return "";
        }

        Uri uri = Uri.parse(originURL);
        if (uri == null) {
            return "";
        }

        Uri.Builder builder = new Uri.Builder();
        String str = uri.getScheme();
        if (str != null) {
            builder.scheme(str);
        }

        str = uri.getEncodedAuthority();
        if (str != null) {
            builder.encodedAuthority(str);
        }

        str = uri.getHost();
        if (str != null) {
            int port = uri.getPort();
            if (port > 0) {
                str = str+":"+ Integer.toString(port);
            }

            builder.encodedOpaquePart(str);
        }

        str = uri.getEncodedPath();
        if (str != null) {
            builder.encodedPath(str);
        }

        if (query != null) {
            str = URLHelper.URLQueryString(query);
        }
        else {
            str = uri.getEncodedQuery();
        }
        if (str != null) {
            builder.encodedQuery(str);
        }

        return builder.build().toString();
    }

    /**
     * 重写URL
     * @param originURL
     * @param scheme
     * @param host
     * @param port
     * @return
     */
    public static String URLReset(String originURL, String scheme, String host, int port) {
        if (TextUtils.isEmpty(originURL)) {
            return "";
        }

        Uri uri = Uri.parse(originURL);
        if (uri == null) {
            return "";
        }

        Uri.Builder builder = new Uri.Builder();
        String str = scheme != null ? scheme : uri.getScheme();
        if (str != null) {
            builder.scheme(str);
        }

        str = uri.getEncodedAuthority();
        if (str != null) {
            builder.encodedAuthority(str);
        }

        if (host != null) {
            str = host;
            if (port > 0) {
                str = str+":"+ Integer.toString(port);
            }

            builder.encodedOpaquePart(str);
        }
        else {
            str = uri.getHost();
            if (str != null) {
                int aport = uri.getPort();
                if (aport > 0) {
                    str = str+":"+ Integer.toString(aport);
                }
                builder.encodedOpaquePart(str);
            }
        }

        str = uri.getEncodedPath();
        if (str != null) {
            builder.encodedPath(str);
        }

        str = uri.getEncodedQuery();
        if (str != null) {
            builder.encodedQuery(str);
        }

        str = uri.getEncodedFragment();
        if (str != null) {
            if (!str.startsWith("!&")) {//新的协议支持
                str = "!&" + str;
            }
            builder.encodedFragment(str);
        }

        return builder.build().toString();
    }

    /**
     * 解析url中的fragment 参数 value url decode
     * @param url
     * @param decode
     * @return Object contains String, List<String>
     */
    public static HashMap<String,Object> URLFragment(String url,boolean decode) {
        if (url == null) {
            return new HashMap<String, Object>();
        }

        Uri uri = Uri.parse(url);
        if (uri == null) {
            return new HashMap<String, Object>();
        }

        String fragment = uri.getEncodedFragment();//除非没有decode的fragment操作

        return URLParams(fragment,decode);
    }

    /**
     * url是否相等，忽略分段，path支持大小写区分
     * @param urlOne
     * @param urlTwo
     * @return
     */
    public static boolean equalsURL(String urlOne, String urlTwo, boolean ignoreCase) {
        boolean isEqual = true;

        Uri uriOne = Uri.parse(urlOne);
        Uri uriTwo = Uri.parse(urlTwo);

        if (!uriOne.getScheme().equalsIgnoreCase(uriTwo.getScheme())) {
            isEqual = false;
            return isEqual;
        }
        if (!uriOne.getHost().equalsIgnoreCase(uriTwo.getHost())) {
            isEqual = false;
            return isEqual;
        }
        // 部分服务器区分大小写
        // Mac OS X 默认的文件系统（HFS case-insensitive) 是不分大小写的、
        // Windows 上的 NTFS 也是。
        // 而 Linux 系统常用的 ext3/4 则是需要区分大小写的。
        if (ignoreCase) {
            if (!uriOne.getPath().equalsIgnoreCase(uriTwo.getPath())) {
                isEqual = false;
                return isEqual;
            }
        } else {
            if (!uriOne.getPath().equals(uriTwo.getPath())) {
                isEqual = false;
                return isEqual;
            }
        }

        if (uriOne.getPort() != (uriTwo.getPort())) {
            isEqual = false;
            return isEqual;
        }

        Set<String> keysOne = uriOne.getQueryParameterNames();
        Set<String> keysTwo = uriTwo.getQueryParameterNames();
        if (keysOne == null && keysTwo == null) {
            return isEqual;
        } else if ((keysOne != null && keysTwo == null) || (keysOne == null && keysTwo != null)) {
            return false;
        } else if (keysOne.equals(keysTwo)) {//比较内容
            for (String key : keysOne) {
                try {
                    List<String> valueOne = new ArrayList<String>(uriOne.getQueryParameters(key));
                    List<String> valueTwo = new ArrayList<String>(uriTwo.getQueryParameters(key));
                    Collections.sort(valueOne);
                    Collections.sort(valueTwo);
                    if (!valueOne.equals(valueTwo)) {
                        return false;
                    }
                } catch (Throwable e) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return isEqual;
    }

    /**
     * 同一个域名
     * @param url
     * @param host
     * @return
     */
    public static boolean equalsHost(String url,String host) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        if (TextUtils.isEmpty(host)) {
            return false;
        }

        Uri uri = Uri.parse(url);
        if (uri == null) {
            return false;
        }

        String uri_host = uri.getHost();
        if (uri_host.equals(host)) {
            return true;
        }

        return false;
    }

    /**
     * 是否为有效url
     * @param url
     * @return 是否有效url
     */
    public static boolean isValidURL(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        Uri uri = Uri.parse(url);
        if (uri != null) {
            String host = uri.getHost();
            if (TextUtils.isEmpty(host)) {
                return false;
            }
            else {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回整理过的path，所有字母都小写
     * @param url
     * @return
     */
    public static String tidyURLPath(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }

        Uri uri = Uri.parse(url);
        if (uri == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        List<String> paths = uri.getPathSegments();
        for (String p : paths) {
            if (!TextUtils.isEmpty(p)) {
                if (isFirst) {
                    isFirst = false;
                }
                else {
                    builder.append("/");
                }
                builder.append(p);
            }
        }

        return builder.toString();
    }

    /**
     * 返回所有path，去掉"/", ".", "..";等path
     * @param url
     * @return
     */
    public static List<String> tidyURLPathSegments(String url) {
        if (TextUtils.isEmpty(url)) {
            return new ArrayList<>();
        }

        Uri uri = Uri.parse(url);
        if (uri == null) {
            return new ArrayList<>();
        }

        List<String> paths = uri.getPathSegments();
        List<String> list = new ArrayList<>();
        for (String p : paths) {

            if (TextUtils.isEmpty(p)) {continue;}
            if (p.equals("/")) {continue;}
            if (p.equals(".")) {continue;}
            if (p.equals("..")) {continue;}

            list.add(p);
        }

        return list;
    }

    ///////////////////////////////////////////////////////////////////
    /**
     *
     * @param builder
     * @param key
     * @param value 基本类型和String支持
     * @return
     */
    private static boolean appendQuery(StringBuilder builder, String key, Object value) {
        String v = null;

        if (value != null && value.getClass().isPrimitive()) {
            v = value.toString();
        } else if (value instanceof String) {
            v = (String)value;
        }

        if (!TextUtils.isEmpty(v)) {

            try {
                builder.append(key + "=" + Uri.encode(v, ALLOWED_URI_CHARS));//RFC-2396
            } catch (Throwable e) {return false;}

            builder.append("&");

            return true;
        } else {
            return false;
        }
    }
}
