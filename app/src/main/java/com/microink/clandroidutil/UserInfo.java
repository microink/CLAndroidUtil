package com.microink.clandroidutil;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private String display_name;

    private String access_token;

    private String token_type;

    private String expires_at;

    private int jahis_setting;

    private String type;

    private String pharmacy_id;

    private String chain_id;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPharmacy_id() {
        return pharmacy_id;
    }

    public void setPharmacy_id(String pharmacy_id) {
        this.pharmacy_id = pharmacy_id;
    }

    public String getChain_id() {
        return chain_id;
    }

    public void setChain_id(String chain_id) {
        this.chain_id = chain_id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getExpires_at() {
        return expires_at;
    }

    public void setExpires_at(String expires_at) {
        this.expires_at = expires_at;
    }

    public int getJahis_setting() {
        return jahis_setting;
    }

    public void setJahis_setting(int jahis_setting) {
        this.jahis_setting = jahis_setting;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "display_name='" + display_name + '\'' +
                ", access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expires_at='" + expires_at + '\'' +
                ", jahis_setting=" + jahis_setting +
                ", type='" + type + '\'' +
                ", pharmacy_id='" + pharmacy_id + '\'' +
                ", chain_id='" + chain_id + '\'' +
                '}';
    }
}
