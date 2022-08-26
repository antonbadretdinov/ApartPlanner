package com.example.apartplanner.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Studio {
    private int id;
    private String name;
    private String size;
    private String state;

    public Studio() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Studio studio = (Studio) o;
        return id == studio.id && name.equals(studio.name) && size.equals(studio.size) && state.equals(studio.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, size, state);
    }

    public Studio(int id, @NonNull String name, @NonNull String size, @NonNull String state) {
/*        if(name.trim().equals("")){
            name = "No name";
        }
        if(size.trim().equals("")){
            size = "No size";
        }
        if(state.trim().equals("")){
            size = "No state";
        }*/
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

