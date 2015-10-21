package be.hepl.benbear.commons.generics;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MappedMap<K, W, V> implements Map<K, V> {

    private Map<K, W> map;
    private Function<W, V> from;
    private final Function<V, W> to;

    public MappedMap(Map<K, W> map, Function<W, V> from, Function<V, W> to) {
        this.map = map;
        this.from = from;
        this.to = to;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object o) {
        return map.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return map.containsValue(to.apply((V) o));
    }

    @Override
    public V get(Object o) {
        return from.apply(map.get(o));
    }

    @Override
    public V put(K k, V v) {
        return null;
    }

    @Override
    public V remove(Object o) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach((k, v) -> map.put(k, to.apply(v)));
    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values().stream().map(from).collect(Collectors.toList());
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException(); // TODO?
    }
}
