package com.valeriano.mypokedex.models;

import java.util.List;

public class Pokemon {
    private int id;
    private String name;
    private int height;
    private int weight;
    private List<Type> types;
    private List<Ability> abilities;
    private Sprites sprites;
    private List<Stat> stats;

    // Constructors
    public Pokemon() {}

    public Pokemon(int id, String name, int height, int weight) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.weight = weight;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    public int getWeight() { return weight; }
    public void setWeight(int weight) { this.weight = weight; }

    public List<Type> getTypes() { return types; }
    public void setTypes(List<Type> types) { this.types = types; }

    public List<Ability> getAbilities() { return abilities; }
    public void setAbilities(List<Ability> abilities) { this.abilities = abilities; }

    public Sprites getSprites() { return sprites; }
    public void setSprites(Sprites sprites) { this.sprites = sprites; }

    public List<Stat> getStats() { return stats; }
    public void setStats(List<Stat> stats) { this.stats = stats; }

    // Inner classes
    public static class Type {
        private TypeInfo type;

        public TypeInfo getType() { return type; }
        public void setType(TypeInfo type) { this.type = type; }

        public static class TypeInfo {
            private String name;

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
    }

    public static class Ability {
        private AbilityInfo ability;

        public AbilityInfo getAbility() { return ability; }
        public void setAbility(AbilityInfo ability) { this.ability = ability; }

        public static class AbilityInfo {
            private String name;

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
    }

    public static class Sprites {
        private String front_default;
        private String back_default;

        public String getFront_default() { return front_default; }
        public void setFront_default(String front_default) { this.front_default = front_default; }

        public String getBack_default() { return back_default; }
        public void setBack_default(String back_default) { this.back_default = back_default; }
    }

    public static class Stat {
        private int base_stat;
        private StatInfo stat;

        public int getBase_stat() { return base_stat; }
        public void setBase_stat(int base_stat) { this.base_stat = base_stat; }

        public StatInfo getStat() { return stat; }
        public void setStat(StatInfo stat) { this.stat = stat; }

        public static class StatInfo {
            private String name;

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        }
    }
}