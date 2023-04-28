package com.vz.mybatis.enhance.common.mapper.core;

import com.vz.mybatis.enhance.common.mapper.hp.MapperHelper;
import com.vz.mybatis.enhance.common.mapper.inf.COLUMN_INF;
import com.vz.mybatis.enhance.common.mapper.inf.TABLE_INF;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtMethod;
import org.apache.ibatis.javassist.bytecode.AnnotationsAttribute;
import org.apache.ibatis.javassist.bytecode.ConstPool;
import org.apache.ibatis.javassist.bytecode.MethodInfo;
import org.apache.ibatis.javassist.bytecode.annotation.Annotation;
import org.apache.ibatis.javassist.bytecode.annotation.BooleanMemberValue;
import org.apache.ibatis.javassist.bytecode.annotation.StringMemberValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author visy.wang
 * @description:
 * @date 2023/4/28 13:43
 */
@Component
public class MapperDiscoverer implements ApplicationListener<ContextRefreshedEvent> {
    //需要添加注解的方法名
    private static final List<String> annotatedMethodNames = Arrays.asList("insert", "insertSelective");

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        if(Objects.isNull(applicationContext.getParent())){
            @SuppressWarnings("rawtypes")
            Map<String, BaseMapper> mappers = applicationContext.getBeansOfType(BaseMapper.class);
            //自动将这些标注了注解的bean注册到MQ的事务监听器
            for(BaseMapper<?,?> mapper: mappers.values()){
                Class<?> implMapperClass = (Class<?>) mapper.getClass().getGenericInterfaces()[0];
                System.out.println("mapperClass: "+ implMapperClass);
                TABLE_INF table = MapperHelper.getTable(implMapperClass);
                COLUMN_INF column = table.getPkColumn();
                addOptionsAnnotation(implMapperClass, column.getColumn(), "record."+column.getProperty());
            }
        }
    }

    private void addOptionsAnnotation(Class<?> mapperClass, String keyColumn, String keyProperty){
        try{
            ClassPool pool = ClassPool.getDefault();
            CtClass ctClass = pool.get(mapperClass.getName());

            CtMethod[] methods = ctClass.getDeclaredMethods();
            for (CtMethod method : methods) {
                if(annotatedMethodNames.contains(method.getName())){
                    MethodInfo methodInfo = method.getMethodInfo();
                    ConstPool cp = methodInfo.getConstPool();
                    AnnotationsAttribute attribute = (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);

                    //创建@Option注解，并设置注解属性值
                    Annotation annotation = new Annotation("org.apache.ibatis.annotations.Options", cp);
                    annotation.addMemberValue("keyColumn", new StringMemberValue(keyColumn, cp));
                    annotation.addMemberValue("keyProperty", new StringMemberValue(keyProperty, cp));
                    annotation.addMemberValue("useGeneratedKeys", new BooleanMemberValue(true, cp));
                    //将注解添加到方法上
                    attribute.addAnnotation(annotation);
                    System.out.println(method.getName()+"添加@Options注解成功");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
