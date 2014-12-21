package com.example.oleg.imagelist;

import android.graphics.Bitmap;

import java.util.ArrayList;

public class InstPicture {
    private  Bitmap m_bitmap = null;
    private  ArrayList<String> m_tags = null;

    public InstPicture( Bitmap bitmap, ArrayList<String> tags)
    {
        m_bitmap = bitmap;
        m_tags = tags;
    }

    public Bitmap getBitmap()
    {
        return m_bitmap;
    }

    public ArrayList<String> getTags()
    {
        return m_tags;
    }
}
