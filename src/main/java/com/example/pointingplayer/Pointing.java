package com.example.pointingplayer;

import com.opencsv.CSVReader;
import javafx.scene.control.Alert;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class Pointing {

    private ArrayList<Frame> pointing = new ArrayList<>();

    public void setPointing(Path filePath) {

        String[] line;
        int num_line = 0;

        try (Reader reader = Files.newBufferedReader(filePath)) {
            CSVReader csvReader = new CSVReader(reader);
                while((line = csvReader.readNext()) != null) {
                    num_line++;
                    if (num_line == 1) {
                        line = csvReader.readNext();
                    }
                    pointing.add(new Frame(line[0], line[1], line[2]));
                }
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Time series caricate correttamente!");
            alert.showAndWait();
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Nessun file csv trovato. Time series non disponibili!");
            alert.showAndWait();
        }
    }

    public String getBeginTime(int index){
        return pointing.get(index).getBeginTime();
    }

    public String getFinishTime(int index) { return pointing.get(index).getFinishTime(); }

    public String getState(int index) { return pointing.get(index).getState(); }

    public void setState(int index, String state) {pointing.get(index).setState(state); }

    public void modifyRow(int index, Frame element) {pointing.set(index, element);}

    public Frame getRow(int index) { return pointing.get(index); }

    public int getSize(){
        return pointing.size();
    }

}
