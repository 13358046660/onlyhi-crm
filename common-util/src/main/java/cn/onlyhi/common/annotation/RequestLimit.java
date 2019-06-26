package cn.onlyhi.common.annotation;

import java.lang.annotation.*;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/2.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RequestLimit {

    /**
     * 允许访问的次数，默认值MAX_VALUE
     */
    int count() default Integer.MAX_VALUE;

    /**
     * 时间段，单位为毫秒，默认值一分钟
     */
    long time() default 60000;

}
