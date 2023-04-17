package com.example.pointingplayer;

import com.opencsv.CSVWriter;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import static com.example.pointingplayer.Application.filename;
import static java.lang.Float.parseFloat;

public class Controller implements Initializable {
    @FXML
    private MediaView mediaView;
    private MediaPlayer mediaPlayer;
    private String filePath;
    @FXML
    private Button playButton;
    @FXML
    private Button stopButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button acceptButton;
    @FXML
    private Button declineButton;
    @FXML Button saveButton;
    @FXML
    private Slider slider;
    @FXML
    private Slider seekSlider;

    @FXML
    private Label lbTime;

    @FXML
    private Label lbTextPointing;
    @FXML
    private Label lbPointing;
    @FXML
    private Label lbState;
    @FXML
    private Button repeatButton;
    @FXML
    private Label labelVolume;
    @FXML
    private ImageView volumeImg;
    @FXML
    private Button slowButton;
    private int pointingCounter;
    private boolean isPressed = false;
    private boolean isPaused = false;
    private boolean isSaved = true;

    private boolean isSlowed = false;
    private String pathCsv;
    public Pointing pointing;

//    @FXML
//    private void handleButtonAction(ActionEvent event) {
//        if(filename != null ) {
//
//
//    }

    private String colorSlider(){
        String initialTime[] = new String[pointing.getSize()];
        String finishTime[] = new String[pointing.getSize()];
        String styles = "-track-color: linear-gradient(from 0% 0% to 100% 0%, white ";
        for(int i = 0; i < pointing.getSize(); i++){
            float beginTime = (float) (((parseFloat(pointing.getBeginTime(i))) * 1000 / mediaPlayer.getTotalDuration().toMillis())*100);
            BigDecimal beginTime_bd = new BigDecimal(beginTime).setScale(3, RoundingMode.HALF_UP);
            String beginTime_ts = beginTime_bd.toString() + "%";
            initialTime[i] = beginTime_ts;

            float FinishTime = (float) (((parseFloat(pointing.getFinishTime(i))) * 1000 / mediaPlayer.getTotalDuration().toMillis())*100);
            BigDecimal FinishTime_bd = new BigDecimal(FinishTime).setScale(3, RoundingMode.HALF_UP);
            String FinishTime_ts = FinishTime_bd.toString() + "%";
            finishTime[i] = FinishTime_ts;
        }

        for(int i = 0; i < initialTime.length; i++){
            if(i == initialTime.length - 1){
                styles += ""+initialTime[i]+", blue "+initialTime[i]+", blue "+finishTime[i]+", white "+finishTime[i]+")";
            } else {
                styles += ""+initialTime[i]+", blue "+initialTime[i]+", blue "+finishTime[i]+", white "+finishTime[i]+", white ";
            }

        }
//        System.out.println(initialTime[32]);
//        System.out.println(finishTime[32]);
//        System.out.println(styles);
        return styles;
    }
    @FXML
    private void chooseCSV(){
        try {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Select a File (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(filter);
            File file = fileChooser.showOpenDialog(null);
            this.pathCsv = file.getPath();
            pointing = new Pointing();
            this.pointingCounter = -1;
            pointing.setPointing(Path.of(this.pathCsv));
            seekSlider.setValue(0);
            mediaPlayer.seek(new Duration(0));
            lbPointing.setText("" + (this.pointingCounter+1) +"/"+ pointing.getSize());
            seekSlider.setStyle(colorSlider());
        } catch (NullPointerException ignored){}
    }
    @FXML
    private void pauseVideo(ActionEvent event){ mediaPlayer.pause(); }

    @FXML
    private void playVideo(ActionEvent event){
        try{
            MediaPlayer.Status status = mediaPlayer.getStatus();

            if (status == MediaPlayer.Status.PLAYING){
                lbTextPointing.setText("");
                mediaPlayer.pause();
                setIconPlay();
            } else {
                lbTextPointing.setText("");
                mediaPlayer.play();
                setIconPause();
            }
        } catch (Exception ignored){}

    }

//    @FXML
//    private void slowVideo(ActionEvent event){
//        if (!this.isSlowed){
//            mediaPlayer.setRate(0.5);
//            this.isSlowed = true;
//            slowButton.setText("Slow ON");
//        } else {
//            mediaPlayer.setRate(1);
//            this.isSlowed = false;
//            slowButton.setText("Slow OFF");
//        }
//    }

    @FXML
    private void stopVideo(ActionEvent event){
        try{
            mediaPlayer.stop();
            setIconPlay();
            this.pointingCounter = -1;
            lbPointing.setText("0/" + (pointing.getSize()));
            lbState.setText("");
            lbTextPointing.setText("");
        } catch (Exception ignored){}

    }

    @FXML
    private void prevPointing(ActionEvent event) {
        try{
            float prev;
            System.out.println("Prev pointing");

            if ( this.pointingCounter > 0 ){
                this.pointingCounter--;
                prev = parseFloat(pointing.getBeginTime(this.pointingCounter));
                mediaPlayer.seek(Duration.seconds(prev));
                seekSlider.setValue(prev);
                lbPointing.setText("" + (this.pointingCounter+1) +"/"+ pointing.getSize());
                lbState.setText(""+ pointing.getState(this.pointingCounter));
                this.isPressed = true;
                mediaPlayer.play();
                setIconPause();
                System.out.println(this.pointingCounter);
                System.out.println(prev);
            }
//            else{
//                this.pointingCounter = 0;
//            }
//            prev = parseFloat(pointing.getBeginTime(this.pointingCounter));
//            mediaPlayer.seek(Duration.seconds(prev));
//            seekSlider.setValue(prev);
//            lbPointing.setText("" + (this.pointingCounter+1) +"/"+ pointing.getSize());
//            lbState.setText(""+ pointing.getState(this.pointingCounter));
//            this.isPrev = true;
//            mediaPlayer.play();
//            setIconPause();
//            System.out.println(this.pointingCounter);
//            System.out.println(prev);
        } catch (Exception ignored){}
    }


    @FXML
    private void nextPointing(ActionEvent event){
        try{
            float next;
            System.out.println("Next pointing");

            if ( this.pointingCounter < pointing.getSize() - 1 ){
                this.pointingCounter++;
                next = parseFloat(pointing.getBeginTime(this.pointingCounter));
                mediaPlayer.seek(Duration.seconds(next));
                seekSlider.setValue(next);
                lbPointing.setText("" + (this.pointingCounter+1) +"/"+ pointing.getSize());
                lbState.setText(""+ pointing.getState(this.pointingCounter));
                this.isPressed = true;
                mediaPlayer.play();
                setIconPause();
                System.out.println(this.pointingCounter);
                System.out.println(next);
            }
            else if ( this.pointingCounter == pointing.getSize() -1 ) {
                next = parseFloat(pointing.getBeginTime(this.pointingCounter));
                mediaPlayer.seek(Duration.seconds(next));
                seekSlider.setValue(next);
                lbPointing.setText("" + (this.pointingCounter+1) +"/"+ pointing.getSize());
                lbState.setText(""+ pointing.getState(this.pointingCounter));
                this.isPressed = true;
                System.out.println(this.pointingCounter);
                System.out.println(next);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Lettura pointing completata!");
                alert.showAndWait();
             }
        } catch (Exception ignored){}
    }


    @FXML
    public void exitVideo(ActionEvent event){
        System.out.println(this.isSaved);
        if(this.isSaved)
            System.exit(0);
        else{
            Alert alert = new Alert(Alert.AlertType.WARNING, "Salvare le modifiche prima di uscire?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            Optional<ButtonType> option = alert.showAndWait();
            if(option.get() == ButtonType.YES){
                saveClicked(event);
                System.exit(0);
            } else if(option.get() == ButtonType.NO){
                System.exit(0);
            }
        }
    }

    @FXML
    private void acceptClicked(ActionEvent event){
        try{
            pointing.setState(this.pointingCounter, "POINTING");
            pointing.modifyRow(this.pointingCounter, new Frame(pointing.getBeginTime(this.pointingCounter), pointing.getFinishTime(this.pointingCounter), pointing.getState(this.pointingCounter)));
            this.isSaved = false;
            lbState.setText(""+ pointing.getState(this.pointingCounter));
            System.out.println(pointing.getRow(this.pointingCounter));
            nextPointing(event);
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Funzione non disponibile.");
            alert.showAndWait();
        }

    }

    @FXML
    private void declineClicked(ActionEvent event){
        try{
            pointing.setState(this.pointingCounter, "NO POINTING");
            pointing.modifyRow(this.pointingCounter, new Frame(pointing.getBeginTime(this.pointingCounter), pointing.getFinishTime(this.pointingCounter), pointing.getState(this.pointingCounter)) );
            this.isSaved = false;
            lbState.setText(""+ pointing.getState(this.pointingCounter));
            System.out.println(pointing.getRow(this.pointingCounter));
            nextPointing(event);
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Funzione non disponibile.");
            alert.showAndWait();
        }

    }

    @FXML
    private void repeatClicked(ActionEvent event){
        if(!this.isPressed && this.isPaused){
            this.isPressed = true;
            mediaPlayer.seek(Duration.seconds(parseFloat(pointing.getBeginTime(this.pointingCounter))));
            seekSlider.setValue(parseFloat(pointing.getBeginTime(this.pointingCounter)));
            mediaPlayer.play();
            setIconPause();
        }
    }

    @FXML
    private void clicked25(ActionEvent event){
        mediaPlayer.setRate(0.25);
    }

    @FXML
    private void clicked50(ActionEvent event){
        mediaPlayer.setRate(0.50);
    }

    @FXML
    private void clicked75(ActionEvent event){
        mediaPlayer.setRate(0.75);
    }

    @FXML
    private void clicked1(ActionEvent event){
        mediaPlayer.setRate(1);
    }
    @FXML
    private void saveClicked(ActionEvent event) {
        try{

            CSVWriter writer = new CSVWriter(
                    new OutputStreamWriter(new FileOutputStream(this.pathCsv),
                            StandardCharsets.UTF_8), ',', '\0', '"', "\n");

            System.out.println(pointing.getSize());
            String[] headers = {"begin_time", "finish_time", "state"};
            writer.writeNext(headers);
            for(int i = 0; i < pointing.getSize(); i++){
                String[] row = {pointing.getBeginTime(i),pointing.getFinishTime(i),pointing.getState(i)};
                writer.writeNext(row);
            }
            writer.close();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Salvataggio completato!");
            alert.showAndWait();
            this.isSaved = true;
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Impossibile salvare il file!");
            alert.showAndWait();
        }


    }

    @FXML
    private void aboutClicked(ActionEvent event){
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Video player per riconoscimento della gesture pointing.");
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("qui");
        setIconPlay();
        setIconStop();
        setIconPrevPointing();
        setIconNextPointing();
        setIconAccept();
        setIconDecline();
        setIconSave();
        setIconVolume();
        setIconRepeat();
        filePath = "file:/C:/Users/giand/Desktop/webapp/video/" + filename;
        System.out.println(filePath);
        this.isSaved = true;
        Media media = new Media(filePath);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        DoubleProperty width = mediaView.fitWidthProperty();
        DoubleProperty height = mediaView.fitHeightProperty();

        width.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
        height.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));

        this.pathCsv = ("C:/Users/giand/Desktop/webapp/clustering/final/"+filename.replace(".mp4", ".csv"));
//            this.setPathCsv("C:/Users/giand/Desktop/children-detection/clustering/final/"+fileName.replace(".mp4", ".csv"));
        pointing = new Pointing();
        this.pointingCounter = -1;
        pointing.setPointing(Path.of(this.pathCsv));

        lbPointing.setText("0/" + pointing.getSize());
        lbState.setText("");

        slider.setValue(mediaPlayer.getVolume() * 100);
        seekSlider.setStyle(colorSlider());

        slider.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                mediaPlayer.setVolume(slider.getValue()/100);
                if(mediaPlayer.getVolume() != 0.0){
                    setIconVolume();
                } else {
                    setIconVolumeSilent();
                }
            }
        });

        mediaPlayer.currentTimeProperty().addListener((observableValue, duration, t1) -> {

            double total = mediaPlayer.getTotalDuration().toMillis();
            double current = mediaPlayer.getCurrentTime().toMillis();
            seekSlider.setMax(total);
            seekSlider.setValue(current);


            lbTime.setText(getTimeString(current) + "/" + getTimeString(total));

            try{
                if(this.isPressed){
                    lbTextPointing.setText("Pointing in corso..");
                    lbTextPointing.setStyle("-fx-text-fill: green");
                    double seconds = mediaPlayer.getCurrentTime().toSeconds();
                    double finish = Double.parseDouble(pointing.getFinishTime(pointingCounter));

                    BigDecimal bd = new BigDecimal(seconds).setScale(1, RoundingMode.HALF_UP);
                    double currentSeconds = bd.doubleValue();
                    BigDecimal bd1 = new BigDecimal(finish).setScale(1,RoundingMode.HALF_UP);
                    double finishSeconds = bd1.doubleValue();

                    if(currentSeconds == finishSeconds){
                        lbTextPointing.setText("Fine pointing");
                        lbTextPointing.setStyle("-fx-text-fill: red");
                        mediaPlayer.pause();
                        setIconPlay();
                        this.isPaused = true;
                        this.isPressed = false;
                    }
                }
            } catch (Exception ignored){
            }
        });


        seekSlider.valueProperty().addListener(ov -> {
            if (seekSlider.isValueChanging()) {
                mediaPlayer.seek(new Duration(seekSlider.getValue()));
            }
        });

        seekSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mouseEvent.getTarget();
                mediaPlayer.seek(new Duration(seekSlider.getValue()));
            }
        });

        seekSlider.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                double total = mediaPlayer.getTotalDuration().toMillis();
                double current = mediaPlayer.getCurrentTime().toMillis();
                seekSlider.setMax(total);
                seekSlider.setValue(current);
                lbTime.setText(getTimeString(current) + "/" + getTimeString(total));
            }
        });

        mediaPlayer.play();
        setIconPause();
    }


    private void setIconPlay(){
        Image imagePlay = new Image(new File("C:/Users/giand/IdeaProjects/PointingPlayer/src/main/resources/icons/play.png").toURI().toString());
        ImageView ivPlay = new ImageView(imagePlay);
        ivPlay.setFitWidth(25);
        ivPlay.setFitHeight(25);
        playButton.setGraphic(ivPlay);
    }

    private void setIconPause(){
        Image imagePause = new Image(new File("C:/Users/giand/IdeaProjects/PointingPlayer/src/main/resources/icons/pause.png").toURI().toString());
        ImageView ivPause = new ImageView(imagePause);
        ivPause.setFitWidth(25);
        ivPause.setFitHeight(25);
        playButton.setGraphic(ivPause);
    }

    private void setIconStop(){
        Image imageStop = new Image(new File("C:/Users/giand/IdeaProjects/PointingPlayer/src/main/resources/icons/stop.png").toURI().toString());
        ImageView ivStop = new ImageView(imageStop);
        ivStop.setFitWidth(25);
        ivStop.setFitHeight(25);
        stopButton.setGraphic(ivStop);
    }

    private void setIconPrevPointing(){
        Image imagePrev = new Image(new File("C:/Users/giand/IdeaProjects/PointingPlayer/src/main/resources/icons/prev.png").toURI().toString());
        ImageView ivPrev = new ImageView(imagePrev);
        ivPrev.setFitWidth(25);
        ivPrev.setFitHeight(25);
        prevButton.setGraphic(ivPrev);
    }

    private void setIconNextPointing(){
        Image imageNext = new Image(new File("C:/Users/giand/IdeaProjects/PointingPlayer/src/main/resources/icons/next.png").toURI().toString());
        ImageView ivNext = new ImageView(imageNext);
        ivNext.setFitWidth(25);
        ivNext.setFitHeight(25);
        nextButton.setGraphic(ivNext);
    }

    private void setIconAccept(){
        Image imagePlay = new Image(new File("C:/Users/giand/IdeaProjects/PointingPlayer/src/main/resources/icons/accept.png").toURI().toString());
        ImageView ivPlay = new ImageView(imagePlay);
        ivPlay.setFitWidth(25);
        ivPlay.setFitHeight(25);
        acceptButton.setGraphic(ivPlay);
    }

    private void setIconDecline(){
        Image imagePlay = new Image(new File("C:/Users/giand/IdeaProjects/PointingPlayer/src/main/resources/icons/remove.png").toURI().toString());
        ImageView ivPlay = new ImageView(imagePlay);
        ivPlay.setFitWidth(25);
        ivPlay.setFitHeight(25);
        declineButton.setGraphic(ivPlay);
    }

    private void setIconSave(){
        Image imagePlay = new Image(new File("C:/Users/giand/IdeaProjects/PointingPlayer/src/main/resources/icons/save.png").toURI().toString());
        ImageView ivPlay = new ImageView(imagePlay);
        ivPlay.setFitWidth(25);
        ivPlay.setFitHeight(25);
        saveButton.setGraphic(ivPlay);
    }

    private void setIconVolume(){
        Image imageVol = new Image(new File("C:/Users/giand/IdeaProjects/PointingPlayer/src/main/resources/icons/volume.png").toURI().toString());
        volumeImg = new ImageView(imageVol);
        volumeImg.setFitHeight(25);
        volumeImg.setFitWidth(25);
        labelVolume.setGraphic(volumeImg);
    }

    private void setIconVolumeSilent(){
        Image imageVol = new Image(new File("C:/Users/giand/IdeaProjects/PointingPlayer/src/main/resources/icons/silent.png").toURI().toString());
        volumeImg = new ImageView(imageVol);
        volumeImg.setFitHeight(25);
        volumeImg.setFitWidth(25);
        labelVolume.setGraphic(volumeImg);
    }

    private void setIconRepeat(){
        Image imagePlay = new Image(new File("C:/Users/giand/IdeaProjects/PointingPlayer/src/main/resources/icons/repeat.png").toURI().toString());
        ImageView ivPlay = new ImageView(imagePlay);
        ivPlay.setFitWidth(25);
        ivPlay.setFitHeight(25);
        repeatButton.setGraphic(ivPlay);
    }

    private static String getTimeString(double millis) {
        millis /= 1000;
        String s = formatTime(millis % 60);
        millis /= 60;
        String m = formatTime(millis % 60);
        millis /= 60;
        String h = formatTime(millis % 24);
        return m + ":" + s;
    }

    private static String formatTime(double time) {
        int t = (int)time;
        if (t > 9) { return String.valueOf(t); }
        return "0" + t;
    }


//    public String getPathCsv() {
//        return pathCsv;
//    }
//    public void setPathCsv(String pathCsv) {
//        this.pathCsv = pathCsv;
//    }



//    private void scorriCSV(){
        //        String[] line;
        //
        //        try (Reader reader = Files.newBufferedReader(Path.of(getPathCsv()))) {
        //            CSVReader csvReader = new CSVReader(reader);
        //            for(int num_line = 0; num_line <= pointing.getSize(); num_line++){
        //                line = csvReader.readNext();
        //                if(num_line == this.pointingCounter + 1){
        //                    System.out.println(line[0]);
        //                    System.out.println(line[2]);
        //                }
        //            }
        //        } catch (Exception e){
        //            Alert alert = new Alert(Alert.AlertType.WARNING, "Nessun file csv trovato. Time series non disponibili!");
        //            alert.show();
        //        }
//    }
//            mediaView.sceneProperty().addListener(new ChangeListener<Scene>() {
//                @Override
//                public void changed(ObservableValue<? extends Scene> observableValue, Scene oldScene, Scene newScene) {
//                    if (oldScene == null && newScene != null) {
//                        mediaView.fitHeightProperty().bind(newScene.heightProperty().subtract(mediaView.fitHeightProperty().add(20)));
//                    }
//                }
//            });

//            seekSlider.setOnMousePressed(new EventHandler<MouseEvent>() {
//                @Override
//                public void handle(MouseEvent mouseEvent) {
//                    mediaPlayer.seek(new Duration(seekSlider.getValue()));
//                }
//            });



    //Funzionante
//            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
//                @Override
//                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
//                    seekSlider.setValue(newValue.toSeconds());
//                }
//            });
}