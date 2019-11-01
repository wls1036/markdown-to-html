package com.asan.wordpress.exception;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: jianfeng.zheng
 * @since: 2019/10/31 3:08 PM
 * @history: 1.2019/10/31 created by jianfeng.zheng
 */
public class AppRuntimeException extends RuntimeException {

    public AppRuntimeException(Throwable ex){
        ex.printStackTrace();
    }
}
