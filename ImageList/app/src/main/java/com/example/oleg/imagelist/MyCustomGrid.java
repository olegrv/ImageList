package com.example.oleg.imagelist;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class MyCustomGrid extends View {
    private float m_distanceY = 0;
    private final boolean m_vertical = true;
    private GestureDetector gestureDetector = null;
    private float m_lastPoint = -1;
    private final int HGAP = 3;
    private final int WGAP = 5;
    private Context m_context=null;


    public MyCustomGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        m_context = context;
        gestureDetector = new GestureDetector(context, new MyGestureListener());
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {




        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            m_distanceY +=velocityY;
            return true;
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {

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

    @Override
    protected void onDraw(Canvas canvas) {

        int wcount = 0;
        int wsize = (m_vertical)?3:5;

        Paint paint=new Paint();
        int widthCanvas = canvas.getWidth();
        int heightCanvas = canvas.getHeight();
        float []Heights = {0,0,0,0,0};

        int countImages = FileHandler.getInstance().getCount();
        for(int i=0;i<countImages;i++)
        {

            float currentHeight = Heights[wcount];
            InstPicture instPicture = FileHandler.getInstance().getPictureByID(i);
            if(null == instPicture)
                continue;
            if(currentHeight <  m_distanceY-heightCanvas)
            {
                currentHeight+=instPicture.getHeight();
            }
            else  if(currentHeight >=  m_distanceY-heightCanvas && currentHeight < m_distanceY+heightCanvas*2) {

                if(!instPicture.isDataLoaded())
                    instPicture.loadData();

                float x_size = instPicture.getBitmap().getWidth();
                float y_size = instPicture.getBitmap().getHeight();
                canvas.drawBitmap(instPicture.getBitmap(), x_size * wcount + WGAP, currentHeight - m_distanceY, paint);
                canvas.drawText(String.valueOf(i), x_size * wcount + WGAP, currentHeight - m_distanceY+y_size/2, paint);
                currentHeight += y_size + HGAP;
                for (int j = instPicture.getTags().size() - 1; j != 0; j--)

                {
                    Rect bounds = new Rect();
                    paint.getTextBounds(instPicture.getTags().get(j), 0, instPicture.getTags().get(j).length(), bounds);
                    currentHeight += bounds.height() + HGAP;
                    canvas.drawText(instPicture.getTags().get(j), x_size * wcount + WGAP, currentHeight - m_distanceY, paint);

                }
            }
            else {
                if(wcount==(wsize-1))
                    break; // all row was showed
            }

            Heights[wcount] = currentHeight;
            wcount = (wcount==(wsize-1))?0:wcount+1;

        }
    }
}
