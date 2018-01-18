package com.hz.android.fileselector;

import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by Administrator on 2018/1/17.
 */

public class FileItem {

    private File file;
    private boolean isBackFileItem = false;

    public FileItem(File file) {
        this.file = file;
    }

    public FileItem(File file, boolean isBackFileItem) {
        this.file = file;
        this.isBackFileItem = isBackFileItem;
    }

    public String getFileName() {
        return file.getName();
    }

    public boolean isBackFileItem() {
        return isBackFileItem;
    }

    public File getFile() {
        return file;
    }
}
