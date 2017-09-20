package com.wael.alameen.worldonlineshopping;


public class NavItems {
    private String name;
    private int icon;

    NavItems(String name, int icon) {
        setName(name);
        setIcon(icon);
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    private void setIcon(int icon) {
        this.icon = icon;
    }
}
