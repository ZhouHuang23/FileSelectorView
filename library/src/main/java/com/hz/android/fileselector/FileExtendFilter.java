package com.hz.android.fileselector;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

/**
 * 根据后缀名过滤。
 *
 * Created by Administrator on 2018/1/19.
 */

public class FileExtendFilter implements FileFilter {
    private List<String> extList;

    public FileExtendFilter(List<String> extList){
        this.extList = extList;
    }

    @Override
    public boolean accept(File pathname) {
        if (!extList.isEmpty()) { //list中有内容 说明需要按照文件类型进行过滤
            if (pathname.isDirectory()) {
                return true;
            }
            for (String fileFilterString : extList) {
                if (pathname.getName().toLowerCase().endsWith(fileFilterString)) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }
}
