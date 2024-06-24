package com.engineer.android.mini.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class CustomRoundedImageViewJava extends AppCompatImageView {

    private final Paint paint = new Paint();
    private final Path path = new Path();
    private float topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius;

    public CustomRoundedImageViewJava(Context context) {
        super(context);
        init();
    }

    public CustomRoundedImageViewJava(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomRoundedImageViewJava(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 定义绘制路径
        path.reset(); // 重置路径

        // 左上角开始
        path.moveTo(0, topLeftRadius); // 移动到左上角的圆角开始位置
        path.arcTo(new RectF(0, 0, topLeftRadius * 2, topLeftRadius * 2), 180, 90); // 左上角圆弧

        // 右上角
        path.lineTo(getWidth() - topRightRadius, 0); // 移动到右上角的开始位置
        path.arcTo(new RectF(getWidth() - topRightRadius * 2, 0, getWidth(), topRightRadius * 2), 270, 90); // 右上角圆弧

        // 右下角
        path.lineTo(getWidth(), getHeight() - bottomRightRadius); // 移动到右下角的开始位置
        path.arcTo(new RectF(getWidth() - bottomRightRadius * 2, getHeight() - bottomRightRadius * 2, getWidth(), getHeight()), 0, 90); // 右下角圆弧

        // 左下角
        path.lineTo(bottomLeftRadius, getHeight()); // 移动到左下角的开始位置
        path.arcTo(new RectF(0, getHeight() - bottomLeftRadius * 2, bottomLeftRadius * 2, getHeight()), 90, 90); // 左下角圆弧

        // 闭合路径
        path.close(); // 关闭路径
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();


        // 应用路径
        canvas.clipPath(path);

        // 绘制ImageView中的图片
        super.onDraw(canvas);

        // 恢复画布状态
        canvas.restore();
    }

    public void setCornerRadii(float topLeft, float topRight, float bottomLeft, float bottomRight) {
        topLeftRadius = topLeft;
        topRightRadius = topRight;
        bottomLeftRadius = bottomLeft;
        bottomRightRadius = bottomRight;
        invalidate(); // 重绘视图
    }
}
