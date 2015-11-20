# AndroidSweetBehavior
Android support Behavior 实践   
###效果图:    


![效果图.gif](http://upload-images.jianshu.io/upload_images/166866-b243d27707cc6d4b.gif?imageMogr2/auto-orient/strip)


通过自定义Behavior 和 Nest 事件,达到 instagram 选择照片的的效果.
关于Behavior 和 Nest 事件的学习可以看一下我之前写的学习笔记1 [捂脸]

使用方法也是很简单.
###gradle      
/build.gradle
~~~
repositories {
    maven {
        url "https://jitpack.io"
    }
}
~~~
/app/build.gradle
~~~
 compile 'com.github.zzz40500:AndroidSweetBehavior:0.1.1'   
~~~
布局中:
~~~


<android.support.design.widget.CoordinatorLayout android:id="@+id/coordinatorLY"
                                                 xmlns:android="http://schemas.android.com/apk/res/android"
                                                 xmlns:app="http://schemas.android.com/apk/res-auto"
                                                 android:layout_width="match_parent"
                                                 android:layout_height="match_parent"
                                                 android:background="#fff"
                                                 android:fitsSystemWindows="false">
    <android.support.design.widget.AppBarLayout
        android:id="@+id/appBarLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:fitsSystemWindows="false"
        app:layout_behavior="@string/InAppBarLayout"
        >
        <com.mingle.widget.ScaleLayout
            android:layout_width="match_parent"
            android:layout_height="450dp"
            android:background="#0000"
            android:minHeight="200px"
            app:layout_scrollFlags="scroll|exitUntilCollapsed"
            app:scaleProvideX="1"
            app:scaleProvideY="1"
            >
            <ImageView
                android:id="@+id/contentIv"
                android:layout_width="fill_parent"
                android:layout_height="fill_parent"
                android:scaleType="centerCrop"/>
        </com.mingle.widget.ScaleLayout>
    </android.support.design.widget.AppBarLayout>
    <android.support.v7.widget.RecyclerView
        android:id="@+id/rv"
        android:layout_width="fill_parent"
        android:layout_height="fill_parent"
        android:clipToPadding="false"
        app:layout_behavior="@string/InNestChild"/>
</android.support.design.widget.CoordinatorLayout>
~~~

在AppBarLayout 上指定`app:layout_behavior="@string/InAppBarLayout"`     
在RecyclerView 中指定` app:layout_behavior="@string/InNestChild"`   
同时在ScaleLayout 中指定`"scroll|exitUntilCollapsed"`
并且指定`android:minHeight="200px"`
这个值表示当 AppBarLayout 收起来的时候,留在屏幕上的高度.   





####Demo 中使用技术:
#####第三方库:
https://github.com/zzz40500/ScaleLayout   用于设置宽高比的   
glide :用于设置图片的.   
https://github.com/tianzhijiexian/CommonAdapter 用于快速建  立RV适配器.   
#####第三方代码:
学习笔记web 页的loading 效果核心代码来至19 的SwipeRefreshLayout中.






##实践2待续...   
