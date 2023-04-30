package com.example.deepspootyzic;


import java.io.Serializable;

public class Musique implements Serializable {

    private String titre;
    private String album;
    private String auteurs;
    private String url;

    private boolean active;

    public Musique(String titre, String auteurs, String album, String url)  {
        this.titre= titre;
        this.album = album;
        this.auteurs = auteurs;
        this.url = url;
        this.active= true;
    }



    public String getAlbum() {
        return album;
    }

    public String getAuteurs() {
        return auteurs;
    }

    public String getTitre() {
        return titre;
    }

    public String getUrl(){
        return url;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return this.titre + " | "   + this.auteurs +" | " + this.album + "";
    }


}
