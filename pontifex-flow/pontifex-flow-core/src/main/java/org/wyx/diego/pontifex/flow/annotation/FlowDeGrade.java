package org.wyx.diego.pontifex.flow.annotation;

public @interface FlowDeGrade {

    Flow flow() default @Flow();

    DeGrade deGrade() default @DeGrade();

}
