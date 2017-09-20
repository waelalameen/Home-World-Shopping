package com.wael.alameen.worldonlineshopping;

public class singleTon {
    private static singleTon ourInstance = new singleTon();

    public static singleTon getInstance() {
        return ourInstance;
    }

    private singleTon() {
    }
}
