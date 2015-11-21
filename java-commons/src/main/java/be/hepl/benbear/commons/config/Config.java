package be.hepl.benbear.commons.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class Config {

    private static final Path DEFAULT_PATH = Paths.get("..", "global.conf");
    private static Path checkPath(Path path) {
        if(path != null && Files.isRegularFile(path)) {
            return path;
        }
        if(Files.isRegularFile(DEFAULT_PATH)) {
            return DEFAULT_PATH;
        }
        throw new RuntimeException(String.format("Config not found either at '%s' or at '%s'", path, DEFAULT_PATH));
    }

    private final Path path;
    private final Map<String, String> data;

    public Config(Path path) {
        this.path = checkPath(path);
        this.data = new ConcurrentHashMap<>();
        try {
            load();
        } catch(IOException e) {
            throw new RuntimeException("Failed to load configuration at '" + this.path + "'");
        }
    }

    public OptionalInt getInt(String name) {
        String value = data.get(name);
        if(value == null) {
            return OptionalInt.empty();
        }
        try {
            return OptionalInt.of(Integer.parseInt(value));
        } catch(NumberFormatException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return OptionalInt.empty();
        }
    }

    public Optional<String> getString(String name) {
        return Optional.ofNullable(data.get(name));
    }

    private Config load() throws IOException {
        Map<String, String> collected = Files.lines(path)
            .map(String::trim)
            .filter(l -> !l.startsWith("#"))
            .filter(l -> !l.startsWith(";"))
            .filter(l -> !l.startsWith("//"))
            .filter(l -> {
                if(!l.contains("=")) {
                    System.err.println("Ignored invalid line: '" + l + "'");
                    return false;
                }
                return true;
            })
            .map(l -> {
                int i = l.indexOf('=');
                return new String[]{l.substring(0, i), l.substring(i + 1)};
            })
            .collect(Collectors.toMap(
                p -> p[0],
                p -> p[1],
                (a, b) -> b
            ));
        data.putAll(collected);

        return this;
    }

}
