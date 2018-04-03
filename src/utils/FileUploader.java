package utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * An utility class for doing the whole stuffs when the files need to be uploaded
 * @author EuiJin.Ham
 * @version 1.0.0
 */
public class FileUploader {

    /**
     * Returns the extension of a file name.
     * Returns null when the file name does not have extension pattern.
     * @param fileName The file to extract the extension
     * @return the extension of a file
     */
    public static String getExtension(String fileName){
        if(fileName.lastIndexOf(".") == -1) return null;
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    /**
     * Returns the current timestamp as a string
     * @return String-typed timestamp
     */
    public static String getTimestamp(){
        return Long.toString(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * Returns formatted date (yyyyMMdd)
     * @return formatted date
     */
    public static String getDateString(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        return simpleDateFormat.format(new Date());
    }

    /**
     * Returns formatted date (yyyyMMddhhmmssSSS)
     * @return formatted date
     */
    public static String getFullDateString(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        return simpleDateFormat.format(new Date());
    }

    /**
     * Returns the timely-generated upload path
     * @param fileName The name of file
     * @return the timely-generated upload path
     */
    public static String getUploadPath(String fileName){
        return String.format("/%s/%s.%s", getDateString(), getFullDateString(), getExtension(fileName));
    }

    // TODO
    public static void forceCreateDirectory(String path){
        File file = new File(path);
        try {
            FileUtils.forceMkdir(file);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
