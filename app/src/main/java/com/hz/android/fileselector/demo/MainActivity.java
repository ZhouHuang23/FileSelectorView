package com.hz.android.fileselector.demo;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.hz.android.fileselector.FileIconCreator;
import com.hz.android.fileselector.FileSelectorView;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileSelectorView fileSelectorView = (FileSelectorView) findViewById(R.id.file_selector_view);

        //test
        //切换目录
        fileSelectorView.setCurrentDirectory(new File(Environment.getExternalStorageDirectory(), "Download"));
        //设置文件过滤
        fileSelectorView.setFileExtensionForFileFilter(Arrays.asList("shp", "txt"));
        //自定义文件图标
        fileSelectorView.setFileIconFactory(new FileIconCreator() {
            public Drawable getIcon(File file) {
                if (file == null) {
                    return getResources().getDrawable(R.drawable.rotating);
                } else {
                    return getResources().getDrawable(R.drawable.layers3);
                }
            }
        });

        fileSelectorView.setTextSize(30);//设置文字大小
        fileSelectorView.setTextColor(Color.GREEN); //设置文字颜色
        fileSelectorView.setIconSize(200); //设置图标大小也就是设置放置图标的imageView的大小

        //设置选择文件的监听
        fileSelectorView.setFileSelectedListener(new FileSelectorView.OnFileSelectedListener() {
            @Override
            public void onSelected(File selectedFile) {
                Toast.makeText(MainActivity.this, "" + selectedFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
