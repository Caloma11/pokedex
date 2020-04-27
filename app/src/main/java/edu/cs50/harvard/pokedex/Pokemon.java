package edu.cs50.harvard.pokedex;

public class Pokemon {
    private String name;
    private int number;

    Pokemon(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }
}
