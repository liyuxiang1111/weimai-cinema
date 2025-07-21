package com.liyuxiang.film.config.logs;

import java.lang.annotation.*;

// TYPE表示注解可以放在类的上面 method表示可以放到 方法上面
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {
    String module() default  "";
    String operator() default  "";
}
