package com.hz.android.fileselector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义文件选择ListView
 * Created by Administrator on 2018/1/17.
 */

public class FileSelectorView extends ListView {
    //需要展示的数据
    private List<FileItem> fileItemList = new ArrayList<>();
    //数据适配器
    private FileAdapter fileAdapter;
    //标记文件 用于返回上一层
    private File markFile;
    //File选择监听器
    private OnFileSelectedListener fileSelectedListener;

    //字体颜色
    private int textColor = 0x88000000;
    //字体大小
    private float textSize = 16;
    //图片大小
    private int iconSize = 100;

    private FileFilter fileFilter; // 文件过滤器

    private Comparator<FileItem> comparator;//文件比较器
    private Comparator<FileItem> defaultComparator;//默认文件比较器

    // 自定义文件图片
    private FileIconCreator fileIconCreator;


    public FileSelectorView(Context context) {
        this(context, null, 0);
    }

    public FileSelectorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置数据适配器
        fileAdapter = new FileAdapter();
        this.setAdapter(fileAdapter);


        fileIconCreator = new DefaultFileIconCreator(getContext()); // 初始值，把默认的图标获取器作为当前使用的获取器
        defaultComparator = new FolderFirstComparator();
        comparator = defaultComparator; //未设置比较器级使用默认比较器

        //获取外部存储目录即 SDCard
        File storageDirectory = Environment.getExternalStorageDirectory();

        updateCurrentDirectory(storageDirectory);

        // 监听Item的点击
        this.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FileItem clickItem = fileItemList.get(position);
                if (clickItem.isBackFileItem()) { //如果是返回item，则返回上一层
                    updateCurrentDirectory(markFile.getParentFile());
                } else if (clickItem.getFile().isDirectory()) { //如果点击是文件夹，则显示该文件夹内容
                    updateCurrentDirectory(clickItem.getFile());
                } else {
                    if (fileSelectedListener != null) {
                        fileSelectedListener.onSelected(clickItem.getFile());
                    }
                }
            }
        });

    }

    /**
     * 更新当前目录
     */
    public void updateCurrentDirectory() {
        updateCurrentDirectory(markFile);
    }

    /**
     * 更新传入的目录
     *
     * @param currentDirectory
     */
    public void updateCurrentDirectory(File currentDirectory) {
        markFile = currentDirectory;
        //清除原来的数据
        fileItemList.clear();

        //设置过滤
        File[] files = currentDirectory.listFiles(fileFilter);

        //1 判断当前目录是否为根目录
        if (!currentDirectory.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            fileItemList.add(new FileItem(null, true));
        }
        // 2 添加数据进fileItemList
        for (File file : files) {
            fileItemList.add(new FileItem(file));
        }
        // 3 对文件进行排序
        Collections.sort(fileItemList, comparator);

        // 4 通知数据适配器
        fileAdapter.notifyDataSetChanged();

        //通知路径改变
        if (fileSelectedListener != null) {
            fileSelectedListener.onFilePathChanged(currentDirectory);
        }

    }

    private class FileAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return fileItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = View.inflate(getContext(), R.layout.item_file, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.fileIcon = (ImageView) view.findViewById(R.id.file_icon);
                viewHolder.filePath = (TextView) view.findViewById(R.id.file_path);
                //绑定
                view.setTag(viewHolder);
            } else {
                view = convertView;
            }
            //获取绑定,可以对viewHolder进行操作
            ViewHolder viewHolder = (ViewHolder) view.getTag();

            refreshData(viewHolder, position);

            return view;
        }
    }

    private void refreshData(ViewHolder viewHolder, int position) {
        FileItem fileItem = fileItemList.get(position);// 当前位置文件
        viewHolder.filePath.setTextColor(textColor);
        viewHolder.filePath.setTextSize(textSize);
        viewHolder.fileIcon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        viewHolder.fileIcon.setImageDrawable(fileIconCreator.getIcon(fileItem.getFile())); //file==null 可以设置返回图标
        if (fileItem.isBackFileItem()) {
            viewHolder.filePath.setText("...");
        } else {
            viewHolder.filePath.setText(fileItem.getFileName());
        }
    }

    //定义ViewHolder 缓存view对象
    private static class ViewHolder {
        private ImageView fileIcon;
        private TextView filePath;

    }

    /*=======API========*/

    /**
     * 设置选择文件监听器
     *
     * @param fileSelectedListener
     */
    public void setOnFileSelectedListener(OnFileSelectedListener fileSelectedListener) {
        this.fileSelectedListener = fileSelectedListener;
    }

    /**
     * 获取选择文件监听器
     *
     * @return
     */
    public OnFileSelectedListener getFileSelectedListener() {
        return fileSelectedListener;
    }

    /**
     * 文件过滤器 ，提供了常见的过滤设置亦可自定义。
     *
     * @param fileFilter
     */
    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
        updateCurrentDirectory();
    }

    /**
     * 获取文件过滤器
     *
     * @return
     */
    public FileFilter getFileFilter() {
        return fileFilter;
    }

    /**
     * 设置当前目录
     *
     * @param file
     */
    public void setCurrentDirectory(File file) {
        if (file.isDirectory()) {
            updateCurrentDirectory(file);
        } else {
            updateCurrentDirectory(file.getParentFile());
        }
    }

    /**
     * 返回标记的file（当前父目录）
     *
     * @return
     */
    public File getCurrentDirectory() {
        return markFile;
    }

    /**
     * 设置文件图标创建器
     *
     * @param fileIconCreator
     */
    public void setFileIconCreator(FileIconCreator fileIconCreator) {
        this.fileIconCreator = fileIconCreator;
        if (this.fileIconCreator == null) { // 如果外部传入的为null， 则使用默认获取器，确保fileIconCreator不为空
            this.fileIconCreator = new DefaultFileIconCreator(getContext());
        }
    }

    /**
     * 获取文件图标创造器
     *
     * @return
     */
    public FileIconCreator getFileIconCreator() {
        return fileIconCreator;
    }

    /**
     * 设置文件名显示的颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
        fileAdapter.notifyDataSetChanged();
    }

    /**
     * 获取文件名显示的颜色
     *
     * @return
     */
    public int getTextColor() {
        return textColor;
    }

    /**
     * 设置文件名显示的大小
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        if (textSize > 0) {
            this.textSize = textSize;
            fileAdapter.notifyDataSetChanged();
        }
    }

    public float getTextSize() {
        return textSize;
    }

    /**
     * 设置文件图标的大小
     *
     * @param iconSize
     */
    public void setIconSize(int iconSize) {
        if (iconSize > 0) {
            this.iconSize = iconSize;
            fileAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 获取文件图标的大小
     *
     * @return
     */
    public int getIconSize() {
        return iconSize;
    }

    /**
     * 定义点击文件则通知外面的接口
     */
    public interface OnFileSelectedListener {
        /**
         * 回调选中的文件
         *
         * @param selectedFile 选中的文件
         */
        void onSelected(File selectedFile);

        /**
         * 回调选中的文件
         *
         * @param file 选中的文件（夹）
         */
        void onFilePathChanged(File file);
    }

    /**
     * 定义文件图标获取器的接口
     */
    public interface FileIconCreator {

        /**
         * 回调获取文件图标
         *
         * @param file 文件的路径，当file == null时表示返回上一级的选项，而不是一个真实文件
         */
        Drawable getIcon(File file);
    }

    /**
     * 设置文件排序器
     *
     * @param comparator
     */
    public void setFileSortComparator(Comparator<FileItem> comparator) {
        this.comparator = comparator;
        if (comparator == null) {
            this.comparator = defaultComparator;
        }
        updateCurrentDirectory();
        fileAdapter.notifyDataSetChanged();
    }

    /**
     * 升序排序比较器
     */
    public static class FileAscSortComparator implements Comparator<FileItem> {

        @Override
        public int compare(FileItem o1, FileItem o2) {
            if (o1.isBackFileItem() || o2.isBackFileItem()) {
                return 1;
            } else {
                return o1.getFile().getName().compareTo(o2.getFile().getName());
            }
        }
    }

    /**
     * 降序排序比较器
     */
    public static class FileDesSortComparator implements Comparator<FileItem> {

        @Override
        public int compare(FileItem o1, FileItem o2) {
            if (o1.isBackFileItem() || o2.isBackFileItem()) {
                return 1;
            } else {
                return o2.getFile().getName().compareTo(o1.getFile().getName());
            }
        }
    }

    /**
     * 文件夹在前文件在后的排序比较器
     */
    public static class FolderFirstComparator implements Comparator<FileItem> {

        @Override
        public int compare(FileItem o1, FileItem o2) {

            if (o1.getFile() == null) {
                return -1;
            }

            if (o2.getFile() == null) {
                return 1;
            }

            if (o1.getFile().isDirectory()) {
                if (o2.getFile().isDirectory()) {
                    return o1.getFile().getName().compareTo(o2.getFile().getName());
                } else {
                    return -1;
                }
            }

            if (o1.getFile().isFile()) {
                if (o2.getFile().isDirectory()) {
                    return 1;
                } else {
                    return o1.getFile().getName().compareTo(o2.getFile().getName());
                }
            }
            return 0;
        }
    }

}
