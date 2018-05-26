package com.xingen.okhttplib.internal.json.utils;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by ${xinGen} on 2018/1/10.
 * <p>
 *  Gson解析泛型类型数据的关键就是TypeToken
 *
 */

public class GsonUtils {
    public static <T> T toBean(String content, Class<T> mclass) {
        return toBean(new Gson(), content, mclass);
    }

    /**
     * 解析最基本对象
     * @param gson
     * @param content
     * @param mclass
     * @param <T>
     * @return
     */
    public static <T> T toBean(Gson gson, String content, Class<T> mclass) {
        T t = null;
        try {
            t = gson.fromJson(content, mclass);
        } catch (Exception e) {
            e.printStackTrace();
            t = null;
        }
        return t;
    }

    /**
     * 解析实体类嵌套泛型，List嵌套泛型等等。
     * @param gson
     * @param content
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T toBean(Gson gson,String content,Type type){
        try {
            T t=gson.fromJson(content,type);
            return t;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
    /**
     *  根据返回值来，制定泛型类型
     * 解析实体类嵌套泛型，List嵌套泛型等等。
     * @param gson
     * @param content
     * @param <T>
     * @return
     */
    public static <T> T toBean(Gson gson,String content){
        try {
            T t=gson.fromJson(content,new TypeToken<T>(){}.getType());
            return t;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
    /**
     * 转成List<T>的数据
     *
     * @param gson
     * @param content
     * @param <T>
     * @return
     */
    public static <T> List<T> toBeanList(Gson gson, String content) {
        try {
            Type type = new TypeToken<List<T>>() {}.getType();
            List<T> list = gson.fromJson(content, type);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 转成Map<String，T>的数据
     * @param gson
     * @param content
     * @param <T>
     * @return
     */
    public static <T> Map<String,T> toBeanMap(Gson gson, String content) {
        try {
            Type type = new TypeToken<Map<String,T>>() {}.getType();
            Map<String,T> map= gson.fromJson(content, type);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String toJson(Object object) {
        return toJson(new Gson(), object);
    }
    public static String toJson(Gson gson, Object object) {
        String content = null;
        try {
            content = gson.toJson(object);
        } catch (Exception e) {
            e.printStackTrace();
            content = null;
        }
        return content;
    }

    /**
     * 用于获取Class对应的Type
     * @param subclass
     * @return
     */
    public static Type getSuperclassTypeParameter(Class<?> subclass){
        //得到带有泛型的类
        Type superclass = subclass.getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        //取出当前类的泛型
        ParameterizedType parameter = (ParameterizedType) superclass;
        return $Gson$Types.canonicalize(parameter.getActualTypeArguments()[0]);
    }

}
