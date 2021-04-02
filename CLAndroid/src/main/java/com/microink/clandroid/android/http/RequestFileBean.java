package com.microink.clandroid.android.http;

import java.io.Serializable;

/**
 * @author Cass
 * @version v1.0
 * @Date 3/30/21 12:04 PM
 *
 * 文件上传Bean
 */
public class RequestFileBean implements Serializable {
    private static final long serialVersionUID = 9202994027822354079L;

    private String name;
    private String fileName;
    private String filePath;

    public RequestFileBean() {
    }

    public RequestFileBean(String name, String fileName, String filePath) {
        this.name = name;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
