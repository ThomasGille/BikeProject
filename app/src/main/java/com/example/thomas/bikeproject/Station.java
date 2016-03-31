package com.example.thomas.bikeproject;

import java.io.Serializable;

/**
 * Created by Thomas on 15/03/2016.
 */
public class Station implements Serializable{

    private int number;
    private String name;
    private String address;
    private Position mPosition;
    private String status;
    private String contract_name;
    private int bike_stands;
    private int available_bike_stands;
    private int available_bikes;
    private long last_update;
    private boolean banking;
    private boolean bonus;
    private boolean favoris;

    public boolean isFavoris() {
        return favoris;
    }
    public void setFavoris(boolean favoris) {
        this.favoris = favoris;
    }
    public boolean isBanking() {
        return banking;
    }
    public void setBanking(boolean banking) {
        this.banking = banking;
    }
    public boolean isBonus() {
        return bonus;
    }
    public void setBonus(boolean bonus) {
        this.bonus = bonus;
    }
    public long getLast_update() {
        return last_update;
    }
    public void setLast_update(int last_update) {
        this.last_update = last_update;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Position getmPosition() {
        return mPosition;
    }
    public void setmPosition(Position mPosition) {
        this.mPosition = mPosition;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getContract_name() {
        return contract_name;
    }
    public void setContract_name(String contract_name) {
        this.contract_name = contract_name;
    }
    public int getBike_stands() {
        return bike_stands;
    }
    public void setBike_stands(int bike_stands) {
        this.bike_stands = bike_stands;
    }
    public int getAvailable_bike_stands() {
        return available_bike_stands;
    }
    public void setAvailable_bike_stands(int available_bike_stands) {
        this.available_bike_stands = available_bike_stands;
    }
    public int getAvailable_bikes() {
        return available_bikes;
    }
    public void setAvailable_bikes(int available_bikes) {
        this.available_bikes = available_bikes;
    }

    public Station(int number, String name, Position mPosition, boolean bonus, String address, String status, String contract_name, int bike_stands, int available_bike_stands, int available_bikes, long last_update, boolean banking) {
        this.number = number;
        this.name = name;
        this.mPosition = mPosition;
        this.bonus = bonus;
        this.address = address;
        this.status = status;
        this.contract_name = contract_name;
        this.bike_stands = bike_stands;
        this.available_bike_stands = available_bike_stands;
        this.available_bikes = available_bikes;
        this.last_update = last_update;
        this.banking = banking;
        this.favoris=false;
    }

    static public class Position implements Serializable{
        double lat;
        double lng;
        public Position(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }
    }
}
