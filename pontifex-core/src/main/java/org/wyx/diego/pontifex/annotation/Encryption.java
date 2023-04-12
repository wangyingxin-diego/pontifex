package org.wyx.diego.pontifex.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Encryption {

    EncryptionDecryptionAlgorithm algorithm() default EncryptionDecryptionAlgorithm.AES128;

    String key() default "";

}
