package com.microink.clandroidutil;

import java.io.Serializable;

public class LoginResp implements Serializable {

    private String code;

    private String msg;

    private UserInfo result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public UserInfo getResult() {
        return result;
    }

    public void setResult(UserInfo result) {
        this.result = result;
    }


    //public String getResult() {
    //    return result;
    //}
    //
    //public void setResult(String result) {
    //    this.result = result;
    //}

    @Override
    public String toString() {
        return "LoginResp{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                '}';
    }
}
