package com.vz.mybatis.enhance.common.mapper.hp;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

/**
 * @author visy.wang
 * @description: 方法引用（Lambda）获取助手
 * @date 2023/4/25 10:06
 */
public class MethodRefHelper {
    /**
     * 获取方法引用的属性名
     * @param methodRef 方法引用
     * @return getter对应的属性名
     */
    public static String getFieldName(Object methodRef) {
        try {
            // 第1步 获取SerializedLambda
            Method method = methodRef.getClass().getDeclaredMethod("writeReplace");
            method.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) method.invoke(methodRef);
            // 第2步 implMethodName 即为Field对应的Getter方法名
            String implMethodName = serializedLambda.getImplMethodName();
            if (implMethodName.startsWith("get") && implMethodName.length() > 3) {
                return Introspector.decapitalize(implMethodName.substring(3));
            } else if (implMethodName.startsWith("is") && implMethodName.length() > 2) {
                return Introspector.decapitalize(implMethodName.substring(2));
            } else if (implMethodName.startsWith("lambda$")) {
                throw new IllegalArgumentException("SerializableFunction不能传递lambda表达式,只能使用方法引用");
            } else {
                throw new IllegalArgumentException(implMethodName + "不是Getter方法引用");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
