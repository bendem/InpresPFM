package be.hepl.benbear.cornanalysis;

import be.hepl.benbear.cornanalysis.parser.Parser;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static void main(String... args) throws IOException {
        if(args.length > 0) {
            System.out.println(new Parser(Paths.get(args[0])).parse().toString());
        }
    }

}
