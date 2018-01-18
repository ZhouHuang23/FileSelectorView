package com.hz.android.fileselector;

import android.content.Context;
import android.graphics.Color;
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
import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 */

public class FileSelectorView extends ListView {
    //需要展示的数据
    private List<FileItem> fileItemList = new ArrayList<>();
    //数据适配器
    private FileAdapter fileAdapter;
    //标记文件 用于返回上一层
    private File markFile;
    // 过滤列表
    private List<String> fileFilterList = new ArrayList<>();
    //File选择监听器
    private OnFileSelectedListener fileSelectedListener;

    //字体颜色
    private int textColor = -1;
    //字体大小
    private float textSize = -1.0f;
    //图片大小
    private int iconSize = -1;


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

        //获取外部存储目录即 SDCard （可以交给外部设置 ）
        File rootDirFile = Environment.getExternalStorageDirectory();

        updateCurrentDirectory(rootDirFile);

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

    // 根据传入的目录显示其包含的文件
    public void updateCurrentDirectory(File currentDirectory) {
        markFile = currentDirectory;
        //清除原来的数据
        fileItemList.clear();

        //设置过滤
        File[] files = currentDirectory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (!fileFilterList.isEmpty()) { //list中有内容 说明需要按照文件类型进行过滤
                    if (pathname.isDirectory()) {
                        return true;
                    }
                    for (String fileFilterString : fileFilterList) {
                        if (pathname.getName().toLowerCase().endsWith(fileFilterString)) {
                            return true;
                        }
                    }
                } else {
                    return true;
                }
                return false;
            }
        });

        //1 判断当前目录是否为更目录
        if (!currentDirectory.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            fileItemList.add(new FileItem(null, true));
        }
        // 2 添加数据进fileItemList
        for (File file : files) {
            fileItemList.add(new FileItem(file));
        }

        //通知数据适配器
        fileAdapter.notifyDataSetChanged();

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

        if (textColor != -1) {
            viewHolder.filePath.setTextColor(textColor);
        }
        if (textSize != -1.0) {
            viewHolder.filePath.setTextSize(textSize);
        }
        if (iconSize != -1) {
            viewHolder.fileIcon.setLayoutParams(new LinearLayout.LayoutParams(iconSize,iconSize));
        }

        if (fileIconCreator == null) {
            if (fileItem.isBackFileItem()) {
                viewHolder.fileIcon.setImageResource(R.drawable.ic_folder_back);
                viewHolder.filePath.setText("...");
            } else if (fileItem.getFile().isDirectory()) {
                viewHolder.fileIcon.setImageResource(R.drawable.folder);
                viewHolder.filePath.setText(fileItem.getFileName());
            } else {
                viewHolder.fileIcon.setImageResource(R.drawable.file_common);
                viewHolder.filePath.setText(fileItem.getFileName());
            }
        } else {
            viewHolder.fileIcon.setImageDrawable(fileIconCreator.getIcon(fileItem.getFile())); //file==null 可以设置返回图标
            if (fileItem.isBackFileItem()) {
                viewHolder.filePath.setText("...");
            } else {
                viewHolder.filePath.setText(fileItem.getFileName());
            }
        }

    }

    //定义ViewHolder 缓存view对象
    private static class ViewHolder {
        private ImageView fileIcon;
        private TextView filePath;

    }

    //定义接口 点击的是文件则通知外面
    public interface OnFileSelectedListener {
        void onSelected(File selectedFile);
    }

    //对外接口 设置选择文件监听器
    public void setFileSelectedListener(OnFileSelectedListener fileSelectedListener) {
        this.fileSelectedListener = fileSelectedListener;
    }

    //对外接口 提供文件过滤
    public void setFileExtensionForFileFilter(List<String> fileFilterList) {
        //this.fileFilterList.clear();
        this.fileFilterList = fileFilterList;
        updateCurrentDirectory(markFile);
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

    public void setFileIconFactory(FileIconCreator fileIconCreator) {
        this.fileIconCreator = fileIconCreator;
    }

    /**
     * 设置文件名显示的颜色
     *
     * @param textColor
     */
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    /**
     * 设置文件名显示的大小
     *
     * @param textSize
     */
    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    /**
     * 设置文件名显示的大小
     *
     * @param iconSize
     */
    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }
}
