package com.example.oleg.imagelist;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public  class Util {
    public static Bitmap getResizedBitmap(Bitmap bm, float scale) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = scale;
        float scaleHeight = scale;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return  Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

    }
}
