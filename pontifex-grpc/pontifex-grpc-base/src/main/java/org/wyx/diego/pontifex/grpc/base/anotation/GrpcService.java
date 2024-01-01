package org.wyx.diego.pontifex.grpc.base.anotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface GrpcService {


    String name() default "";

}
