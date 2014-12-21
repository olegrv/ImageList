package com.example.oleg.imagelist;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

    ;
    private final int NOT_INIT_PICNUMPER = -1;
    private int m_nLastPictureNumber = NOT_INIT_PICNUMPER;
    private final String m_strCountFileName = "count.bin";
    private final String m_strExtJpeg = ".jpeg";
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
            //fos.getChannel().size();

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

            String strJpegFileName = (m_nLastPictureNumber + 1) + m_strExtJpeg;
            String strTagsFileName = (m_nLastPictureNumber + 1) + m_strExtTags;

            FileOutputStream fosJpeg = m_context.openFileOutput(strJpegFileName, Context.MODE_PRIVATE);
            FileOutputStream fosTags = m_context.openFileOutput(strTagsFileName, Context.MODE_PRIVATE);


            DataOutputStream dosJpeg = new DataOutputStream(fosJpeg);
            DataOutputStream dosTags = new DataOutputStream(fosTags);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            instPicture.getBitmap().compress(Bitmap.CompressFormat.JPEG, 75, stream);
            byte[] byteArray = stream.toByteArray();
            dosJpeg.write(byteArray);

            String strAllTags = "";

            for (int i = 0; i < instPicture.getTags().size(); i++)
                strAllTags += instPicture.getTags().get(i) + ((instPicture.getTags().size() == i) ? "" : SPLIT_SYMBOL);

            dosTags.writeUTF(strAllTags);

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
        InstPicture instPicture = null;
        try {

            String strJpegFileName = index + m_strExtJpeg;
            String strTagsFileName = index + m_strExtTags;

            FileInputStream fisJpeg = m_context.openFileInput(strJpegFileName);
            FileInputStream fisTags = m_context.openFileInput(strTagsFileName);


            DataInputStream disTags = new DataInputStream(fisTags);


            Bitmap bitmap = BitmapFactory.decodeStream(fisJpeg);

            String str = disTags.readUTF();


            ArrayList<String> tags = new ArrayList<String>();

            String [] strs = str.split(SPLIT_SYMBOL);

            for(int i =0; i<strs.length;i++)
                tags.add(strs[i]);

          instPicture  =  new InstPicture(bitmap,tags);
        } catch (IOException e) {
            ;
        }
        return instPicture;

    }

}
