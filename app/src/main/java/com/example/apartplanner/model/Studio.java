package com.example.apartplanner.model;

public class Studio {
    private String name;
    private String size;
    private String state;

    public Studio(){}

    public Studio(String name, String size, String state) {
/*        if(name.trim().equals("")){
            name = "No name";
        }
        if(size.trim().equals("")){
            size = "No size";
        }
        if(state.trim().equals("")){
            size = "No state";
        }*/
        this.name = name;
        this.size = size;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

