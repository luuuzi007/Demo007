package com.example.arouter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.DexFile;

/**
 * 中间人
 * 容器对象 装载所有的Activity对象
 */
public class Arouter {
    private static Arouter fragment = new Arouter();
    private Map<String, Class<? extends Activity>> map;
    private Context context;

    public static Arouter newInstance() {
        return fragment;
    }

    public void init(Context context) {
        this.context = context;
        List<String> className = getClassName("com.luuuzi.util");
        for (String s : className) {
            //通过类名获取到类对象
            try {
                Class<?> aClass = Class.forName(s);
                //判断类对象是否是arouter的子类
                if (IRouter.class.isAssignableFrom(aClass)) {
                    IRouter iRouter= (IRouter) aClass.newInstance();
                    //调用方法
                    iRouter.putActivity();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Arouter() {
        map=new HashMap<>();
    }

    /**
     * 将Activity添加到map容器中
     *
     * @param key
     * @param clz
     */
    public void addActivity(String key, Class<? extends Activity> clz) {
        if (TextUtils.isEmpty(key) || clz == null) {
            return;
        }
        map.put(key, clz);
    }

    public Class<? extends Activity> getActivity(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        return map.get(key);
    }

    /**
     * 跳转Activity
     *
     * @param key
     * @param bundle
     */
    public void jumpActivity(String key, Bundle bundle) {
        Class<? extends Activity> aClass = map.get(key);
        if (aClass == null) {
            return;
        }
        Intent intent = new Intent(context, aClass);
        if (bundle != null) {
            intent.putExtras(bundle);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 通过包名获取这个包下面的所有类名
     *
     * @param packageName 包名
     * @return
     */
    private List<String> getClassName(String packageName) {
        ArrayList<String> classList = new ArrayList<>();
        String path = null;
        try {
            //通过包管理器，获取到应用信息类然后获取到apk的完整路径
            path = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0).sourceDir;
            DexFile dexFile = new DexFile(path);
            Enumeration<String> entries = dexFile.entries();
            //遍历
            while (entries.hasMoreElements()) {
                String name = entries.nextElement();
                //判断包名是否符合 com.luuuzi.util
                if (name.contains(packageName)) {
                    classList.add(name);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classList;
    }
}
