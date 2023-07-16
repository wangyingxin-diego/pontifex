package org.wyx.diego.pontifex.flow.annotation;

import org.wyx.diego.pontifex.flow.FlowModel;

public @interface Flow {

    int count() default 2000;

    FlowModel model() default FlowModel.QPS;

}
