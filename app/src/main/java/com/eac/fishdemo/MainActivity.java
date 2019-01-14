package com.eac.fishdemo;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int newWidth;
    private int newHeight;
    private Random random;
    private int offset = 100;

    private int width; // 屏幕宽度（像素）
    private int height; // 屏幕高度（像素）
    private ConstraintLayout contaner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置全透明标题栏
        setStatusBarFullTransparent();

        contaner = findViewById(R.id.contaner);
        //屏幕宽高
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;
        height = metric.heightPixels;
        newWidth = width + offset;
        newHeight = height + offset;
        random = new Random();
        addView();

    }

    private void addView() {
        ImageView imageView = new ImageView(this);
        imageView.setVisibility(View.GONE);


//        设置帧动画
        imageView.setImageResource(R.drawable.hong_right_animlist);
        AnimationDrawable drawable = (AnimationDrawable) imageView.getDrawable();
        drawable.start();
        //设置平移和曲线
        initAnimation(0, imageView);
        //添加这个
        contaner.addView(imageView);

    }
    private int tempY;
    private int tempX;

    private void initAnimation(int type, final ImageView iv) {

        iv.setVisibility(View.VISIBLE);
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        Point endPoint;
        //下面是随机生成一个坐标，并让他随机从屏幕的某个方向滑出
        if (x < width / 2 && y < height / 2) {
            if (x < y) {
                x = 0;
                endPoint = new Point(width + offset, random.nextInt(height));
            } else {
                y = 0;
                endPoint = new Point(random.nextInt(width), height + offset);
            }
        } else if (x > width / 2 && y < height / 2) {
            if ((width - x) < y) {
                x = width + offset;
                endPoint = new Point(-offset, random.nextInt(height));
            } else {
                y = 0;
                endPoint = new Point(random.nextInt(width), height + offset);
            }
        } else if (x < (width / 2) && y > height / 2) {
            if (x < (height - y)) {
                x = 0;
                endPoint = new Point(width, random.nextInt(height + offset));
            } else {
                y = height + offset;
                endPoint = new Point(random.nextInt(width), -offset);
            }
        } else {
            if ((width - x) < (height - y)) {
                x = width + offset;
                endPoint = new Point(-offset, random.nextInt(height));
            } else {
                y = height + offset;
                endPoint = new Point(random.nextInt(width), -offset);
            }
        }
        Point startPoint = new Point(x, y);
        int newX = random.nextInt(375);
        int newY = random.nextInt(667);
        Point randomPoint = new Point(newX, newY);

        // 这个randmoPoint 就是在两点之间的第三点，做贝塞尔曲线，让鱼滑动的方向是曲线
        MyBeizer bezierEvaluator = new MyBeizer(randomPoint);
        ValueAnimator   valueAnimator = ValueAnimator.ofObject(
                bezierEvaluator, startPoint, endPoint);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //这里是取贝塞尔曲线的回调的坐标
                Point pointF = (Point) valueAnimator.getAnimatedValue();
                int x = pointF.x;
                int y = pointF.y;
                Log.d("Computepower2Activity", "x=" + x);
                Log.d("Computepower2Activity", "y=" + y);
                //移动这个图片
                iv.setX(x);
                iv.setY(y);
                //记录上次x y
                tempX = x;
                tempY = y;
            }
        });

        valueAnimator.setDuration(6000);
        valueAnimator.start();





    }

    /**
     * 全透状态栏
     */
    protected void setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        }
    }
}
