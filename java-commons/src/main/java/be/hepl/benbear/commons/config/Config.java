package be.hepl.benbear.commons.config;

import be.hepl.benbear.commons.logging.Log;

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
        return null;
    }

    private final Map<String, String> data;

    public Config() {
        this.data = new ConcurrentHashMap<>();
    }

    public Config(String path) throws IOException {
        this(path == null ? null : Paths.get(path));
    }

    public Config(Path path) throws IOException {
        path = checkPath(path);
        this.data = new ConcurrentHashMap<>();

        if(path != null) {
            load(path);
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

    public int getIntThrowing(String name) {
        return getInt(name).orElseThrow(() -> new RuntimeException(name + " not found in the config"));
    }

    public Optional<String> getString(String name) {
        return Optional.ofNullable(data.get(name));
    }

    public String getStringThrowing(String name) {
        return getString(name).orElseThrow(() -> new RuntimeException(name + " not found in the config"));
    }

    public Config load(String path) throws IOException {
        if(path != null) {
            load(Paths.get(path));
        }
        return this;
    }

    public Config load(Path path) throws IOException {
        Map<String, String> collected = Files.lines(path)
            .map(String::trim)
            .filter(l -> !l.isEmpty())
            .filter(l -> !l.startsWith("#"))
            .filter(l -> !l.startsWith(";"))
            .filter(l -> !l.startsWith("//"))
            .filter(l -> {
                if(!l.contains("=")) {
                    Log.w("Ignored invalid line: '%s'", l);
                    return false;
                }
                return true;
            })
            .map(l -> {
                int i = l.indexOf('=');
                return new String[]{l.substring(0, i), l.substring(i + 1)};
            })
            .collect(Collectors.toMap(
                p -> p[0].trim(),
                p -> p[1].trim(),
                (a, b) -> {
                    Log.w("Duplicated key '%s'", b);
                    return b;
                }
            ));
        data.putAll(collected);

        return this;
    }

}
