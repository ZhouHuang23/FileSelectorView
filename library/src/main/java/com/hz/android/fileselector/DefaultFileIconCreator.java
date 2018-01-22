package com.hz.android.fileselector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2018/1/22.
 */


public class DefaultFileIconCreator implements FileSelectorView.FileIconCreator {

    //缓存绘制的文件图标
    private Map<String, BitmapDrawable> savedFileIconMap = new HashMap<>();
    private Context context;

    public DefaultFileIconCreator(Context context) {
        this.context = context;
    }

    @Override
    public Drawable getIcon(File file) {
        if (file == null) {
            return context.getResources().getDrawable(R.drawable.ic_folder_back);
        } else if (file.isDirectory()) {
            return context.getResources().getDrawable(R.drawable.folder);
        } else { // 根据文件后缀生成对应的文件图标
            String fileExtensionString = file.getName().substring(file.getName().lastIndexOf(".") + 1).toUpperCase();
            BitmapDrawable savedFileIcon = savedFileIconMap.get(fileExtensionString);
            BitmapDrawable commonFileIcon = (BitmapDrawable) context.getResources().getDrawable(R.drawable.file_common); // 图片实际的对象类型为BitmapDrawable

            if (fileExtensionString.length() > 5) {
                return commonFileIcon;
            } else {
                if (savedFileIcon != null) {
                    return savedFileIcon;
                } else {
                    Bitmap icon = commonFileIcon.getBitmap().copy(Bitmap.Config.ARGB_8888, true); //拷贝图片副本 使之可以修改
                    Canvas canvas = new Canvas(icon); // 把图片作为画布
                    //在画布上绘制文字，相当于在图片上绘制
                    Paint paint = new Paint(Paint.FAKE_BOLD_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); //设置粗体反锯齿
                    paint.setTextSize(35);
                    paint.setColor(Color.WHITE);

                    //获取文字矩形
                    Rect fileExtensionRect = new Rect();
                    paint.getTextBounds(fileExtensionString, 0, fileExtensionString.length(), fileExtensionRect);

                    int centerX = icon.getWidth() / 2 - (fileExtensionRect.right - fileExtensionRect.left) / 2;
                    int centerY = icon.getHeight() / 2 + (fileExtensionRect.bottom - fileExtensionRect.top) / 2;
                    int offsetX = -10;
                    int offsetY = 20;

                    canvas.drawText(fileExtensionString, centerX + offsetX, centerY + offsetY, paint);//在画布正确位置绘制文字

                    savedFileIcon = new BitmapDrawable(icon);// 封装成Drawable对象
                    savedFileIconMap.put(fileExtensionString, savedFileIcon);
                    return savedFileIcon;
                }
            }
        }
    }
}