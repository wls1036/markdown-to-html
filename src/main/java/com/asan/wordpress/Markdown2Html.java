package com.asan.wordpress;

import com.asan.wordpress.util.AppUtil;

import java.util.regex.Pattern;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: jianfeng.zheng
 * @since: 2019/10/31 3:05 PM
 * @history: 1.2019/10/31 created by jianfeng.zheng
 */
public class Markdown2Html {


    public static void main(String[] cmd) {
        String html = AppUtil.getSysClipboardText();
        if (html == null || html.trim().length() == 0) {
            System.out.println("请将文章复制到剪贴板");
            return;
        }
        //预处理
        html = pre(html);

        //####  四级标题
        html = replace(html, "\n####\\s?(.*?)(?=\n)+", "<h4>$1</h4>\n");
        //###   三级标题
        html = replace(html, "\n###\\s?(.*?)(?=\n)+", "<h2>$1</h2>\n");
        //##    二级标题
        html = replace(html, "\n##\\s?(.*?)(?=\n)+", "<h2>$1</h2>\n");
        //#     一级标题
        // html = replace(html, "\n#\\s?(.*?)(?=\n)+", "<h1>$1</h1>\n");

        //***  加粗
        html = replace(html, "\n\\*\\*\\*\\s?(.*?)\\*\\*\\*(?=\n)+", "<strong>$1</strong>\n");
        //**    加粗
        html = replace(html, "\n\\*\\*\\s?(.*?)\\*\\*(?=\n)+", "<strong>$1</strong>\n");
        //*    加粗
        html = replace(html, "\n\\*\\s?(.*?)\\*(?=\n)+", "<strong>$1</strong>\n");


        //```   代码引用
        html = replace(html, "\\s*```(\\S+)\\s*(.*?)\\s*```", "\n<pre name=\"code\" class=\"$1\">\n$2\n</pre>\n");
        html = replace(html, "\\s*```\\s*(.*?)\\s*```", "\n<pre name=\"code\" class=\"java\">\n$1\n</pre>\n");
        //`     引用
        html = replace(html, "`(.*?)`", "<code>$1</code>");
        //li    列表
        html = replace(html, "\n-\\s+([^\n]+)(?=\n)?", "\n<li>$1</li>\n");
        //[](more)  more标签
        html = replace(html, "\\[\\]\\(more\\)", "<!--more-->");
        //>     特别说明
        html = replace(html, "\n>(.*?)(?=\n)", "<blockquote>$1</blockquote>");
        //!>     特别说明
        html = replace(html, "\n\\!>\\s*(.*?)(?=\n)", "<blockquote>$1</blockquote>");
        //image 图片
        html = replace(html, "\\!\\[([^\\]]*)\\]\\(([^\\)]+)\\)", "\n<figure class=\"wp-block-image\"><img src=\"$2\" alt=\"$1\"></figure>\n");
        //href  超链接
        html = replace(html, "\\[([^\\]]+)\\]\\(([^\\)]+)\\)", "<a href=\"$2\">$1</a>");
        //善后
        html = post(html);
        AppUtil.setSysClipboardText(html);
        System.out.println("转换完成");
    }

    /**
     * String自带的replaceAll不支持DOTALL特性，参考其实现重写了一个
     *
     * @param content
     * @param regex
     * @param replacement
     * @return
     */
    public static String replace(String content, String regex, String replacement) {
        return Pattern.compile(regex, Pattern.DOTALL).matcher(content).replaceAll(replacement);
    }

    /**
     * 预处理
     *
     * @param content
     * @return
     */
    public static String pre(String content) {
        content = "\n" + content + "\n";
        return content;
    }

    /**
     * 善后
     * 1. 处理<ul>标签
     * 2. 处理语法高亮
     * 3. 处理文章加<p>标签
     * 4. 处理特殊字符
     *
     * @param conent
     * @return
     */
    public static String post(String conent) {
        String[] sp = conent.split("\n+");
        StringBuffer buf = new StringBuffer();
        boolean ul = false;
        boolean plainText = false;
        for (String s : sp) {
            if (s == null || s.length() == 0) {
                continue;
            }
            if (ul && !s.startsWith("<li>")) {
                ul = false;
                buf.append("</ul>\n");
            }
            if (s.startsWith("<pre") || s.startsWith("<blockquote>")) {
                s = replace(s, "class=\"bash\"", "class=\"java\"");
                s = replace(s, "class=\"shell\"", "class=\"java\"");
                s = replace(s, "class=\"sh\"", "class=\"java\"");
                s = replace(s, "class=\"properties\"", "class=\"java\"");
                s = replace(s, "class=\"txt\"", "class=\"java\"");
                s = replace(s, "class=\"text\"", "class=\"java\"");
                s = replace(s, "class=\"\"", "class=\"java\"");
                plainText = true;
            }
            if (s.contains("</pre>") || s.contains("</blockquote>")) {
                plainText = false;
            }
            if (!plainText) {
                s = s.trim();
            }
            if (!s.startsWith("<")) {
                if (!plainText) {
                    buf.append("<p>");
                }
                if (plainText) {
                    s = s.replaceAll("<", "&lt;");
                    s = s.replaceAll(">", "&gt;");
                }
                buf.append(s);
                if (!plainText) {
                    buf.append("</p>");
                }
            } else {
                if (plainText && !s.startsWith("<pre") && !s.startsWith("<blockquote>")) {
                    s = s.replaceAll("<", "&lt;");
                    s = s.replaceAll(">", "&gt;");
                }
                if (s.startsWith("<li>") && !ul) {
                    ul = true;
                    buf.append("<ul>\n");
                }
                buf.append(s);
            }
            buf.append("\n");

        }
        return buf.toString();
    }
}
