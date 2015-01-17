package com.example.oleg.imagelist;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class ImagesActivity extends ActionBarActivity {

    private MyCustomGrid m_grid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        m_grid = (MyCustomGrid) findViewById(R.id.mg);

    }

    @Override
    protected void onStart() {
        super.onStart();
        InetHandler.getInstance().Start();
        FileHandler.getInstance().setContext(this);

    }

    public void invalidate() {
       if(null!=m_grid)
           m_grid.postInvalidate();
    }


}