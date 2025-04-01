package main.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;

public class Utils {
    public static final String REGEX_NUM = "^0|([1-9]\\d*)";
    public static final String CHARSET = "ASCII";

    public static String timestampToDatetime(long timeMillis){
        Timestamp timestamp = new Timestamp(timeMillis);
        Date date = new Date(timestamp.getTime());
        return date.toString();
    }

    public static String stringFromStream(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuilder.append(str).append('\n');
            }
            return stringBuilder.toString().trim();
        } else return "";
    }
}
