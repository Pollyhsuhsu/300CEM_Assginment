package com.example.a300cem_android_assignment.HomeAdapter;

import android.graphics.drawable.GradientDrawable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoriesHelperClass {

    int image;
    String title;
    GradientDrawable gradient;


    public CategoriesHelperClass(int image, String title, GradientDrawable gradient) {
        this.image = image;
        this.title = title;
        this.gradient = gradient;
    }

    public int getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public GradientDrawable getGradient() {
        return gradient;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setGradient(GradientDrawable gradient) {
        this.gradient = gradient;
    }
}
