package com.example.oleg.imagelist;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class InetHandler  {

    private static InetHandler m_instance = null;

    private InetHandler(){};

    public static synchronized InetHandler getInstance()
    {
        if(null == m_instance)
        {
            m_instance  = new InetHandler();
        }
        return m_instance;
    }
    private Thread m_thread = null;
    public void Start(ImagesActivity activity) {
        if (null == m_thread) // we should start thread only once
        {
            m_thread = new Thread(new CheckInetAndGetToken(activity));
            m_thread.start();
        }
    }

    public void startFetchImages(String strToken) {
    }


    private class CheckInetAndGetToken implements Runnable {

    private ImagesActivity m_activity;
        public CheckInetAndGetToken(ImagesActivity activity) {
            m_activity = activity;

        }

        public void run() {
            long timeoutForSleepMilliseconds = 5000l;
            while(true) {
                if(IsInetConnected() == true)
                    break;

                try {
                    Thread.sleep(timeoutForSleepMilliseconds);
                } catch (InterruptedException e) {
                    ;
                }
            }
             AskUserAuth();

        }
        private void  AskUserAuth()
        {
            m_activity.runOnUiThread(new Runnable() {
                public void run() {
                    m_activity.showAuthActivity();
                }
            });
        }

        private boolean IsInetConnected()
        {
            String url = "http://instagram.com";
            int HttpResponseOK = 200;
            boolean result = false;
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

                int status = httpResponse.getStatusLine().getStatusCode();

                result = (HttpResponseOK==status)?true:false;

            } catch (Exception e) {
                ;// nothing to do
            }
            return result;
        }



    }

}
