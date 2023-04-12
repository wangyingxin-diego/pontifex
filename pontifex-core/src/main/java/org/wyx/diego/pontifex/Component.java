package org.wyx.diego.pontifex;


import org.wyx.diego.pontifex.component.ComponentReq;
import org.wyx.diego.pontifex.component.Res1;
import org.wyx.diego.pontifex.component.Res2;

import java.util.function.Function;

/**
 * @author diego
 * @time 2015-07-16
 * @description
 */
public interface Component<P extends ComponentReq, R1> {
    Res1<R1> call(P req);

    default <R2> Res2<R1, R2> apply(P req, Function<R1, R2> function) {
        Res1<R1> r = this.call(req);
        R1 r1 = (R1)r.getR1();
        R2 r2 = function.apply(r1);
        Res2<R1, R2> res = new Res2(r1, r2);
        return res;
    }

    enum MethodName {
        CALL("call"),
        APPLY("apply");

        private String methodName;

        private MethodName(String methodName) {
            this.methodName = methodName;
        }

        public static Component.MethodName getByMethodName(String methodName) {

            for(MethodName methodName1 : values()) {
                if (methodName1.methodName.equals(methodName)) {
                    return methodName1;
                }
            }

            return null;
        }
    }
}
