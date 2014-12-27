package com.example.oleg.imagelist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Rect;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class InstPicture {
    private static final java.lang.String SPLIT_SYMBOL =";" ;
    private static final int HGAP = 3;
    private  Bitmap m_bitmap = null;
    private  ArrayList<String> m_tags = null;
    private int m_height = 0;
    private String m_strJpegFileName = null;
    private String m_strTagsFileName = null;

    public InstPicture( Bitmap bitmap, ArrayList<String> tags)
    {
        m_bitmap = bitmap;
        m_tags = tags;

        calcHeight();
    }

    public InstPicture(int height, String strJpegFileName, String strTagsFileName)
    {
        m_height = height;

        m_strTagsFileName = strTagsFileName;
        m_strJpegFileName = strJpegFileName;
    }

    public Bitmap getBitmap()
    {
        return m_bitmap;
    }

    private void calcHeight() {


        Paint paint = new Paint();

        int currentHeight = getBitmap().getHeight();
        currentHeight+=HGAP;
        for(int j=getTags().size()-1;j!=0;j--)

        {
            Rect bounds = new Rect();

            paint.getTextBounds(getTags().get(j), 0, getTags().get(j).length(), bounds);
            currentHeight+=bounds.height()+HGAP;

        }
        m_height = currentHeight;

    }

    public int getHeight()
    {
        return m_height;
    }


    public ArrayList<String> getTags()
    {
        return m_tags;
    }

    public boolean isDataLoaded()
    {
        return (null != m_bitmap && null != m_tags);
    }



    public void loadData() {

        try {
            Context context = FileHandler.getInstance().getContext();

            FileInputStream fisJpeg = context.openFileInput(m_strJpegFileName);
            FileInputStream fisTags = context.openFileInput(m_strTagsFileName);

            DataInputStream disTags = new DataInputStream(fisTags);


            Bitmap bitmap = BitmapFactory.decodeStream(fisJpeg);

            String str = disTags.readUTF();


            ArrayList<String> tags = new ArrayList<String>();

            String[] strs = str.split(SPLIT_SYMBOL);

            for (int i = 0; i < strs.length; i++)
                tags.add(strs[i]);

            m_bitmap = bitmap;
            m_tags = tags;
        }
        catch (IOException e) {
              ;
         }
    }


}