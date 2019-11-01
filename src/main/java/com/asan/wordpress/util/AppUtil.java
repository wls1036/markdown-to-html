package com.asan.wordpress.util;

import com.asan.wordpress.exception.AppRuntimeException;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * @Description:
 * @author: jianfeng.zheng
 * @since: 2019/10/26 1:43 PM
 * @history: 1.2019/10/26 created by jianfeng.zheng
 */
public class AppUtil {

    public static void writeFile(String path, String content) {
        try {
            FileOutputStream fout = new FileOutputStream(path);
            fout.write(content.getBytes());
            fout.flush();
            fout.close();
        } catch (Exception ex) {
            //Logger.log("write file failed:" + path);
            throw new AppRuntimeException(ex);
        }
    }

    /**
     * 读取文本文件
     *
     * @param path
     * @return
     */
    public static String readTextFile(String path) {
        String content = null;
        try {
            FileInputStream fin = new FileInputStream(path);
            byte[] buf = new byte[fin.available()];
            fin.read(buf);
            fin.close();
            content = new String(buf, "UTF-8");
        } catch (Exception ex) {
            //Logger.log("write file failed:" + path);
            throw new AppRuntimeException(ex);
        }
        return content;
    }

    /**
     * 1. 从剪切板获得文字。
     */
    public static String getSysClipboardText() {
        String ret = "";
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 获取剪切板中的内容
        Transferable clipTf = sysClip.getContents(null);

        if (clipTf != null) {
            // 检查内容是否是文本类型
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = (String) clipTf
                            .getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret;
    }

    /**
     * 2.将字符串复制到剪切板。
     */
    public static void setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }
}
