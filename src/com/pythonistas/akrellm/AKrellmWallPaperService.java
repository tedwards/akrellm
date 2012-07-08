package com.pythonistas.akrellm;

import java.lang.Float;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.SharedPreferences;
import android.graphics.Bitmap;  
import android.graphics.BitmapFactory;  
import android.graphics.BitmapShader;  
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;  
import android.graphics.Color;
import android.graphics.Matrix;  
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;  

import android.app.WallpaperManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.util.Log;

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
        private Paint lpaint = new Paint();
        private Paint lpaintBlur = new Paint();
        private Paint monthPaint = new Paint();
        private Paint monthPaintBlur = new Paint();
        private Paint dowPaint = new Paint();
        private Paint dowPaintBlur = new Paint();
        private Paint domPaint = new Paint();
        private Paint domPaintBlur = new Paint();
        private Paint tyPaint = new Paint();
        private Paint tyPaintBlur = new Paint();

        private RectF rect = new RectF();
        private boolean visible = true;
        private int maxNumber;
        private AKrellmTop top = new AKrellmTop();
        private AKrellmLoad sysload;
        private float syscpu;
        private float sysmem;
        private float sysbattery;
        private float systemp;
        private String date;
        private int width;
        private int height;
        private int pulseCounter;
        private int touchState = 0;
        private int color;
        private int red;
        private int green;
        private int blue;

        private Bitmap fillBMP;  
        private BitmapShader fillBMPshader;  
        private Matrix m = new Matrix();   
        private Path linePath = new Path();

        private float touchX;
        private float touchY;

        private static final String TAG = "AKrellm:";

        public AKrellmWallPaperEngine() {
            SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(AKrellmWallPaperService.this);
            maxNumber = Integer
                .valueOf(prefs.getString("numberOfCircles","4"));
            //touchEnabled = prefs.getBoolean("touch", false);
            color = prefs.getInt("color",0xff4a8aff);
            red = Color.red(color);
            green = Color.green(color);
            blue = Color.blue(color);

            pulseCounter=0;
            
            DisplayMetrics metrics = new DisplayMetrics();  
            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();  
            display.getMetrics(metrics);  
            width = display.getWidth() / 2;
            height = display.getHeight() / 2 ;

            paint.setAntiAlias(true);
            paint.setColor(Color.argb(148, 255, 255, 255));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(2f);
            paint.setTextSize(20);

            paintBlur.set(paint);
            paintBlur.setColor(Color.argb(135, red, green, blue));
            paintBlur.setStyle(Paint.Style.STROKE);
            paintBlur.setStrokeJoin(Paint.Join.ROUND);
            paintBlur.setStrokeWidth(5f);
            paintBlur.setTextSize(20);
            paintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));


            monthPaint.setAntiAlias(true);
            monthPaint.setColor(Color.argb(248, 255, 255, 255));
            monthPaint.setStyle(Paint.Style.STROKE);
            monthPaint.setStrokeJoin(Paint.Join.ROUND);
            monthPaint.setStrokeWidth(5f);
            monthPaint.setTextSize(60);

            monthPaintBlur.set(paint);
            monthPaintBlur.setColor(Color.argb(135, red, green, blue));
            monthPaintBlur.setStyle(Paint.Style.STROKE);
            monthPaintBlur.setStrokeJoin(Paint.Join.ROUND);
            monthPaintBlur.setStrokeWidth(15f);
            monthPaintBlur.setTextSize(60);
            monthPaintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));

            dowPaint.setAntiAlias(true);
            dowPaint.setColor(Color.argb(248, 255, 255, 255));
            dowPaint.setStyle(Paint.Style.STROKE);
            dowPaint.setStrokeJoin(Paint.Join.ROUND);
            dowPaint.setStrokeWidth(2f);
            dowPaint.setTextSize(75);

            dowPaintBlur.set(paint);
            dowPaintBlur.setColor(Color.argb(135, red, green, blue));
            dowPaintBlur.setStyle(Paint.Style.STROKE);
            dowPaintBlur.setStrokeJoin(Paint.Join.ROUND);
            dowPaintBlur.setStrokeWidth(5f);
            dowPaintBlur.setTextSize(75);
            dowPaintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));

            domPaint.setAntiAlias(true);
            domPaint.setColor(Color.argb(248, 255, 255, 255));
            domPaint.setStyle(Paint.Style.STROKE);
            domPaint.setStrokeJoin(Paint.Join.ROUND);
            domPaint.setStrokeWidth(10f);
            domPaint.setTextSize(180);

            domPaintBlur.set(paint);
            domPaintBlur.setColor(Color.argb(135, red, green, blue));
            domPaintBlur.setStyle(Paint.Style.STROKE);
            domPaintBlur.setStrokeJoin(Paint.Join.ROUND);
            domPaintBlur.setStrokeWidth(15f);
            domPaintBlur.setTextSize(180);
            domPaintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));

            tyPaint.setAntiAlias(true);
            tyPaint.setColor(Color.argb(248, 255, 255, 255));
            tyPaint.setStyle(Paint.Style.STROKE);
            tyPaint.setStrokeJoin(Paint.Join.ROUND);
            tyPaint.setStrokeWidth(2f);
            tyPaint.setTextSize(80);

            tyPaintBlur.set(paint);
            tyPaintBlur.setColor(Color.argb(135, red, green, blue));
            tyPaintBlur.setStyle(Paint.Style.STROKE);
            tyPaintBlur.setStrokeJoin(Paint.Join.ROUND);
            tyPaintBlur.setStrokeWidth(5f);
            tyPaintBlur.setTextSize(80);
            tyPaintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));


            bmPaint.setAntiAlias(true);
            //bmPaint.setAlpha(35);
            bmPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));

            cpaint.setAntiAlias(true);
            cpaint.setColor(Color.argb(248, 255, 255, 255));
            cpaint.setStyle(Paint.Style.STROKE);
            cpaint.setStrokeJoin(Paint.Join.ROUND);
            cpaint.setStrokeWidth(15f);
            //cpaint.setShader(fillBMPshader);


            cpaintBlur.set(paint);
            cpaintBlur.setColor(Color.argb(235, red, green, blue));
            cpaintBlur.setStyle(Paint.Style.STROKE);
            cpaintBlur.setStrokeJoin(Paint.Join.ROUND);
            cpaintBlur.setStrokeWidth(30f);
            cpaintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
            //cpaintBlur.setShader(fillBMPshader);


            lpaint.setAntiAlias(true);
            lpaint.setColor(Color.argb(248, 255, 255, 255));
            lpaint.setStyle(Paint.Style.STROKE);
            lpaint.setStrokeJoin(Paint.Join.ROUND);
            lpaint.setStrokeWidth(2f);
            //lpaint.setShader(fillBMPshader);


            lpaintBlur.set(paint);
            lpaintBlur.setColor(Color.argb(135, red, green, blue));
            lpaintBlur.setStyle(Paint.Style.STROKE);
            lpaintBlur.setStrokeJoin(Paint.Join.ROUND);
            lpaintBlur.setStrokeWidth(5f);
            lpaintBlur.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.NORMAL));

            //Initialize the bitmap object by loading an image from the resources folder  
            fillBMP = BitmapFactory.decodeResource(getResources(), R.drawable.akrellmpolar);  
            //Initialize the BitmapShader with the Bitmap object and set the texture tile mode  
            //fillBMPshader = new BitmapShader(fillBMP, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);  
            
            //Define the lines path
            getStats();
            defineLines();

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
            this.visible = false;
            handler.removeCallbacks(drawRunner);
            super.onSurfaceDestroyed(holder);
        }
        
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                         int width, int height) {
            this.width = width/2;
            this.height = height/2;
            this.defineLines();
            super.onSurfaceChanged(holder, format, width, height);
        }


        @Override
        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
            if (action.equals(WallpaperManager.COMMAND_TAP)) {
                handler.removeCallbacks(drawRunner);
                touchState = (touchState+1) %3;
                if (visible){
                    handler.post(drawRunner);
                }

            }
            return null;
        }
        
        // @Override
        // public void onTouchEvent(MotionEvent event) {
        //     if ( (event.getAction() == MotionEvent.ACTION_DOWN)) {
        //         handler.removeCallbacks(drawRunner);
        //         touchX = event.getX();
        //         touchY = event.getY();
        //     }
            
        //     else if ( (event.getAction() == MotionEvent.ACTION_UP)) {
        //         if ( (touchX==event.getX()) && (touchY==event.getY()) && (event.getEventTime()-event.getDownTime()<1250)) {
        //             touchState = (touchState+1) %3;
        //                 if (visible){
        //                     handler.post(drawRunner);
        //                 }
        //             }
        //     }
        //     super.onTouchEvent(event);
        // }
        
        private void draw() {
            SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    canvas.drawColor(Color.BLACK);
                    //draw the old values before updating
                    drawCircles(canvas);
                    getStats();
                    drawBackground(canvas);
                    drawCircles(canvas);
                    switch ( touchState ) {
                    case 0:
                        drawDate(canvas);
                        break;
                    case 1:
                        drawStats(canvas);
                        break;
                    case 2:
                        drawStats(canvas);
                        drawLines(canvas);
                        break;
                    }
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
            //systemp = 0;
            date = top.date();

            //try {
            //  Thread.sleep(1000);
            //} catch (Exception e) {}

        }
        
        private void drawBackground(Canvas canvas) {
            canvas.getMatrix().invert(m);
            canvas.drawBitmap(fillBMP, new Matrix(), bmPaint);
        }
        
        private void drawCircles(Canvas canvas) {
            //rect.set(width-400, height-400, width+400, height+400);
            //canvas.drawRect(rect, blackpaint);

            //draw load
            int step = 55;
            int pos = 20;
            rect.set(width-pos, height-pos, width+pos, height+pos);
            canvas.drawArc(rect, -90, 360*(sysload.one/2), false, cpaint);
            canvas.drawArc(rect, -95, 360*(sysload.one/2)+10, false, cpaintBlur);

            pos += step;
            rect.set(width-pos, height-pos, width+pos, height+pos);
            canvas.drawArc(rect, -90, 360*syscpu, false, cpaint);
            canvas.drawArc(rect, -95, 360*syscpu+10, false, cpaintBlur);
            
            pos += step;
            rect.set(width-pos, height-pos, width+pos, height+pos);
            canvas.drawArc(rect, -90, 360*sysmem, false, cpaint);
            canvas.drawArc(rect, -95, 360*sysmem+10, false, cpaintBlur);

            if (systemp > 0){
                pos += step;
                rect.set(width-pos, height-pos, width+pos, height+pos);
                canvas.drawArc(rect, -90, 360*(systemp/50000.0f), false, cpaint);
                canvas.drawArc(rect, -95, 360*(systemp/50000.0f)+10, false, cpaintBlur);
            }
            
            pos += step;
            rect.set(width-pos, height-pos, width+pos, height+pos);
            canvas.drawArc(rect, -90, 360*(sysbattery/100.0f), false, cpaint);
            canvas.drawArc(rect, -95, 360*(sysbattery/100.0f)+10, false, cpaintBlur);

        }

        private void defineLines() {
            Log.d(TAG,"Height is "+height);
            Log.d(TAG,"Width is "+width);
            int cypos = height-30;
            int cystep = -56;
            int cxpos = width+20;
            int cxstep = -5;

            int sypos = 135;
            int systep = 30;
            int sxpos = width+80;
            int sxstep = -30;
            
            linePath.reset();
            for (int x=1; x<=5; x++) {
                if ( (systemp!=0) || (x!=4)){
                    linePath.moveTo(100, sypos);
                    linePath.addCircle(98, sypos, 2, Path.Direction.CW);
                    linePath.lineTo(sxpos, sypos);
                    linePath.lineTo(cxpos, cypos);
                    linePath.addCircle(cxpos, cypos+2, 2, Path.Direction.CW);
                    cxpos += cxstep;
                    cypos += cystep;
                    sxpos += sxstep;
                    sypos += systep;
                }

            }

            
        }

        private void drawLines(Canvas canvas) {
            canvas.drawPath(linePath, lpaint);
            canvas.drawPath(linePath, lpaintBlur);
        }
        
        private void drawStats(Canvas canvas) {

            int pos = 130;
            int step = 30;

            String loadString = String.format("LOAD: %10.2f", sysload.one);
            canvas.drawText(loadString, 20,pos, paint);
            canvas.drawText(loadString, 20,pos, paintBlur);
            pos+=step;
            
            String cpuString = String.format("CPU: %11.2f", syscpu*100);
            canvas.drawText(cpuString, 20,pos, paint);
            canvas.drawText(cpuString, 20,pos, paintBlur);
            pos+=step;
            
            String memString = String.format("MEM: %11.2f", sysmem*100);
            canvas.drawText(memString, 20,pos, paint);        
            canvas.drawText(memString, 20,pos, paintBlur);
            pos+=step;

            if (systemp > 0){
                String tempString = String.format("TEMP: %10.2f", systemp/1000.0f);
                canvas.drawText(tempString, 20,pos, paint);
                canvas.drawText(tempString, 20,pos, paintBlur);
                pos+=step;
            }
            
            String battString = String.format("BATT: %10.2f", sysbattery);
            canvas.drawText(battString, 20,pos, paint);   
            canvas.drawText(battString, 20,pos, paintBlur);
            pos+=step;

        }

        private void drawDate(Canvas canvas) {
            Date now = new Date();
            Calendar calendar = new GregorianCalendar();
            Locale myLocale = Locale.getDefault();
            int pos=130;
            String month = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, myLocale).toUpperCase();
            for ( int i=0; i<month.length(); i++){
                canvas.drawText(month,i,i+1,40,pos, monthPaint);
                canvas.drawText(month,i,i+1,40,pos, monthPaintBlur);
                pos+=60;
            }
            
            String year = "" + calendar.get(Calendar.YEAR);
            canvas.drawText(year, 20, pos, tyPaint);
            canvas.drawText(year, 20, pos, tyPaintBlur);

            String dow = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, myLocale);
            canvas.drawText(dow, 100, 180, dowPaint);
            canvas.drawText(dow, 100, 180, dowPaintBlur);
            
            int x = calendar.get(Calendar.DAY_OF_MONTH);
            String dom = "";
            if (x<10){
                dom = "0"+x;
            }
            else{
                dom = ""+x;
            }
            canvas.drawText(dom, 200, 230, domPaint);
            canvas.drawText(dom, 200, 230, domPaintBlur);


            x = calendar.get(Calendar.HOUR_OF_DAY);
            String hour = "";
            if (x<10){
                hour = "0"+x;
            }
            else{
                hour = ""+x;
            }
            canvas.drawText(hour, 420, 130, tyPaint);
            canvas.drawText(hour, 420, 130, tyPaintBlur);

            x = calendar.get(Calendar.MINUTE);
            String minute = "";
            if (x<10){
                minute = "0"+x;
            }
            else{
                minute = ""+x;
            }
            canvas.drawText(minute, 420, 200, tyPaint);
            canvas.drawText(minute, 420, 200, tyPaintBlur);

        }
    }
}