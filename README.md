# FileSelectorView
##### 介绍
FileSelectorView 是自定义的文件选择器，用户在此基础上可自定义文件选择器风格。
##### 功能
- 切换目录
- 文件过滤
- 自定义文件图标和设置大小
- 设置文件名文字的大小、颜色
- 监听选择的文件
##### 使用
FileSelectorView 使用简单，只需将其加入到布局文件即可，无其他使用限制。

- 布局文件

```
<android.support.constraint.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >
......
    <com.hz.android.fileselector.FileSelectorView
        android:id="@+id/file_selector_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
......

</android.support.constraint.ConstraintLayout   

```
- 代码中

```java
......

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
    
    ......


```
##### 注意
读取文件路径需要涉及到用户的许可：

```
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```


