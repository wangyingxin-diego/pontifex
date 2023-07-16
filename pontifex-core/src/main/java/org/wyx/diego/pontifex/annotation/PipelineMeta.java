package org.wyx.diego.pontifex.annotation;

import org.wyx.diego.pontifex.flow.annotation.DeGrade;
import org.wyx.diego.pontifex.flow.annotation.Flow;
import org.wyx.diego.pontifex.flow.annotation.FlowDeGrade;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PipelineMeta {

    String name() default "";

    Cache cache() default @Cache(
            isOpen = false
    );

    boolean encryption() default true;

    boolean decryption() default true;

    FlowDeGrade flowDeGrade() default @FlowDeGrade(flow = @Flow(), deGrade = @DeGrade());

}
