package com.example.oleg.imagelist;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MyCustomGrid extends View {
    private float m_distanceY = 0;
    private boolean m_vertical = true;
    private GestureDetector m_gestureDetector = null;
    private float m_lastPoint = -1;
    private final int HGAP = 3;
    private final int WGAP = 15;
    private Context m_context=null;
    private Thread m_threadFling = null;


    public MyCustomGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_context = context;
        m_gestureDetector = new GestureDetector(context, new MyGestureListener());
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {




        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            //final int countTick = 100;
            final int speedFactor = 7;
            //final int timeFactor = 2;
            float distanceY = velocityY /speedFactor;

            if(null == m_threadFling) {
                m_threadFling = new Thread(new FlingRunnable(distanceY));
                m_threadFling.start();
            } else if(!m_threadFling.isAlive()){
                m_threadFling = new Thread(new FlingRunnable(distanceY));
                m_threadFling.start();
                } else{
                return false;
            }


            return true;
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

        if(m_gestureDetector.onTouchEvent(event))
            return true;

         int action = event.getActionMasked();
        float currentY = event.getY();

        switch(action)
                {
        case MotionEvent.ACTION_UP:
            m_distanceY+= m_lastPoint-currentY;
            m_lastPoint = currentY;
            invalidate();
            break;
        case MotionEvent.ACTION_DOWN:
            m_lastPoint = currentY;
           break;
        case MotionEvent.ACTION_MOVE:
            m_distanceY+= m_lastPoint-currentY;
            m_lastPoint = currentY;
            invalidate();
            break;
        default:
            break;

            }

            return true;




    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            m_vertical = true;
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            m_vertical = false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int wcount = 0;
        int wsize = (m_vertical)?3:5;

        Paint paint=new Paint();
        int widthCanvas = canvas.getWidth();
        int heightCanvas = canvas.getHeight();
        float width_image = widthCanvas/wsize-WGAP;

        float []Heights = {0,0,0,0,0};

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int countImages = FileHandler.getInstance().getCount();
        float currentWidth = WGAP;
        for(int i=0;i<countImages;i++)
        {

            float currentHeight = Heights[wcount];

            InstPicture instPicture = CacheHolder.getInstance().getPictureByID(i);
            if(null == instPicture)
                continue;
            if(currentHeight <  m_distanceY-heightCanvas)
            {
                currentHeight+=instPicture.getHeight()*(width_image/instPicture.getWidth());
                currentWidth += width_image + WGAP;
            }
            else  if(currentHeight >=  m_distanceY-heightCanvas && currentHeight < m_distanceY+heightCanvas*3) {

                if(!instPicture.isDataLoaded())
                    instPicture.loadData();

                instPicture.draw(canvas,new RectF(currentWidth,currentHeight-m_distanceY,currentWidth+width_image,currentHeight-m_distanceY+instPicture.getHeight()*(width_image/instPicture.getWidth())),metrics);
                instPicture.freeLock();
                currentHeight+=instPicture.getHeight()+HGAP;
                currentWidth += width_image + WGAP;
            }
            else {
                if(wcount==(wsize-1)) {
                    break; // all row was showed
                }
            }

            Heights[wcount] = currentHeight+50;
            if(wcount==(wsize-1))
            {
                wcount = 0;
                currentWidth = WGAP;
            }
            else
                wcount++;

        }
    }

    private class FlingRunnable  implements Runnable{
        private  float m_localY = 0;
        private final int tickCount = 100;
        private final long timePeriodMS = 2000;//ms
        private final long sleepTimeMS  = timePeriodMS/tickCount;
        FlingRunnable(float distanceY)
        {
            m_localY = distanceY;
        }
        public void run() {
            for(int i=0;i<tickCount;i++) {
                m_distanceY -= m_localY / (tickCount+i);
                postInvalidate();
                try {
                    Thread.sleep(sleepTimeMS);
                } catch (InterruptedException e) {
                    ; // just ignore
                }
            }
        }
    }
}
