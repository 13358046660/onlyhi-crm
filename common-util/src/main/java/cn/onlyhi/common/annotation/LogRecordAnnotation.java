package cn.onlyhi.common.annotation;

import cn.onlyhi.common.enums.ClientEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/2.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface LogRecordAnnotation {
    /**
     * 模块编码
     *
     * @return
     */
    int moduleCode() default 0;

    /**
     * 模块名称
     *
     * @return
     */
    String moduleName() default "";

    /**
     * 页面编码
     *
     * @return
     */
    int pageCode() default 0;

    /**
     * 页面名称
     *
     * @return
     */
    String pageName() default "";

    /**
     * 方法编码
     *
     * @return
     */
    int methodCode() default 0;

    /**
     * 方法名称
     *
     * @return
     */
    String methodName() default "";

    /**
     * 接口描述
     *
     * @return
     */
    String description() default "";

    /**
     * 是否检查token
     *
     * @return
     */
    boolean checkToken() default true;

    ClientEnum.UserType[] userTypes() default {};
}
