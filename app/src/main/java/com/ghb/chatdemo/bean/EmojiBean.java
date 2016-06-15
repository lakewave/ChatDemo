package com.ghb.chatdemo.bean;

/**
 * TODO:
 */
public class EmojiBean {

    private int id;
    private String des;
    private String FileName;

    public EmojiBean() {
    }

    public EmojiBean(int id) {
        this.id = id;
    }

    public EmojiBean(int id, String des, String fileName) {
        this.id = id;
        this.des = des;
        FileName = fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }
}
