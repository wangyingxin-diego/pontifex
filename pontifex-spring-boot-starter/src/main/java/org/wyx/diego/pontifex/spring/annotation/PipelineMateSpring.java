package org.wyx.diego.pontifex.spring.annotation;


import org.springframework.stereotype.Component;
import org.wyx.diego.pontifex.annotation.PipelineMeta;

import java.lang.annotation.*;

@Component
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PipelineMateSpring {

    PipelineMeta pipelineMeta();

}
