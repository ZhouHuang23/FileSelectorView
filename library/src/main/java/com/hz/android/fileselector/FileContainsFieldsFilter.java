package com.hz.android.fileselector;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

/**
 * 根据包含字段过滤，文件过滤器的其中一种策略。
 *
 * Created by Administrator on 2018/1/19.
 */

public class FileContainsFieldsFilter implements FileFilter {
    private List<String> fieldsList;

    public FileContainsFieldsFilter(List<String> fieldsList){
        this.fieldsList = fieldsList;
    }

    @Override
    public boolean accept(File pathname) {
        if (!fieldsList.isEmpty()) { //list中有内容 说明需要按照文件类型进行过滤
            if (pathname.isDirectory()) {
                return true;
            }
            for (String fileFilterString : fieldsList) {
                if (pathname.getName().contains(fileFilterString)) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }
}
