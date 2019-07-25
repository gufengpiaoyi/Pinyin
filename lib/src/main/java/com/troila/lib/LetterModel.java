package com.troila.lib;

import java.util.ArrayList;

public class LetterModel {
    private String Letter;
    private ArrayList<City.LocationBean.CountryRegionBean.StateBean.CityBean> List;

    public String getLetter() {
        return Letter;
    }

    public void setLetter(String letter) {
        Letter = letter;
    }

    public ArrayList<City.LocationBean.CountryRegionBean.StateBean.CityBean> getList() {
        return List;
    }

    public void setList(ArrayList<City.LocationBean.CountryRegionBean.StateBean.CityBean> list) {
        List = list;
    }
}
