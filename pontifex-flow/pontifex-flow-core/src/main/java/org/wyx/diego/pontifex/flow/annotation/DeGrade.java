package org.wyx.diego.pontifex.flow.annotation;

import org.wyx.diego.pontifex.flow.DeGradeModel;

public @interface DeGrade {

    DeGradeModel model() default DeGradeModel.SLOW;

    int rt() default 5000;

    int window() default 5;

    int minInvoker() default 10;

    int threshold() default 50;

}
