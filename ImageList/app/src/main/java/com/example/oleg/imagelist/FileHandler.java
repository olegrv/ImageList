package com.example.oleg.imagelist;


import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FileHandler {

    private static FileHandler m_instance = null;
    private Context m_context = null;


    private FileHandler() {

    }

    private final int NOT_INIT_PICNUMBER = -1;
    private int m_nLastPictureNumber = NOT_INIT_PICNUMBER;
    private int m_nLastPictureNumberNew = 0;
    private ArrayList<Integer> m_index = null;


    public static synchronized FileHandler getInstance() {
        if (null == m_instance) {
            m_instance = new FileHandler();
        }
        return m_instance;
    }



    public synchronized void setContext(Context context) {
        m_context = context;

    }
    public Context getContext()
    {
        return  m_context;
    }

    private void loadIndex()
    {
        m_index = new ArrayList<Integer>();
        try {

            FileInputStream fis = m_context.openFileInput(Constants.INDEX_FILE_NAME);
            DataInputStream dis = new DataInputStream(fis);
            int count = dis.readInt();
            while(count>0) {
                m_index.add(dis.readInt());
                count--;
            }

            dis.close();
            fis.close();
        } catch (IOException e) {
            ;
        }

    }

    private void saveIndex()
    {

        try {


            FileOutputStream fos = m_context.openFileOutput(Constants.INDEX_FILE_NAME, Context.MODE_PRIVATE);

            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeInt(m_index.size());
            for(int i=0;i < m_index.size();i++)
                dos.writeInt(m_index.get(i));

            dos.close();
            fos.close();
        } catch (IOException e) {
            ;
        }

    }

    private void checkPictureNumber() {
        if (NOT_INIT_PICNUMBER == m_nLastPictureNumber) {
            loadLastPictureNumber();
            loadIndex();
        }


    }

    private void increaseLastPictureNumber() {

        m_nLastPictureNumberNew++;
        m_nLastPictureNumber++;
        writeLastPictureNumber();

    }

    private void updateIndex() {

        m_index.add(m_nLastPictureNumberNew,m_nLastPictureNumber);
        saveIndex();
    }




    private void writeLastPictureNumber() {
        try {


            FileOutputStream fos = m_context.openFileOutput(Constants.COUNT_FILE_NAME, Context.MODE_PRIVATE);

            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeInt(m_nLastPictureNumber);
            dos.close();
            fos.close();
        } catch (IOException e) {
            ;
        }


    }

    public synchronized String readLastTagNumber() {
        String str = "!";
        try {
            Context context = FileHandler.getInstance().getContext();


            FileInputStream fisTag = context.openFileInput(Constants.LAST_TAG_FILE_NAME);
            DataInputStream disTag = new DataInputStream(fisTag);

            str = disTag.readUTF();
        }
        catch (IOException e) {
            ;
        }
        return str;
    }

    public synchronized void writeLastTagNumber(String strTagNumber) {
        try {

            FileOutputStream fosTag = m_context.openFileOutput(Constants.LAST_TAG_FILE_NAME, Context.MODE_PRIVATE);
            DataOutputStream dosTag = new DataOutputStream(fosTag);


            dosTag.writeUTF(strTagNumber);

            dosTag.close();
            fosTag.close();
        }
        catch (IOException e) {
             ;
        }
    }

    private void loadLastPictureNumber()

    {

        try {

            FileInputStream fis = m_context.openFileInput(Constants.COUNT_FILE_NAME);
            DataInputStream dis = new DataInputStream(fis);

            m_nLastPictureNumber = dis.readInt();

            dis.close();
            fis.close();
        } catch (IOException e) {
            m_nLastPictureNumber = 0;
        }

    }

    public synchronized int getCount() {
        checkPictureNumber();
        return m_nLastPictureNumber;
    }

    public synchronized boolean addPicture(InstPicture instPicture) {
        boolean res = true;
        checkPictureNumber();
        try {

            String strJpegFileName = m_nLastPictureNumber  + Constants.EXT_JPEG;
            String strTagsFileName = m_nLastPictureNumber + Constants.EXT_TAGS;
            String strSizesFileName = m_nLastPictureNumber + Constants.EXT_SIZES;

            FileOutputStream fosJpeg = m_context.openFileOutput(strJpegFileName, Context.MODE_PRIVATE);
            FileOutputStream fosTags = m_context.openFileOutput(strTagsFileName, Context.MODE_PRIVATE);
            FileOutputStream fosSizes = m_context.openFileOutput(strSizesFileName, Context.MODE_PRIVATE);


            DataOutputStream dosJpeg = new DataOutputStream(fosJpeg);
            DataOutputStream dosTags = new DataOutputStream(fosTags);
            DataOutputStream dosSizes = new DataOutputStream(fosSizes);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            instPicture.getBitmap().compress(Bitmap.CompressFormat.JPEG, 75, stream);
            byte[] byteArray = stream.toByteArray();
            dosJpeg.write(byteArray);

            String strAllTags = "";

            for (int i = 0; i < instPicture.getTags().size(); i++)
                strAllTags += instPicture.getTags().get(i) + ((instPicture.getTags().size() == i) ? "" : Constants.SPLIT_TAGS_SYMBOL);

            dosTags.writeUTF(strAllTags);

            dosSizes.writeInt(instPicture.getHeight());
            dosSizes.writeInt(instPicture.getWidth());

            dosJpeg.close();
            dosTags.close();
            dosSizes.close();;

            fosJpeg.close();
            fosTags.close();
            fosSizes.close();


            updateIndex();
            CacheHolder.getInstance().reloadPictureById(m_nLastPictureNumberNew);
            increaseLastPictureNumber();

            ImagesActivity activity =  ((ImagesActivity)m_context);
            if(null!=activity)
                activity.invalidate();



        } catch (IOException e)
        {
            res = false;
        }

        return res;

    }

    public synchronized InstPicture getPictureByID(int index) {
        checkPictureNumber();
        InstPicture instPicture = null;
        int newIndex = m_index.get(index);
        try {

            String strJpegFileName = newIndex  + Constants.EXT_JPEG;
            String strTagsFileName = newIndex + Constants.EXT_TAGS;
            String strSizesFileName = newIndex + Constants.EXT_SIZES;

            FileInputStream fis = m_context.openFileInput(strSizesFileName);
            DataInputStream dis = new DataInputStream(fis);

            int height = dis.readInt();
            int width = dis.readInt();

            dis.close();
            fis.close();

          instPicture  =  new InstPicture(width,height,strJpegFileName,strTagsFileName);
        } catch (IOException e) {
            ;
        }
        return instPicture;

    }


    public synchronized int getLastCount() {

        return m_nLastPictureNumberNew;
    }
}
