package org.wyx.diego.pontifex.exception;

/**
 * @author wangyingxin
 * @title: BusinessException
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/1
 */
public interface BusinessException {
    int getCode();

    String getMsg();
}
