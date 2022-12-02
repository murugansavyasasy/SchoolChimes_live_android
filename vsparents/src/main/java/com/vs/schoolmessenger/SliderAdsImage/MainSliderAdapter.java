package com.vs.schoolmessenger.SliderAdsImage;

import android.content.Context;

import com.vs.schoolmessenger.model.adsModel;

import java.util.ArrayList;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {

    private ArrayList<adsModel> adsList;
    Context context;

    @Override
    public int getItemCount() {
        return 3;
    }

    public MainSliderAdapter(Context context, ArrayList<adsModel> dateList) {
        this.context = context;
        this.adsList = dateList;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        switch (position) {
            case 0:
                String ad_one = adsList.get(0).getAdImage();
                viewHolder.bindImageSlide(ad_one);
                break;
            case 1:
                String ad_two = adsList.get(1).getAdImage();
                viewHolder.bindImageSlide(ad_two);
                break;
            case 2:
                String ad_three = adsList.get(2).getAdImage();
                viewHolder.bindImageSlide(ad_three);
                break;
            case 3:
                String ad_four = adsList.get(3).getAdImage();
                viewHolder.bindImageSlide(ad_four);
                break;
            case 4:
                String ad_five = adsList.get(4).getAdImage();
                viewHolder.bindImageSlide(ad_five);
                break;
            case 5:
                String ad_six = adsList.get(5).getAdImage();
                viewHolder.bindImageSlide(ad_six);
                break;
            case 6:
                String ad_seven = adsList.get(6).getAdImage();
                viewHolder.bindImageSlide(ad_seven);
                break;
            case 7:
                String ad_eight = adsList.get(7).getAdImage();
                viewHolder.bindImageSlide(ad_eight);
                break;
            case 8:
                String ad_nine = adsList.get(8).getAdImage();
                viewHolder.bindImageSlide(ad_nine);
                break;
            case 9:
                String ad_ten = adsList.get(9).getAdImage();
                viewHolder.bindImageSlide(ad_ten);
                break;
            case 10:
                String ad_leven = adsList.get(10).getAdImage();
                viewHolder.bindImageSlide(ad_leven);
                break;
            case 11:
                String ad_twele = adsList.get(11).getAdImage();
                viewHolder.bindImageSlide(ad_twele);
                break;


        }
    }
}