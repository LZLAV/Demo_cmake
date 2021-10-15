package com.lzlbuilder.demo.natives;

/**
 * @author liuzuoliang
 * @Description Native 代码测试类
 * @Date
 * @E-Mail Address：
 */
public class NativeTest {

    public native String stringFromJNI();
    public static native String stringFromJNIStatic();
    public native String getLine(String prompt);
}