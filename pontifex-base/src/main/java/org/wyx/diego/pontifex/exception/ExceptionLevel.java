package org.wyx.diego.pontifex.exception;

/**
 * @author diego
 * @time 2015-07-10
 * @description
 */
public enum ExceptionLevel {

    EXCEPTION_MUST(1, ""),
    EXCEPTION_DEFAULT_VALUE(2, "");

    private int level;

    private String description;

    private ExceptionLevel(int level, String description) {

        this.level = level;
        this.description = description;

    }

}
