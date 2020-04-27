package edu.cs50.harvard.pokedex;

public class Pokemon {
    private String name;
    private String url;


    Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }


}
