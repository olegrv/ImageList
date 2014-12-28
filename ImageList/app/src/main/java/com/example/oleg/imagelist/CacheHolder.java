package com.example.oleg.imagelist;


import java.util.ArrayList;

public class CacheHolder {

    private static CacheHolder m_instance = null;
    private ArrayList<InstPicture> m_listCache = null;
    private int m_lastID = 0;
    private final int SIZE_CACHE = 128*2;
    private Thread m_threadCacheUpdate = null;

    private CacheHolder() {
        m_listCache = new ArrayList<InstPicture>();
    }

    public void StartCacheUpdate() {
        if(null == m_threadCacheUpdate)
        {
            m_threadCacheUpdate = new Thread(new CacheUpdate());
            m_threadCacheUpdate.start();
        }
        else if(!m_threadCacheUpdate.isAlive() )
        {
            m_threadCacheUpdate = new Thread(new CacheUpdate());
            m_threadCacheUpdate.start();
        }
    }

    public static synchronized CacheHolder getInstance() {
        if (null == m_instance) {
                m_instance = new CacheHolder();
        }
        return m_instance;
     }

    public synchronized InstPicture getPictureByID(int id)
    {
       InstPicture res = null;
        if(id>m_listCache.size()-1) {
            res =  FileHandler.getInstance().getPictureByID(id);
        }
        else {

            res = m_listCache.get(id);
        }

        if(m_lastID != id)
            StartCacheUpdate();
         m_lastID = id;

        res.setLock();
        return res;
    }

    private synchronized void loadNextPicture()
    {
            m_listCache.add(FileHandler.getInstance().getPictureByID(m_listCache.size()));
    }

    private synchronized void loadDataPictureByID(int id)
    {
        if(id<m_listCache.size()-1)
            if(!m_listCache.get(id).isDataLoaded())
                m_listCache.get(id).loadData();
    }

    private synchronized int  getPictureCount()
    {
        return m_listCache.size();
    }
    private synchronized void freeDataPictureByID(int id)
    {
        if(id<m_listCache.size()-1)
            if(m_listCache.get(id).isDataLoaded() && !m_listCache.get(id).isLock())
                m_listCache.get(id).freeData();
    }

    private class CacheUpdate implements Runnable {

        public void run() {

            int countCache = getPictureCount();
            int countFile = FileHandler.getInstance().getCount();
            int lastID = m_lastID;
            for(int i=countCache;i<countFile;i++)
            {
                loadNextPicture();
            }
            int newCountCache = getPictureCount();

            for(int i=0;i<newCountCache;i++)
            {
                if(i<lastID-SIZE_CACHE/2)
                    freeDataPictureByID(i);
                else if(i>lastID-SIZE_CACHE/2 && i<lastID+SIZE_CACHE/2)
                    loadDataPictureByID(i);
                else
                    freeDataPictureByID(i);
            }



        }
    }
}
