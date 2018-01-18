package com.hz.android.fileselector;

import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * 定义文件图标获取器的接口
 * Created by Administrator on 2018/1/18.
 */

public interface FileIconCreator {

    /**
     *
     * @param file 文件的路径，当file == null时表示返回上一级的选项，而不是一个真实文件
     * @return
     */
    Drawable getIcon(File file);
}
