package com.example.pointingplayer;

import com.opencsv.CSVReader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Frame {

    private String beginTime;
    private String finishTime;

    private String state;

    public Frame(String beginTime, String finishTime, String state) {
        this.beginTime = beginTime;
        this.finishTime = finishTime;
        this.state = state;
    }


    public String getBeginTime() {
        return beginTime;
    }
    public String getFinishTime() {
        return finishTime;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return (""+this.getBeginTime()+","+this.getFinishTime()+","+this.getState());
    }
}
