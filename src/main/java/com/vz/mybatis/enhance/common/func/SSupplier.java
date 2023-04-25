package com.vz.mybatis.enhance.common.func;

import java.io.Serializable;
import java.util.function.Supplier;


@FunctionalInterface
public interface SSupplier<T> extends Supplier<T>, Serializable {
    //通过Getter方法引用获取属性名所需的函数是接口
}
