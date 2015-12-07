package be.hepl.benbear.cornanalysis.parser;

public enum Orientation implements Nameable {
    North("Nord"),
    South("Sud"),
    East("Est"),
    West("Ouest"),
    ;

    private final String name;

    Orientation(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
