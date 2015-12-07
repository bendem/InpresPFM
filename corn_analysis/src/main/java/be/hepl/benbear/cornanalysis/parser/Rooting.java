package be.hepl.benbear.cornanalysis.parser;

public enum Rooting implements Nameable {
    VeryStrong("Tres.fort"),
    Strong("Fort"),
    Average("Moyen"),
    Weak("Faible"),
    ;

    private final String name;

    Rooting(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
