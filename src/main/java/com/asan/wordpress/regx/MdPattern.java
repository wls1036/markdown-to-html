package com.asan.wordpress.regx;

import java.util.regex.Pattern;

/**
 * @Description:
 * @author: jianfeng.zheng
 * @since: 2019/10/31 3:10 PM
 * @history: 1.2019/10/31 created by jianfeng.zheng
 */
public class MdPattern {

    public static final Pattern H2 = Pattern.compile("##\\s?.*?\r?\n");

}
