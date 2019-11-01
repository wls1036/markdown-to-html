## 背景

自己在阿里云上搭建了一个wordpress个人博客，习惯了用markdown写文章，是在不习惯用可视化编辑器，也曾试用过wordpress一些markdown插件，比如`wp-editormd`，也不能说不好，就是不习惯，试过markdown转html通过wordpress 代码编辑器直接贴代码，也不尽理想，主要有几方面的原因：

- 我代码高亮用的是`Google Syntax Highlighter for WordPress`插件，无论是wp-editormd还是转的html都无法正确将代码专成插件指定的语法，插件也无法设置语法格式。
- 转过来的一些样式不满意，所以每次都得手动调整，如果一两次还好，长期肯定接受不了。

还有一个很重要的原因在于作为一名重度markdown用户并且是资深程序员，我希望我能够控制转换的过程，所以就自己写了一个。刚开始的思路当然是去解析md文件，但感觉得不偿失，这样就相当于实现了一个markdown的解析器了，后面尝试用正则去转，效果还是相当可以。

## 实现

我自己写的博客用到的markdown标签并不是很多，也没想过将所有的md标签全部实现转换，够用就行，如果需要可以自行添加。转换的原理是利用java.lang.String.replaceAll函数匹配正则逐步替换掉原内容实现。

有两个地方需要注意下，一个是`java.lang.String.replaceAll`不支持`Pattern.DOTALL`模式，什么意思呢，正常模式下，正则里面的点(DOT)`.`只匹配可见字符，无法匹配不可见字符，加上这个以后点可以匹配任意字符。另外一个是替换的问题，先看下replaceAll的源码：

```java
public String replaceAll(String regex, String replacement) {
        return Pattern.compile(regex).matcher(this).replaceAll(replacement);
    }
```

`replacement`参数为要替换的字符串，`$1`表示获取第一个匹配的字符串，以此类推，`$N`表示获取第N个匹配的字符串。

知道以上两点后就是写正则匹配，代码如下

```java
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
        String html = AppUtil.readTextFile("/Users/asan/document/004GC/md.md");
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
        System.out.println(html);
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
```

简单解读下代码

之所以要预处理是因为很多正则需要匹配行开始，如果是第一行的话因为没有换行符`\n`无法被匹配到，所以预处理里面行首和行尾各加上一个换行符。

所以的正则都替换完了后需要做一个善后，善后要做的事很多，但有些你可能并不需要，是我为了满足自己的需求加上的。

- 处理<ul>标签，因为正则里行首如果是`-`的话会被匹配成`<li>`标签，但是无法加入`<ul>`标签，需要事后去补上。
- 特殊字符的处理。
- 语法高亮插件`Google Syntax Highlighter for WordPress`有些语言并不支持，但我不想去改我的文章，就在这里做了一个替换，将不支持的语言全部用java的语法进行高亮显示。
- 文章在替换过程中并不能被识别出来，事后识别文章，并加上`<p>`标签。

> 本文就是使用该程序将markdown转为wordpress的博文。