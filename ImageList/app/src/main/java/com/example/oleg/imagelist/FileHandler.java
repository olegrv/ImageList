package com.example.oleg.imagelist;


import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler {

    private static FileHandler m_instance = null;
    private Context m_context = null;

    private FileHandler() {

    }

    private final int NOT_INIT_PICNUMPER = -1;
    private int m_nLastPictureNumber = NOT_INIT_PICNUMPER;
    private final String m_strCountFileName = "count.bin";
    private final String m_strExtJpeg = ".jpeg";
    private final String m_strExtHeight = ".bin";
    private final String m_strExtTags = ".txt";
    private final String SPLIT_SYMBOL = ";";

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

    private void checkPictureNumber() {
        if (NOT_INIT_PICNUMPER == m_nLastPictureNumber)
            loadLastPictureNumber();

    }

    private void increaseLastPictureNumber() {
        m_nLastPictureNumber++;
        writeLastPictureNumber();
    }

    private void writeLastPictureNumber() {
        try {


            FileOutputStream fos = m_context.openFileOutput(m_strCountFileName, Context.MODE_PRIVATE);

            DataOutputStream dos = new DataOutputStream(fos);
            dos.writeInt(m_nLastPictureNumber);
            dos.close();
            fos.close();
        } catch (IOException e) {
            ;
        }


    }

    private void loadLastPictureNumber()

    {

        try {

            FileInputStream fis = m_context.openFileInput(m_strCountFileName);
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

    public synchronized void addPicture(InstPicture instPicture) {
        checkPictureNumber();
        try {

            String strJpegFileName = m_nLastPictureNumber  + m_strExtJpeg;
            String strTagsFileName = m_nLastPictureNumber + m_strExtTags;
            String strHeightName = m_nLastPictureNumber + m_strExtHeight;

            FileOutputStream fosJpeg = m_context.openFileOutput(strJpegFileName, Context.MODE_PRIVATE);
            FileOutputStream fosTags = m_context.openFileOutput(strTagsFileName, Context.MODE_PRIVATE);
            FileOutputStream fosHeight = m_context.openFileOutput(strHeightName, Context.MODE_PRIVATE);


            DataOutputStream dosJpeg = new DataOutputStream(fosJpeg);
            DataOutputStream dosTags = new DataOutputStream(fosTags);
            DataOutputStream dosHeight = new DataOutputStream(fosHeight);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            instPicture.getBitmap().compress(Bitmap.CompressFormat.JPEG, 75, stream);
            byte[] byteArray = stream.toByteArray();
            dosJpeg.write(byteArray);

            String strAllTags = "";

            for (int i = 0; i < instPicture.getTags().size(); i++)
                strAllTags += instPicture.getTags().get(i) + ((instPicture.getTags().size() == i) ? "" : SPLIT_SYMBOL);

            dosTags.writeUTF(strAllTags);

            dosHeight.writeInt(instPicture.getHeight());

            dosJpeg.close();
            dosTags.close();
            fosJpeg.close();
            fosTags.close();

            increaseLastPictureNumber();
        } catch (IOException e) {
            m_nLastPictureNumber = 0;
        }

    }

    public synchronized InstPicture getPictureByID(int index) {
        checkPictureNumber();
        InstPicture instPicture = null;
        try {

            String strJpegFileName = index + m_strExtJpeg;
            String strTagsFileName = index + m_strExtTags;
            String strHeightFileName = index + m_strExtHeight;

            FileInputStream fis = m_context.openFileInput(m_strCountFileName);
            DataInputStream dis = new DataInputStream(fis);

            int height = dis.readInt();

            dis.close();
            fis.close();

          instPicture  =  new InstPicture(height,strJpegFileName,strTagsFileName);
        } catch (IOException e) {
            ;
        }
        return instPicture;

    }



}
