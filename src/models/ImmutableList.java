package models;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImmutableList<T> {
    private final List<T> items;

    public ImmutableList(List<T> items) {
        this.items = new ArrayList<>(items);
    }

    public ImmutableList(Stream<T> items){
        this.items = items.collect(Collectors.toList());
    }

    public int size(){
        return items.size();
    }

    public Stream<T> stream(){
        return items.stream();
    }

    public ImmutableList<T> only(Predicate<T> predicate){
        return new ImmutableList<>(
                stream().filter(predicate)
        );
    }

    public ImmutableList<T> except(Predicate<T> predicate){
        return new ImmutableList<>(
                stream().filter(item -> !predicate.test(item))
        );
    }

    @SafeVarargs
    public final ImmutableList<T> except(T... items){
        Set<T> itemSet = new HashSet<>(Arrays.asList(items));

        return except(itemSet::contains);
    }

    @SafeVarargs
    public final ImmutableList<T> union(T... additionalItems){
        Set<T> existing = new HashSet<>(this.items);

        existing.addAll(Arrays.asList(additionalItems));

        return new ImmutableList<>(existing.stream());
    }

    @SafeVarargs
    public final ImmutableList<T> concat(T... additionalItems) {
        List<T> existing = new ArrayList<>(this.items);
        existing.addAll(Arrays.asList(additionalItems));

        return new ImmutableList<>(existing);
    }

    public static <T> ImmutableList<T> empty(){
        return new ImmutableList<>(Stream.empty());
    }
}
