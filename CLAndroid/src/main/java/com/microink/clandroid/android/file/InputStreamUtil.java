package com.microink.clandroid.android.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Cass
 * @version v1.0
 * @Date 4/19/21 5:04 PM
 *
 * 输入流工具类
 */
public class InputStreamUtil {

    /**
     * 输入流转换为文件
     * @param ins
     * @param file
     * @param readBytes 每次循环读取的字节数
     * @throws IOException
     */
    public static void inputStreamToFile(InputStream ins, File file, int readBytes)
            throws IOException {
        OutputStream os = new FileOutputStream(file);
        int bytesRead = 0;
        byte[] buffer = new byte[readBytes];
        while ((bytesRead = ins.read(buffer, 0, readBytes)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
    }
}
