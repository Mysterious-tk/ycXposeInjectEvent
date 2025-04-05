package com.example.injectevent;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XC_MethodHook;

import android.content.pm.PackageManager;
import android.content.pm.Signature; // 添加Signature类的导入
import android.view.InputDevice;
import android.view.InputEvent;
import android.os.Binder;
import java.lang.reflect.Field;
import java.util.Arrays;
import android.view.MotionEvent;

public class MainHook implements IXposedHookLoadPackage {

    private static final String INJECT_EVENT_CLASS = "android.hardware.input.InputManager";
    private static final String INJECT_EVENT_METHOD = "injectInputEvent";
    private static final String IINPUT_PROXY_CLASS = "android.hardware.input.IInputManager$Stub$Proxy";
    private static final String TARGET_CLASS = "com.sony.apps.digitalpaperapp.view.fragment.SinglePageFragment$3";
    

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        // 修正后的IInputManager Hook
        XposedHelpers.findAndHookMethod(
            IINPUT_PROXY_CLASS,
            ClassLoader.getSystemClassLoader(),
            "injectInputEvent",
            InputEvent.class,
            int.class,
            new XC_MethodHook() {
                
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    try {
                        // 动态获取SOURCE_ANY常量值
                        Class<?> inputDeviceClass = XposedHelpers.findClass("android.view.InputDevice", lpparam.classLoader);
                        int sourceAny = XposedHelpers.getStaticIntField(inputDeviceClass, "SOURCE_ANY");
                        
                        InputEvent event = (InputEvent) param.args[0];
                        
                        // 新增事件类型判断
                        if (event instanceof MotionEvent) {
                            XposedBridge.log("MotionEvent详情: " + event.toString());
                        } else {
                            // 仅修改非MotionEvent的输入源
                            XposedHelpers.setIntField(event, "mSource", sourceAny);
                            XposedBridge.log("已修改输入源");
                        }
                        
                        // 设置异步注入模式（保持原有逻辑）
                        Class<?> inputManagerClass = XposedHelpers.findClass("android.hardware.input.InputManager", lpparam.classLoader);
                        int asyncMode = XposedHelpers.getStaticIntField(inputManagerClass, "INJECT_INPUT_EVENT_MODE_ASYNC");
                        param.args[1] = asyncMode;
                        
                    } catch (Throwable e) {
                        XposedBridge.log("事件处理异常: " + e.getMessage());
                    }
                }
            });
        XposedHelpers.findAndHookMethod("android.view.MotionEvent", 
            lpparam.classLoader,
            "getToolType", 
            int.class, 
            new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) {
                    return 1; // 始终返回手指类型
                }
            });
            if (lpparam.packageName.equals("com.sony.apps.digitalpaperapp")){
        XposedHelpers.findAndHookMethod(
        "com.sony.apps.digitalpaperapp.utils.DigitalPaperAppDetector$1", // 匿名内部类路径
        lpparam.classLoader, 
        "onFling", 
        MotionEvent.class, 
        MotionEvent.class, 
        float.class, 
        float.class, 
        new XC_MethodHook() {
            
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                try {
                    // 获取四个参数
                    MotionEvent e1 = (MotionEvent) param.args[0];
                    MotionEvent e2 = (MotionEvent) param.args[1];
                    float velocityX = (float) param.args[2];
                    float velocityY = (float) param.args[3];

                    // 打印事件详细信息
                    logFlingDetails(e1, e2, velocityX, velocityY);
                    
                } catch (Exception e) {
                    XposedBridge.log("Hook onFling error: " + e);
                }
            }
            
            private void logFlingDetails(MotionEvent e1, MotionEvent e2, float vx, float vy) {
                String info = String.format("onFling捕获: \n" 
                    + "e1[%s]\n"
                    + "e2[%s]\n" 
                    + "vX=%.1f Y=%.1f",
                    e1.toString(),
                    e2.toString(),
                    vx, vy);
                XposedBridge.log(info);
            }
            
        }
    );
    }
                    // Hook Binder身份验证
        XposedHelpers.findAndHookMethod("android.os.Binder", 
            lpparam.classLoader, 
            "getCallingUid", 
            new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    // 优化后的堆栈检查（仅检查前8层）
                    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
                    for (int i = 3; i < Math.min(11, stack.length); i++) { // 从第3层开始检查
                        if ("injectInputEvent".equals(stack[i].getMethodName())) {
                            param.setResult(1000);
                            break; // 找到立即退出循环
                        }
                    }
                }
            });


    
    }
}