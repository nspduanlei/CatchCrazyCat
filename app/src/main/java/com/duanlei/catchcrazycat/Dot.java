package com.duanlei.catchcrazycat;

/**
 * Author: duanlei
 * Date: 2016-01-11
 * 记录每一个点
 */
public class Dot {

    int x,y;
    int status;

    //cat不能走的位置
    public static final int STATUS_ON = 0;
    //默认状态
    public static final int STATUS_OFF = 1;
    //cat所在的位置
    public static final int STATUS_IN = 2;

    public Dot(int x, int y) {
        this.x = x;
        this.y = y;
        status = STATUS_OFF;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
