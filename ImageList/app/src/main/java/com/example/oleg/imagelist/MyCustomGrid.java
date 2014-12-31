package com.example.oleg.imagelist;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
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


    private int getCurrentRow(float []Heights)
    {
        int minHRow = 0;
        for(int i = 0; i<Heights.length;i++)
            if(Heights[i]<Heights[minHRow])
                minHRow = i;

    return minHRow;
}
    @Override
    protected void onDraw(Canvas canvas) {

        int wcount = 0;
        int wsize = (m_vertical)?Constants.PORTRET_COUNT:Constants.LANSCAPE_COUNT;


        int widthCanvas = canvas.getWidth();
        int heightCanvas = canvas.getHeight();
        float width_image = widthCanvas/wsize-Constants.WGAP;

        float []Heights = new float[wsize];
        for(int i = 0; i<Heights.length;i++)
            Heights[i]=0;


        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int countImages = FileHandler.getInstance().getCount();


        for(int i=0;i<countImages;i++)
        {

            wcount = getCurrentRow(Heights);
            float currentHeight = Heights[wcount];


            InstPicture instPicture = CacheHolder.getInstance().getPictureByID(i);
            if(null == instPicture)
                continue;
            if(currentHeight <  m_distanceY-heightCanvas)
            {
                currentHeight+=instPicture.getHeight()*(width_image/instPicture.getWidth());

            }
            else  if(currentHeight >=  m_distanceY-heightCanvas && currentHeight < m_distanceY+heightCanvas*3) {

                if(!instPicture.isDataLoaded())
                    instPicture.loadData();
                CacheHolder.getInstance().setLastID(i);
                RectF rect = new RectF(Constants.WGAP+wcount*width_image,currentHeight-m_distanceY,(wcount+1)*width_image,currentHeight-m_distanceY+instPicture.getHeight()*(width_image/instPicture.getWidth()));
                instPicture.draw(canvas,rect,metrics);
                instPicture.freeLock();
                currentHeight+=rect.height();

            }
            else {
                 int x = 0;
                    break;
                }


            Heights[wcount] = currentHeight+Constants.HGAP_IMAGES;
           /* if(wcount==(wsize-1))
            {
                wcount = 0;
                currentWidth = Constants.WGAP;
            }
            else
                wcount++;*/

        }
    }

    private class FlingRunnable  implements Runnable{
        private  float m_localY = 0;

        private final long sleepTimeMS  = Constants.TIME_PERIOD_FLYING_MS/Constants.TICK_FLING_COUNT;
        FlingRunnable(float distanceY)
        {
            m_localY = distanceY;
        }
        public void run() {
            for(int i=0;i<Constants.TICK_FLING_COUNT;i++) {
                m_distanceY -= m_localY / (Constants.TICK_FLING_COUNT+i);
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
