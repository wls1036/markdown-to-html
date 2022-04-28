package com.asan.wordpress.convert;

/**
 * @Description:
 * @author: jianfeng.zheng
 * @since: 2022/4/28 3:00 下午
 * @history: 1.2022/4/28 created by jianfeng.zheng
 */
public enum Mode {

    UNKNOWN("unknown", "模式错误"),
    IMAGE("image", "图片转换"),
    HTML("html", "转换为html");
    private String name;
    private String desc;

    Mode(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static Mode value(String name) {
        Mode[] values = values();
        for (Mode item : values) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return UNKNOWN;
    }
}
