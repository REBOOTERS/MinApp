package com.engineer.android.mini.better.bitmap;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;

import com.engineer.android.mini.better.bitmap.internal.BaseGestureDetector;

/**
 * Created on 2021/5/1.
 *
 * @author rookie
 */
public class MoveGestureDetector extends BaseGestureDetector {

    private PointF mCurrentPointer;
    private PointF mPrePointer;
    //仅仅为了减少创建内存
    private final PointF mDeltaPointer = new PointF();

    //用于记录最终结果，并返回
    private final PointF mExtenalPointer = new PointF();

    private final OnMoveGestureListener mListenter;


    public MoveGestureDetector(Context context, OnMoveGestureListener listener) {
        super(context);
        mListenter = listener;
    }

    @Override
    protected void handleInProgressEvent(MotionEvent event) {
        int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
        switch (actionCode) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mListenter.onMoveEnd(this);
                resetState();
                break;
            case MotionEvent.ACTION_MOVE:
                updateStateByEvent(event);
                boolean update = mListenter.onMove(this);
                if (update) {
                    mPreMotionEvent.recycle();
                    mPreMotionEvent = MotionEvent.obtain(event);
                }
            default:
                break;

        }
    }

    @Override
    protected void handleStartProgressEvent(MotionEvent event) {
        int actionCode = event.getAction() & MotionEvent.ACTION_MASK;
        switch (actionCode) {
            case MotionEvent.ACTION_DOWN:
                resetState(); // 防止没有接收到CANCEL or UP ,保险起见
                mPreMotionEvent = MotionEvent.obtain(event);
                updateStateByEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mGestureInProgress = mListenter.onMoveBegin(this);
            default:
                break;
        }

    }

    protected void updateStateByEvent(MotionEvent event) {
        final MotionEvent prev = mPreMotionEvent;

        mPrePointer = caculateFocalPointer(prev);
        mCurrentPointer = caculateFocalPointer(event);

        //Log.e("TAG", mPrePointer.toString() + " ,  " + mCurrentPointer);

        boolean skipmovement = prev.getPointerCount() != event.getPointerCount();

        //Log.e("TAG", "mSkipThisMoveEvent = " + mSkipThisMoveEvent);
        mExtenalPointer.x = skipmovement ? 0 : mCurrentPointer.x - mPrePointer.x;
        mExtenalPointer.y = skipmovement ? 0 : mCurrentPointer.y - mPrePointer.y;

    }

    /**
     * 根据event计算多指中心点
     *
     * @param event
     * @return
     */
    private PointF caculateFocalPointer(MotionEvent event) {
        final int count = event.getPointerCount();
        float x = 0, y = 0;
        for (int i = 0; i < count; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }

        x /= count;
        y /= count;

        return new PointF(x, y);
    }


    public float getMoveX() {
        return mExtenalPointer.x;

    }

    public float getMoveY() {
        return mExtenalPointer.y;
    }


    public interface OnMoveGestureListener {
        boolean onMoveBegin(MoveGestureDetector detector);

        boolean onMove(MoveGestureDetector detector);

        void onMoveEnd(MoveGestureDetector detector);
    }

    public static class SimpleMoveGestureDetector implements OnMoveGestureListener {

        @Override
        public boolean onMoveBegin(MoveGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onMove(MoveGestureDetector detector) {
            return false;
        }

        @Override
        public void onMoveEnd(MoveGestureDetector detector) {
        }
    }

}