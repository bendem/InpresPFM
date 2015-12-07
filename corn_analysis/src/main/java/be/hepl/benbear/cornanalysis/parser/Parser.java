package be.hepl.benbear.cornanalysis.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Parser {

    private final Path file;

    public Parser(Path file) {
        this.file = file;
    }

    public CornStat parse() throws IOException {
        CornStat stat = new CornStat();
        Files.lines(file)
            .skip(1)
            .map(String::trim)
            .filter(l -> !l.isEmpty())
            .filter(l -> !l.startsWith("#"))
            .forEach(l -> parseLine(stat, l));
        return stat;
    }

    private void parseLine(CornStat stat, String line) {
        String[] parts = line.split("\t");
        stat.add(
            readInteger(parts[0]),
            readInteger(parts[1]),
            readInteger(parts[2]),
            readInteger(parts[3]),
            readDouble(parts[4]),
            readEnum(parts[5], Color.class),
            readBoolean(parts[6]),
            readEnum(parts[7], Rooting.class),
            readBoolean(parts[8]),
            readBoolean(parts[9]),
            readEnum(parts[10], Orientation.class),
            readDouble(parts[11]),
            readBoolean(parts[12]),
            readInteger(parts[13]),
            readBoolean(parts[14])
        );
    }

    private static Integer readInteger(String s) {
        if(s.equalsIgnoreCase("na")) {
            return null;
        }
        return Integer.parseInt(s);
    }

    private static Double readDouble(String s) {
        if(s.equalsIgnoreCase("na")) {
            return null;
        }
        return Double.parseDouble(s);
    }

    private static Boolean readBoolean(String s) {
        if(s.equalsIgnoreCase("na")) {
            return null;
        }
        return s.equals("Oui");
    }

    private static <T extends Enum<T> & Nameable> T readEnum(String s, Class<T> clazz) {
        if(s.equalsIgnoreCase("na")) {
            return null;
        }

        for(T t : clazz.getEnumConstants()) {
            if(t.getName().equals(s)) {
                return t;
            }
        }

        throw new AssertionError(String.format("Unknown value '%s' for '%s'", s, clazz.getName()));
    }

}
