package com.duanlei.catchcrazycat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.Vector;

/**
 * Author: duanlei
 * Date: 2016-01-11
 */
public class Playground extends SurfaceView implements View.OnTouchListener {

    /**
     * 矩阵的列数
     */
    private static final int COL = 10;
    /**
     * 矩阵的行数
     */
    private static final int ROW = 10;

    /**
     * 元素的宽度
     */
    private int dotWidth;


    /**
     * 默认添加的路障数量
     */
    private static final int BLOCKS = 15;

    private Dot matrix[][];
    private Dot cat;

    public Playground(Context context) {
        super(context);
        getHolder().addCallback(mCallback);

        matrix = new Dot[COL][ROW];
        for (int y = 0; y < ROW; y++) {
            for (int x = 0; x < COL; x++) {
                matrix[y][x] = new Dot(x, y);
            }
        }

        setOnTouchListener(this);
        initGame();
    }

    /**
     * 由于矩阵的行和列与二维数组的定义是反的，所以用一个方法获取
     *
     * @param x
     * @param y
     * @return
     */
    private Dot getDot(int x, int y) {
        return matrix[y][x];
    }

    /**
     * 判断点是否处于游戏的边界
     */
    private boolean isAtEdge(Dot d) {
        return d.getX() * d.getY() == 0 || d.getX() + 1 == COL || d.getY() + 1 == ROW;
    }

    /**
     * 根据方向得到相邻的点
     *
     * @param one
     * @param dir
     * @return
     */
    private Dot getNeighbour(Dot one, int dir) {

        switch (dir) {
            case 1:
                return getDot(one.getX() - 1, one.getY());
            case 2:
                if (one.getY() % 2 == 0) {
                    return getDot(one.getX() - 1, one.getY() - 1);
                } else {
                    return getDot(one.getX(), one.getY() - 1);
                }
            case 3:
                if (one.getY() % 2 == 0) {
                    return getDot(one.getX(), one.getY() - 1);
                } else {
                    return getDot(one.getX() + 1, one.getY() - 1);
                }
            case 4:
                return getDot(one.getX() + 1, one.getY());
            case 5:
                if (one.getY() % 2 == 0) {
                    return getDot(one.getX(), one.getY() + 1);
                } else {
                    return getDot(one.getX() + 1, one.getY() + 1);
                }
            case 6:
                if (one.getY() % 2 == 0) {
                    return getDot(one.getX() - 1, one.getY() + 1);
                } else {
                    return getDot(one.getX(), one.getY() + 1);
                }

            default:
                return new Dot(0, 0);
        }
    }

    /**
     * 得到点在指定方向上走到边界所需要的步数
     *
     * @param one
     * @param dir
     * @return
     */
    private int getDistance(Dot one, int dir) {

        int distance = 0;
        Dot ori = one, next;

        while (true) {
            next = getNeighbour(ori, dir);

            if (next.getStatus() == Dot.STATUS_ON) {
                return distance * -1;
            }
            if (isAtEdge(next)) {
                distance++;
                return distance;
            }
            distance++;
            ori = next;
        }
    }

    /**
     * cat移动到指定位置
     *
     * @param one 猫移动到的位置
     */
    private void MoveTo(Dot one) {
        one.setStatus(Dot.STATUS_IN);
        getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
        cat.setXY(one.getX(), one.getY());
    }

    /**
     * 猫的移动
     */
    private void move() {
        if (isAtEdge(cat)) {
            lose();
            return;
        }

        Vector<Dot> available = new Vector<>();
        for (int i = 1; i < 7; i++) {
            Dot n = getNeighbour(cat, i);

            if (n.getStatus() == Dot.STATUS_OFF) {
                available.add(n);
            }

            if (available.size() == 0) {
                win();
            } else {
                MoveTo(available.get(0));
            }
        }
    }

    /**
     * 游戏失败
     */
    private void lose() {
        Toast.makeText(getContext(), "Lose", Toast.LENGTH_SHORT).show();
    }

    /**
     * 游戏成功
     */
    private void win() {
        Toast.makeText(getContext(), "You Win!", Toast.LENGTH_SHORT).show();
    }

    private void redraw() {
        Canvas canvas = getHolder().lockCanvas();
        canvas.drawColor(Color.LTGRAY);

        //根据点的状态设置点的颜色
        Paint paint = new Paint();
        //抗锯齿
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);

        for (int y = 0; y < ROW; y++) {

            //奇数行和偶数行错位
            int offset = 0;
            if (y % 2 != 0) { //偶数行
                offset = dotWidth / 2;
            }

            for (int x = 0; x < COL; x++) {
                Dot one = getDot(x, y);
                switch (one.getStatus()) {
                    case Dot.STATUS_OFF:
                        paint.setColor(0xFFEEEEEE);
                        break;

                    case Dot.STATUS_IN:
                        paint.setColor(0xFFFF0000);
                        break;

                    case Dot.STATUS_ON:
                        paint.setColor(0xFFFFAA00);
                        break;
                    default:
                        break;
                }

                canvas.drawOval(new RectF(one.getX() * dotWidth + offset, one.getY() * dotWidth,
                        (one.getX() + 1) * dotWidth + offset, (one.getY() + 1) * dotWidth), paint);
            }
        }

        getHolder().unlockCanvasAndPost(canvas);
    }

    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            redraw();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            dotWidth = width / (COL + 1);
            redraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    };

    private void initGame() {
        //创建所有的点
        for (int y = 0; y < ROW; y++) {
            for (int x = 0; x < COL; x++) {
                matrix[y][x].setStatus(Dot.STATUS_OFF);
            }
        }

        //创建猫
        cat = new Dot(4, 5);
        getDot(4, 5).setStatus(Dot.STATUS_IN);

        //创建路障
        for (int i = 0; i < BLOCKS; ) {

            //随机获取路障的x，y坐标
            //只有当前的点是默认状态的时候，才设置状态，i才自加

            int x = (int) ((Math.random() * 1000) % COL);
            int y = (int) ((Math.random() * 1000) % ROW);

            if (getDot(x, y).getStatus() == Dot.STATUS_OFF) {
                getDot(x, y).setStatus(Dot.STATUS_ON);
                i++;
            }
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            int x, y;
            y = (int) (event.getY() / dotWidth);
            if (y % 2 == 0) { //奇数行
                x = (int) (event.getX() / dotWidth);
            } else {
                x = (int) ((event.getX() - dotWidth / 2) / dotWidth);
            }

            if (x + 1 > COL || y + 1 > ROW) { //点击元素的外部将游戏进行初始化
                initGame();
            } else if (getDot(x, y).getStatus() == Dot.STATUS_OFF) {
                getDot(x, y).setStatus(Dot.STATUS_ON);
                move();
            }

            redraw();
        }

        return true;
    }
}
