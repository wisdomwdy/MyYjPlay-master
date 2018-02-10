package chuangyuan.ycj.yjplay.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;

/**
 * Author：weidanyan
 * Email：1022664273@qq.com
 * Description：This is RoundBackgroudImageView
 * Time: 2017/12/15
 */

public class RoundBackgroudImageView extends ImageView {

    private Paint mPaint;
    private Context mContext;
    private int screenWidth;
    private int screenHeight;
    private int radial = 50;
    private boolean isNeedDraw;

    public RoundBackgroudImageView(Context context) {
        super(context);
    }

    public RoundBackgroudImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

    /**
     * 由于onDraw()方法会不停的绘制 所以需要定义一个初始化画笔方法 避免导致不停创建造成内存消耗过大
     */
    private void init() {
        if(mPaint != null){
            mPaint = null;
        }
        mPaint = new Paint();
        // 设置画笔为抗锯齿
        mPaint.setAntiAlias(true);
        // 设置颜色为红色
        mPaint.setColor(Color.GRAY);
        /**
         * 画笔样式分三种： 1.Paint.Style.STROKE：描边 2.Paint.Style.FILL_AND_STROKE：描边并填充
         * 3.Paint.Style.FILL：填充
         */
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        /**
         * 设置描边的粗细，单位：像素px 注意：当setStrokeWidth(0)的时候描边宽度并不为0而是只占一个像素
         */
        mPaint.setStrokeWidth(20);

        screenWidth = this.getWidth();
        screenHeight = this.getHeight();
        /**
         * 获取屏幕的宽高
         WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
         Display display = wm.getDefaultDisplay();
         screenWidth = display.getWidth();
         screenHeight = display.getHeight();*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        /**
         * 绘制圆环drawCircle的前两个参数表示圆心的XY坐标， 这里我们用到了一个工具类获取屏幕尺寸以便将其圆心设置在屏幕中心位置，
         * 第三个参数是圆的半径，第四个参数则为我们的画笔
         *
         */
        if(isNeedDraw){
            init();
            canvas.drawCircle(screenWidth/2 , screenHeight/2 , screenWidth*60/100, mPaint);
        }
       super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                isNeedDraw = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isNeedDraw = false;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPaint.reset();
                        postInvalidate();
                    }
                },1000);
            default:

                break;
        }
        return true;
    }
}
