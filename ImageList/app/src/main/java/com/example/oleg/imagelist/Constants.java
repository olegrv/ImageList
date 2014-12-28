package com.example.oleg.imagelist;

/**
 * Created by oleg on 12/28/14.
 */
public  class Constants {
    public final static int MAX_FILES_ALLOWED_TO_LOAD = 0x10000;
    public final static float SCALE_IMAGE_FACTOR = 0.7f;
    public final static String HASH_TAG = "umbrella";
    public static final long PERIOD_TO_TRAY_INET = 5000l;
    public static final int HGAP = 3;
    public static  final int WGAP = 15;
    public static final String SPLIT_TAGS_SYMBOL =";";
    public static final String LAST_TAG_FILE_NAME = "lastTag.txt";
    public static final String COUNT_FILE_NAME = "count.bin";
    public static final String EXT_JPEG = ".jpeg";
    public static final String EXT_SIZES = ".bin";
    public static final String EXT_TAGS = ".txt";
    public static final int PORTRET_COUNT = 3;
    public static final int LANSCAPE_COUNT = 5;
    public static final  int HGAP_IMAGES = 50;
    public static final int TICK_FLING_COUNT = 100;
    public static final long TIME_PERIOD_FLYING_MS = 2000;//ms
    public static final String AUTH_URL = "https://instagram.com/oauth/authorize/?client_id=bd3d78d339ee4096a6a8a831f40c5315&redirect_uri=http://localhost&response_type=token";
    public static final String AUTH_RET_URL = "http://localhost";
    public static final int SIZE_CACHE = 128*2;
}

