package com.teamtreehouse.pomodoro.controllers;

import com.teamtreehouse.pomodoro.model.Attempt;
import com.teamtreehouse.pomodoro.model.AttemptKind;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;



/**
 * Created by leo_r on 29/10/2015.
 */
public class Home
{
    private final AudioClip mApplause;
    @FXML
    private VBox container;
    @FXML private Label title;
    private Attempt mCurrentAttempt;

    @FXML private TextArea message;

    private Timeline mTimeline;


    private StringProperty mTimerText;

    public Home()
    {
        mTimerText = new SimpleStringProperty();
        setTimerText(0);
        mApplause = new AudioClip(getClass().getResource("/sounds/applause.mp3").toExternalForm());
    }

    public String getTimerText() {
        return mTimerText.get();
    }

    public StringProperty timerTextProperty() {
        return mTimerText;
    }

    public void setTimerText(String timerText) {
        this.mTimerText.set(timerText);
    }



    public void setTimerText(int remainingSeconds)
    {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        setTimerText(String.format("%02d:%02d", minutes, seconds));

    }

    private void prepareAttempt(AttemptKind kind) {
        reset();
        mCurrentAttempt = new Attempt(kind, "");
        addAttemptStyle(kind);
        title.setText(kind.getDisplayName());
        setTimerText(mCurrentAttempt.getRemainingSeconds());
        mTimeline = new Timeline();
        mTimeline.setCycleCount(kind.getTotalSeconds());
        mTimeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> {
            mCurrentAttempt.tick();
            setTimerText(mCurrentAttempt.getRemainingSeconds());
        }));
        mTimeline.setOnFinished(e -> {

            saveCurrentAttempt();
            mApplause.play();
            prepareAttempt(mCurrentAttempt.getKind() == AttemptKind.FOCUS ?
                                AttemptKind.BREAK : AttemptKind.FOCUS);
        });
    }

    private void saveCurrentAttempt()
    {

        mCurrentAttempt.setMessage(message.getText());

        mCurrentAttempt.save();
    }

    private void reset() {
        clearAttemptStyles();
        if(mTimeline != null && mTimeline.getStatus() == Animation.Status.RUNNING)
        {
            mTimeline.stop();
        }
    }

    public void playTimer()
    {
        container.getStyleClass().add("playing");
        mTimeline.play();
    }

    public void pauseTimer()
    {
        container.getStyleClass().remove("playing");
        mTimeline.pause();
    }

    private void addAttemptStyle(AttemptKind kind)
    {
        container.getStyleClass().add(kind.toString().toLowerCase());
    }

    private void clearAttemptStyles()
    {

        container.getStyleClass().remove("playing");

        for(AttemptKind kind : AttemptKind.values())
        {
            container.getStyleClass().remove(kind.toString().toLowerCase());
        }

    }


    public void DEBUG(ActionEvent actionEvent) {
        prepareAttempt(AttemptKind.BREAK);
    }

    public void handleRestart(ActionEvent actionEvent)
    {

        prepareAttempt(AttemptKind.FOCUS);
        playTimer();

    }

    public void handlePlay(ActionEvent actionEvent)
    {
        if(mCurrentAttempt == null)
        {
            handleRestart(actionEvent);
        }
        else
        {

            playTimer();

        }

    }

    public void handlePause(ActionEvent actionEvent)
    {
        pauseTimer();
    }
}
