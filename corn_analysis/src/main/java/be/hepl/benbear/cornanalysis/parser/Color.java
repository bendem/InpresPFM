package be.hepl.benbear.cornanalysis.parser;

public enum Color implements Nameable {
    Red("Rouge"),
    Yellow("Jaune"),
    RedYellow("Jaune.rouge"),
    ;

    private final String name;

    Color(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
