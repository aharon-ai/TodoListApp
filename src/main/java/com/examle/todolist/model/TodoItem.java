package com.examle.todolist.model;

import java.time.LocalDate;

public class TodoItem {
    public String Title;
    public String bechreibung;
    public LocalDate fristDatum ;
    public boolean istGemacht;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getBechreibung() {
        return bechreibung;
    }

    public void setBechreibung(String bechreibung) {
        this.bechreibung = bechreibung;
    }

    public LocalDate getFristDatum() {
        return fristDatum;
    }

    public void setFristDatum(LocalDate fristDatum) {
        this.fristDatum = fristDatum;
    }

    public boolean isIstGemacht() {
        return istGemacht;
    }

    public void setIstGemacht(boolean istGemacht) {
        this.istGemacht = istGemacht;
    }
}
