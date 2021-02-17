package com.pkk.notes.Models;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesModel implements Serializable {

    int id;
    String title;
    String body;
    Date date;
    byte[] image;

    public NotesModel(){
        id=-1;
        title = "";
        body = "";
    }

    public NotesModel(int id, String title, String body, Date date) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = date;
    }
    public NotesModel(String title, String body, Date date) {
        this.title = title;
        this.body = body;
        this.date = date;
    }

    public NotesModel(int id, String title, String body, String date) {
        this.id = id;
        this.title = title;
        this.body = body;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            this.date = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        return date;
    }

    public String getTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    public byte[] getImage() {
        return image;
    }
}
