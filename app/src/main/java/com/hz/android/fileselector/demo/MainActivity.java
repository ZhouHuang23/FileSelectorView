package com.hz.android.fileselector.demo;

import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hz.android.fileselector.FileSelectorView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private FileSelectorView fileSelectorView;
    private TextView curPathTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileSelectorView = (FileSelectorView) findViewById(R.id.file_selector_view);
        curPathTextView = (TextView) findViewById(R.id.txt_cur_path);

        //test

        File curDir = new File(Environment.getExternalStorageDirectory(), "Download");
        curPathTextView.setText(curDir.getAbsolutePath());
        //切换目录
        fileSelectorView.setCurrentDirectory(curDir);
        //设置文件过滤
        //fileSelectorView.setFileFilter(new FileExtendFilter(Arrays.asList("shp", "kml"))); // 设置过滤规则
        //fileSelectorView.setFileFilter(new FileContainsFieldsFilter(Arrays.asList("shp")));

       /* //自定义文件图标
        fileSelectorView.setFileIconFactory(new FileSelectorView.FileIconCreator() {
            public Drawable getIcon(File file) {
                if (file == null) {
                    return getResources().getDrawable(R.drawable.rotating);
                } else {
                    return getResources().getDrawable(R.drawable.layers3);
                }
            }
        });*/

        //设置选择文件的监听
        fileSelectorView.setOnFileSelectedListener(new FileSelectorView.OnFileSelectedListener() {
            @Override
            public void onSelected(File selectedFile) {
                Toast.makeText(MainActivity.this, "" + selectedFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFilePathChanged(File file) {
                curPathTextView.setText(file.getAbsolutePath());
            }
        });
    }

    public void reset(View view) {
        fileSelectorView.setTextSize(30);//设置文字大小

        fileSelectorView.setIconSize(200); //设置图标大小也就是设置放置图标的imageView的大小

        fileSelectorView.setTextColor(Color.RED); //设置文字颜色
    }

    public void upOrder(View view) {
        fileSelectorView.setFileSortComparator(new FileSelectorView.FileAscSortComparator());
    }

    public void downOrder(View view) {
        fileSelectorView.setFileSortComparator(new FileSelectorView.FileDesSortComparator());
    }

}
