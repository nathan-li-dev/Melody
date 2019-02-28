package com.gmail.iaminavir.melody;

import java.io.File;

public class Utils {


    public static boolean tryParseInt(String value){
        try{
            Integer.parseInt(value);
            return true;
        }
        catch (NumberFormatException e){
            return false;
        }
    }

    public static boolean fileExists(String name, File root) {
        File check = new File(root, name);
        return check.exists();
    }

    public static String getFileExtension(File file) {
        if(file.exists()) {
            String fileName = file.getName();
            if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
                return fileName.substring(fileName.lastIndexOf(".") + 1);
            else return "";
        }
        else{
            return "";
        }
    }

}
