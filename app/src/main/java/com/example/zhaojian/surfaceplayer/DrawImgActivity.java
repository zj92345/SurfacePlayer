package com.example.zhaojian.surfaceplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import java.io.IOException;
import java.io.InputStream;

public class DrawImgActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawImgView(this));

    }

    /**
     * 自定义SurfaceView
     */
    class DrawImgView extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder holder;
        private SimulatorThread myThread;

        public DrawImgView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
            holder = this.getHolder();
            holder.addCallback(this);
            myThread = new SimulatorThread(this);//创建一个模拟解码器
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            // TODO Auto-generated method stub
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            myThread.isRun = true;
            myThread.start();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            myThread.isRun = false;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        public void drawImg(Bitmap bitmap) {
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(bitmap, 0, 0, null);
            holder.unlockCanvasAndPost(canvas);
            holder.lockCanvas(new Rect(0, 0, 0, 0));
            holder.unlockCanvasAndPost(canvas);
        }

    }

    /**
     * 模拟解码器，50 ms 发送一张图片
     */
    class SimulatorThread extends Thread {
        public boolean isRun;
        int interval = 50;

        private DrawImgView myView;

        public SimulatorThread(DrawImgView view) {
            myView = view;
            isRun = true;
        }

        @Override
        public void run() {
            SurfaceHolder holder = myView.getHolder();
            while (isRun) {
                Canvas c = null;
                try {
                    synchronized (holder) {
                        String[] list_image = null;
                        try {
                            list_image = getAssets().list("");
                        } catch (IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        for (int i = 0; i < list_image.length; ++i) {
                            InputStream open = null;
                            try {
                                String temp = "" + list_image[i];
                                open = getAssets().open(temp);
                                Bitmap bitmap = BitmapFactory.decodeStream(open);
                                myView.drawImg(bitmap);
                                Thread.sleep(interval);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                if (open != null) {
                                    try {
                                        open.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) {
                        holder.unlockCanvasAndPost(c);
                    }
                }
            }
        }

    }
}