package common;

/**
 * @author: scyang
 * @date: 2019-09-03 21:32:43
 */
public interface StatusCode {
    public static final Integer ADDSUCCESS=200;
    public static final Integer ADDFALSE=201;
    public static final Integer DELETESUCCESS=300;
    public static final Integer DELETEFALSE=301;
    public static final Integer UPDATESUCCESS=400;
    public static final Integer UPDATEFALSE=401;
    public static final Integer QUERYSUCCESS=500;
    public static final Integer QUERYSFALSE=501;
    public static final Integer PAGESUCCESS=600;
    public static final Integer PAGEFALSE=601;
    public static final Integer UPLOADSUCCESS=700;
    public static final Integer UPLOADFALSE=701;
    public static final Integer LOGINSUCCESS=800;
    public static final Integer LOGINFALSE=801;
    public static final Integer ADD_LIST_SUCCESS=900;
    public static final Integer ADD_LIST_FALSE=901;
}
