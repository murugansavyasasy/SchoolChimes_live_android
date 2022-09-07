package com.vs.schoolmessenger.model;

import java.io.Serializable;

/**
 * Created by voicesnap on 8/31/2016.
 */

@SuppressWarnings("serial")
public class CircularDates implements Serializable {
    private String circularDate, circularDay;
    private String voiceTotCount, voiceUnreadCount;
    private String textTotCount, textUnreadCount;
    private String imageTotCount, imageUnreadCount;
    private String pdfTotCount, pdfUnreadCount;
    private  boolean is_Archive;

    public CircularDates() {
    }

    public CircularDates(String circularDate, String circularDay, String voiceTotCount, String voiceUnreadCount,
                         String textTotCount, String textUnreadCount, String imageTotCount,
                         String imageUnreadCount, String pdfTotCount, String pdfUnreadCount,boolean archive) {
        this.circularDate = circularDate;
        this.circularDay = circularDay;
        this.voiceTotCount = voiceTotCount;
        this.voiceUnreadCount = voiceUnreadCount;
        this.textTotCount = textTotCount;
        this.textUnreadCount = textUnreadCount;
        this.imageTotCount = imageTotCount;
        this.imageUnreadCount = imageUnreadCount;
        this.pdfTotCount = pdfTotCount;
        this.pdfUnreadCount = pdfUnreadCount;
        this.is_Archive = archive;
    }
    public CircularDates(String circularDate, String circularDay, String voiceTotCount, String voiceUnreadCount,
                         String textTotCount, String textUnreadCount,boolean archive) {
        this.circularDate = circularDate;
        this.circularDay = circularDay;
        this.voiceTotCount = voiceTotCount;
        this.voiceUnreadCount = voiceUnreadCount;
        this.textTotCount = textTotCount;
        this.textUnreadCount = textUnreadCount;
        this.imageTotCount = imageTotCount;
        this.imageUnreadCount = imageUnreadCount;
        this.pdfTotCount = pdfTotCount;
        this.pdfUnreadCount = pdfUnreadCount;
        this.is_Archive = archive;
    }
    public String getCircularDate() {
        return circularDate;
    }

    public void setCircularDate(String circularDate) {
        this.circularDate = circularDate;
    }

    public String getCircularDay() {
        return circularDay;
    }

    public void setCircularDay(String circularDay) {
        this.circularDay = circularDay;
    }

    public String getVoiceTotCount() {
        return voiceTotCount;
    }

    public void setVoiceTotCount(String voiceTotCount) {
        this.voiceTotCount = voiceTotCount;
    }

    public String getVoiceUnreadCount() {
        return voiceUnreadCount;
    }

    public void setVoiceUnreadCount(String voiceUnreadCount) {
        this.voiceUnreadCount = voiceUnreadCount;
    }

    public String getTextTotCount() {
        return textTotCount;
    }

    public void setTextTotCount(String textTotCount) {
        this.textTotCount = textTotCount;
    }

    public String getTextUnreadCount() {
        return textUnreadCount;
    }

    public void setTextUnreadCount(String textUnreadCount) {
        this.textUnreadCount = textUnreadCount;
    }

    public String getImageTotCount() {
        return imageTotCount;
    }

    public void setImageTotCount(String imageTotCount) {
        this.imageTotCount = imageTotCount;
    }

    public String getImageUnreadCount() {
        return imageUnreadCount;
    }

    public void setImageUnreadCount(String imageUnreadCount) {
        this.imageUnreadCount = imageUnreadCount;
    }

    public String getPdfTotCount() {
        return pdfTotCount;
    }

    public void setPdfTotCount(String pdfTotCount) {
        this.pdfTotCount = pdfTotCount;
    }

    public String getPdfUnreadCount() {
        return pdfUnreadCount;
    }

    public void setPdfUnreadCount(String pdfUnreadCount) {
        this.pdfUnreadCount = pdfUnreadCount;
    }

    public Boolean getIs_Archive() {
        return is_Archive;
    }

    public void setIs_Archive(Boolean archive) {
        this.is_Archive = archive;
    }
}
