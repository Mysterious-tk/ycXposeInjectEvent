package com.example.injectevent;

import android.os.SystemClock;
import android.view.InputEvent;
import android.view.KeyEvent;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;

public class InputUtils {
    
    public static void sendKeyEvent(int keyCode) {
        try {
            // 获取InputManager实例
            Class<?> inputManagerClass = Class.forName("android.hardware.input.InputManager");
            Object inputManager = inputManagerClass.getMethod("getInstance").invoke(null);
            
            // 构造KeyEvent
            long now = SystemClock.uptimeMillis();
            KeyEvent downEvent = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0);
            KeyEvent upEvent = new KeyEvent(now, now + 50, KeyEvent.ACTION_UP, keyCode, 0);
            
            // 获取injectInputEvent方法
            Method injectMethod = inputManagerClass.getMethod("injectInputEvent", 
                InputEvent.class, int.class);
            
            // 注入事件（异步模式）
            injectMethod.invoke(inputManager, downEvent, 0);
            injectMethod.invoke(inputManager, upEvent, 0);
            
        } catch (Exception e) {
            XposedBridge.log("发送按键失败: " + e.getMessage());
        }
    }
}