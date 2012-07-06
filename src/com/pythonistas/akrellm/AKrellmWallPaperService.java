package com.pythonistas.akrellm;

import java.lang.Float;

import android.content.SharedPreferences;
import android.graphics.Bitmap;  
import android.graphics.BitmapFactory;  
import android.graphics.BitmapShader;  
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;  
import android.graphics.Color;
import android.graphics.Matrix;  
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;  

import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


public class AKrellmWallPaperService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        return new AKrellmWallPaperEngine();
    }
    
    private class AKrellmWallPaperEngine extends Engine {
        private final Handler handler = new Handler();
        private final Runnable drawRunner = new Runnable() {
                @Override
                public void run() {
                    draw();
                }
            };
        private Paint paint = new Paint();
        private Paint paintBlur = new Paint();
        private Paint bmPaint = new Paint();
        private Paint cpaint = new Paint();
        private Paint cpaintBlur = new Paint();
        private RectF rect = new RectF();
        private boolean visible = true;
        private int maxNumber;
        private boolean touchEnabled;
        private AKrellmTop top = new AKrellmTop();
        private AKrellmLoad sysload;
        private float syscpu;
        private float sysmem;
        private float sysbattery;
        private float systemp;
        private int width;
        private int height;

        private Bitmap fillBMP;  
        private BitmapShader fillBMPshader;  
        private Matrix m = new Matrix();   


        public AKrellmWallPaperEngine() {
            SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(AKrellmWallPaperService.this);
            maxNumber = Integer
                .valueOf(prefs.getString("numberOfCircles","4"));
            touchEnabled = prefs.getBoolean("touch", false);

            paint.setAntiAlias(true);
            paint.setColor(Color.argb(148, 255, 255, 255));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(2f);
            paint.setTextSize(30);

            paintBlur.set(paint);
            paintBlur.setColor(Color.argb(135, 74, 138, 255));
            paintBlur.setStyle(Paint.Style.STROKE);
            paintBlur.setStrokeJoin(Paint.Join.ROUND);
            paintBlur.setStrokeWidth(5f);
            paintBlur.setTextSize(30);
            paintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));

            bmPaint.setAntiAlias(true);
            //bmPaint.setAlpha(35);
            bmPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));

            cpaint.setAntiAlias(true);
            cpaint.setColor(Color.argb(248, 255, 255, 255));
            cpaint.setStyle(Paint.Style.STROKE);
            cpaint.setStrokeJoin(Paint.Join.ROUND);
            cpaint.setStrokeWidth(30f);
            //cpaint.setShader(fillBMPshader);


            cpaintBlur.set(paint);
            cpaintBlur.setColor(Color.argb(235, 74, 138, 255));
            cpaintBlur.setStyle(Paint.Style.STROKE);
            cpaintBlur.setStrokeJoin(Paint.Join.ROUND);
            cpaintBlur.setStrokeWidth(40f);
            cpaintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
            //cpaintBlur.setShader(fillBMPshader);

            //Initialize the bitmap object by loading an image from the resources folder  
            fillBMP = BitmapFactory.decodeResource(getResources(), R.drawable.akrellmpolar);  
            //Initialize the BitmapShader with the Bitmap object and set the texture tile mode  
            //fillBMPshader = new BitmapShader(fillBMP, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);  

            handler.post(drawRunner);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;
            if (visible) {
                handler.post(drawRunner);
            } else {
                handler.removeCallbacks(drawRunner);
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            this.visible = false;
            handler.removeCallbacks(drawRunner);
        }
        
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                         int width, int height) {
            this.width = width/2;
            this.height = height/2;
            super.onSurfaceChanged(holder, format, width, height);
        }
        
        /*        @Override
        public void onTouchEvent(MotionEvent event) {
            if (touchEnabled) {
                float x = event.getX();
                float y = event.getY();
                SurfaceHolder holder = getSurfaceHolder();
                Canvas canvas = null;
                try {
                    canvas = holder.lockCanvas();
                    if (canvas != null){
                        canvas.drawColor(Color.BLACK);
                        circles.clear();
                        circles.add(new AKrellmPoint(String.valueOf(circles.size() + 1),
                                                     x,y));
                        drawCircles(canvas, circles);
                    }
                } finally {
                    if (canvas != null)
                        holder.unlockCanvasAndPost(canvas);
                }
                super.onTouchEvent(event);
            }
            }*/
        
        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    getStats();
                    drawBackground(canvas);
                    drawCircles(canvas);
                    drawStats(canvas);
                }
            } finally {
                if (canvas != null)
                    holder.unlockCanvasAndPost(canvas);
            }
            handler.removeCallbacks(drawRunner);
            if (visible){
                handler.postDelayed(drawRunner, 5000);
            }
        }

        private void getStats(){
            syscpu = top.cpu();
            sysload = top.load();
            sysmem = top.meminfo();
            sysbattery = top.battery();
            systemp = top.temp();

            try {
                Thread.sleep(1000);
            } catch (Exception e) {}

        }
        
        private void drawBackground(Canvas canvas) {
            canvas.getMatrix().invert(m);
            canvas.drawBitmap(fillBMP, new Matrix(), bmPaint);
        }
        
        private void drawCircles(Canvas canvas) {
            //rect.set(width-400, height-400, width+400, height+400);
            //canvas.drawRect(rect, blackpaint);

            //draw load
            rect.set(width-20, height-20, width+20, height+20);
            canvas.drawArc(rect, -90, 360*(sysload.one/2), false, cpaint);
            canvas.drawArc(rect, -90, 360*(sysload.one/2), false, cpaintBlur);

            rect.set(width-60, height-60, width+60, height+60);
            canvas.drawArc(rect, -90, 360*syscpu, false, cpaint);
            canvas.drawArc(rect, -90, 360*syscpu, false, cpaintBlur);
            
            rect.set(width-100, height-100, width+100, height+100);
            canvas.drawArc(rect, -90, 360*sysmem, false, cpaint);
            canvas.drawArc(rect, -90, 360*sysmem, false, cpaintBlur);
            
            rect.set(width-140, height-140, width+140, height+140);
            canvas.drawArc(rect, -90, 360*(systemp/50000.0f), false, cpaint);
            canvas.drawArc(rect, -90, 360*(systemp/50000.0f), false, cpaintBlur);
            
            rect.set(width-180, height-180, width+180, height+180);
            canvas.drawArc(rect, -90, 360*(sysbattery/100.0f), false, cpaint);
            canvas.drawArc(rect, -90, 360*(sysbattery/100.0f), false, cpaintBlur);

        }

        
        private void drawStats(Canvas canvas) {

            int pos = 100;
            
            String loadString = String.format("LOAD: %10.2f", sysload.one);
            canvas.drawText(loadString, 20,pos, paint);
            canvas.drawText(loadString, 20,pos, paintBlur);
            pos+=40;
            
            String cpuString = String.format("CPU: %11.2f", syscpu*100);
            canvas.drawText(cpuString, 20,pos, paint);
            canvas.drawText(cpuString, 20,pos, paintBlur);
            pos+=40;
            
            String memString = String.format("MEM: %11.2f", sysmem*100);
            canvas.drawText(memString, 20,pos, paint);        
            canvas.drawText(memString, 20,pos, paintBlur);
            pos+=40;

            if (systemp > 0){
                String tempString = String.format("TEMP: %10.2f", systemp/1000.0f);
                canvas.drawText(tempString, 20,pos, paint);
                canvas.drawText(tempString, 20,pos, paintBlur);
                pos+=40;
            }
            
            String battString = String.format("BATT: %10.2f", sysbattery);
            canvas.drawText(battString, 20,pos, paint);   
            canvas.drawText(battString, 20,pos, paintBlur);
            pos+=40;

        }
    }
}