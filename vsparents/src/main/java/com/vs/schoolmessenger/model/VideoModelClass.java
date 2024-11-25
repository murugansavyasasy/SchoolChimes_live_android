package com.vs.schoolmessenger.model;

public class VideoModelClass {

    private String VideoId,CreatedBy,CreatedOn,Title,Description,VimeoUrl,VimeoId,DetailID,IsAppViewed,Iframe;
    String VideoURL,VideoID;
    Boolean is_Archive,isDownload;

    public VideoModelClass() {
        this.VideoID = VideoID;
        this.VideoURL = VideoURL;
    }

    public String getVideoURL() {
        return VideoURL;
    }

    public void setVideoURL(String videoURL) {
        VideoURL = videoURL;
    }

    public String getVideoID() {
        return VideoID;
    }

    public void setVideoID(String videoID) {
        VideoID = videoID;
    }

    public VideoModelClass(String videoId, String createdBy, String createdOn, String title, String description,
                           String vimeoUrl, String vimeoId,String DetailID,String IsAppViewed,String Iframe,Boolean archive,Boolean isDownload) {
        VideoId = videoId;
        CreatedBy = createdBy;
        CreatedOn = createdOn;
        Title = title;
        Description = description;
        VimeoUrl = vimeoUrl;
        VimeoId = vimeoId;
       this.DetailID = DetailID;
        this.IsAppViewed = IsAppViewed;
        this.Iframe = Iframe;
        this.is_Archive = archive;
        this.isDownload = isDownload;
    }

    public Boolean getDownload() {
        return isDownload;
    }

    public void setDownload(Boolean download) {
        isDownload = download;
    }

    public String getIframe() {
        return Iframe;
    }

    public void setIframe(String iframe) {
        Iframe = iframe;
    }

    public String getDetailID() {
        return DetailID;
    }

    public void setDetailID(String detailID) {
        DetailID = detailID;
    }

    public String getIsAppViewed() {
        return IsAppViewed;
    }

    public void setIsAppViewed(String isAppViewed) {
        IsAppViewed = isAppViewed;
    }

    public String getVideoId() {
        return VideoId;
    }

    public void setVideoId(String videoId) {
        VideoId = videoId;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedOn() {
        return CreatedOn;
    }

    public void setCreatedOn(String createdOn) {
        CreatedOn = createdOn;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getVimeoUrl() {
        return VimeoUrl;
    }

    public void setVimeoUrl(String vimeoUrl) {
        VimeoUrl = vimeoUrl;
    }

    public String getVimeoId() {
        return VimeoId;
    }

    public void setVimeoId(String vimeoId) {
        VimeoId = vimeoId;
    }

    public Boolean getIs_Archive() {
        return is_Archive;
    }

    public void setIs_Archive(Boolean archive) {
        this.is_Archive = archive;
    }
}
