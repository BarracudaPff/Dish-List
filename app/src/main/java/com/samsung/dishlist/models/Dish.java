package com.samsung.dishlist.models;

import java.util.Arrays;
import java.util.Objects;

public class Dish {
    public int id;
    public String[] ingredients;

    public Dish() {
        //require
    }

    public Dish(int id, String[] ingredients) {
        this.id = id;
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dish dish = (Dish) o;
        return id == dish.id &&
                Arrays.equals(ingredients, dish.ingredients);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result + Arrays.hashCode(ingredients);
        return result;
    }

    @Override
    public String toString() {
        return "Dish{" +
                "id=" + id +
                ", ingredients=" + Arrays.toString(ingredients) +
                '}';
    }
}
