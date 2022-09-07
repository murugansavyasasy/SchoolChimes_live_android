package com.vs.schoolmessenger.model;

public class Imagemodel

    {
        private String Img1, Img2, Img3;

    public Imagemodel(String img1, String img2, String img3) {
        this.Img1 = img1;
        this.Img2 = img2;
        this.Img3 = img3;
    }

    public Imagemodel() {}

        public String getImg1() {
        return Img1;
    }

        public void setImg1(String date) {
        this.Img1 = date;
    }

        public String getImg2() {
        return Img2;
    }

        public void setImg2(String day) {
        this.Img2 = day;
    }

        public String getImg3() {
        return Img3;
    }

        public void setImg3(String count) {
        this.Img3 = count;
    }
    }