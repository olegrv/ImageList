package com.example.oleg.imagelist;

import android.graphics.BitmapFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class InetHandler  {

    private static InetHandler m_instance = null;
    private final float scaleFactor = Constants.SCALE_IMAGE_FACTOR;

    private InetHandler(){};

    public static synchronized InetHandler getInstance()
    {
        if(null == m_instance)
        {
            m_instance  = new InetHandler();
        }
        return m_instance;
    }
    private Thread m_threadCheckInet = null;
    private Thread m_threadFetchImages = null;
    public void Start(ImagesActivity activity) {
        if (null == m_threadCheckInet) // we should start thread only once
        {
            m_threadCheckInet = new Thread(new CheckInetAndGetToken(activity));
            m_threadCheckInet.start();
        }
    }

    public void startFetchImages(String strToken) {

        if (null == m_threadFetchImages) // we should start thread only once
        {
            m_threadFetchImages = new Thread(new FetchImages(strToken));
            m_threadFetchImages.start();
        }
    }

    private class FetchImages implements Runnable {
        private String m_strToken = null;
        private String m_strUrl = null;
        private String m_strCount = "1";
        public FetchImages(String strToken) {
            m_strToken = strToken;
            m_strUrl = "https://api.instagram.com/v1/tags/"+Constants.HASH_TAG+"/media/recent?access_token="+m_strToken +"&count="+m_strCount+"&max_tag_id=0";


        }
        private  String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;
            return result;

        }

        private String getJSON( String url) throws IOException, java.lang.NullPointerException
        {
            HttpClient httpclient = new DefaultHttpClient();

            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            InputStream inputStream = httpResponse.getEntity().getContent();
            String strJSONText = null;
            if(inputStream != null)
                 strJSONText = convertInputStreamToString(inputStream);
            else
                throw new IOException();
            inputStream.close();
            return strJSONText;
        }

        private String getNextMaxTagID(String JSONText) throws JSONException
        {
            String strTagPagination = "pagination";
            String strTagMaxID = "next_max_tag_id";
            JSONObject jObject = new JSONObject(JSONText);

            return jObject.getJSONObject(strTagPagination).getString(strTagMaxID);

        }

        private String getNextURL(String JSONText) throws JSONException
        {
            String strTagPagination = "pagination";
            String strTagURL = "next_url";
            JSONObject jObject = new JSONObject(JSONText);

            return jObject.getJSONObject(strTagPagination).getString(strTagURL);

        }

        private InstPicture getPicture(String JSONText) throws JSONException, IOException, java.lang.NullPointerException
        {
            String strTagData = "data";
            String strTagImage = "images";
            String strTagResImage = "low_resolution";
            String strTagImageURL = "url";
            String strTagHashTags = "tags";
            String strTypeTag = "type";
            String strTypeResp = "image";

            JSONObject jObject = new JSONObject(JSONText);
            JSONArray jObjectData = jObject.getJSONArray(strTagData);
            assert(jObjectData.length()==1); // we get images one by one
            String strType = jObjectData.getJSONObject(0).getString(strTypeTag);
            if(strType.compareTo(strTypeResp)!=0)
                return null;
            String strImageUrl = jObjectData.getJSONObject(0).getJSONObject(strTagImage).getJSONObject(strTagResImage).getString(strTagImageURL);

            ArrayList<String> tags = new ArrayList<String>();
            try {
                JSONArray jObjectTags = jObjectData.getJSONObject(0).getJSONArray(strTagHashTags);
                for (int i = 0; i < jObjectTags.length(); i++) {
                    tags.add(jObjectTags.getString(i));
                }
            }
            catch (Exception e)
            {;}

            InputStream inputStream = null;
            android.graphics.Bitmap  bitmap = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(strImageUrl));
            inputStream = httpResponse.getEntity().getContent();
            bitmap = BitmapFactory.decodeStream(inputStream);

            InstPicture instPicture = new InstPicture(Util.getResizedBitmap(bitmap,scaleFactor),tags);

            return instPicture;


        }

        public void run() {
            String previousMaxTagID = FileHandler.getInstance().readLastTagNumber();
            String newMaxTagID = previousMaxTagID;
            String currentMaxTagID = null;
            String JSONText = null;
            InstPicture instPicture = null;


            try {
                JSONText = getJSON(m_strUrl);
                newMaxTagID = getNextMaxTagID(JSONText);
                currentMaxTagID = getNextMaxTagID(JSONText);
            }
            catch (Exception e)
            {
                newMaxTagID = "!";
                currentMaxTagID = "A";
            }

            while(FileHandler.getInstance().getLastCount()<Constants.MAX_FILES_ALLOWED_TO_LOAD_ONE_TIME) {

                try {

                     if(currentMaxTagID.compareTo(previousMaxTagID)<=0)
                          break;
                     instPicture = getPicture(JSONText);
                     if(null!=instPicture)
                     FileHandler.getInstance().addPicture(instPicture);
                     if(null != JSONText)
                           m_strUrl = getNextURL(JSONText);
                     JSONText = getJSON(m_strUrl);
                     currentMaxTagID = getNextMaxTagID(JSONText);
                }
               catch (Exception e)
               {;}
            }

            FileHandler.getInstance().writeLastTagNumber(newMaxTagID);

        }


    }



    private class CheckInetAndGetToken implements Runnable {


    private ImagesActivity m_activity;
        public CheckInetAndGetToken(ImagesActivity activity) {
            m_activity = activity;

        }

        public void run() {
            long timeoutForSleepMilliseconds = Constants.PERIOD_TO_TRAY_INET;
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
