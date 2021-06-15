package io.github.divios.core_lib.misc;

import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * Uses a map of outcomes to weights to get random values
 * @param <T> The type of the value
 * @author Redempt
 */
public class WeightedRandom<T> {

    private Map<T, Double> weights;
    private double total;
    private List<Double> totals;
    private List<T> items;

    private boolean removeOnRoll = false;

    /**
     * Create a new WeightedRandom from a map of outcomes to their weights
     * @param map The map of outcomes to their weights
     * @param <T> The type of the outcomes
     * @return A WeightedRandom which can be used to roll for the given outcome
     */
    public static <T> WeightedRandom<T> fromIntMap(Map<T, Integer> map) {
        HashMap<T, Double> dmap = new HashMap<>();
        map.forEach((k, v) -> {
            dmap.put(k, (double) v);
        });
        return new WeightedRandom<>(dmap, false);
    }

    /**
     * Create a new WeightedRandom from a map of outcomes to their weights
     * @param map The map of outcomes to their weights
     * @param <T> The type of the outcomes
     * @return A WeightedRandom which can be used to roll for the given outcome
     */
    public static <T> WeightedRandom<T> fromDoubleMap(Map<T, Double> map) {
        return new WeightedRandom<T>(map, false);
    }

    /**
     * Creates a WeightedRandom from a collection
     * @param collection The collection to convert
     * @param converter The function to convert from the type in the collection to the type in the WeightedRandom
     * @param weightGetter The function to get the weight of an element in the collection
     * @param <T> The type the WeightedRandom will roll on
     * @param <K> The type of the elements in the Collection
     * @return The populated WeightedRandom
     */
    public static <T, K> WeightedRandom<T> fromCollection(Collection<K> collection, Function<K, T> converter, ToDoubleFunction<K> weightGetter) {
        Map<T, Double> map = new HashMap<>();
        for (K element : collection) {
            map.put(converter.apply(element), weightGetter.applyAsDouble(element));
        }
        return fromDoubleMap(map);
    }

    /**
     * Creates a WeightedRandom using the map of weights
     * @param weights The map of outcomes to weights
     * @deprecated Use {@link WeightedRandom#fromIntMap(Map)}
     */
    public WeightedRandom(Map<T, Integer> weights) {
        HashMap<T, Double> dmap = new HashMap<>();
        weights.forEach((k, v) -> {
            dmap.put(k, (double) v);
        });
        initialize(dmap);
    }

    /**
     * Create an empty WeightedRandom
     */
    public WeightedRandom() {
        weights = new HashMap<>();
        totals = new ArrayList<>();
        items = new ArrayList<>();
        total = 0;
    }

    public void removeOnRoll() { removeOnRoll = true; }

    public void setRemoveOnRoll(boolean removeOnRoll) { this.removeOnRoll = removeOnRoll; }

    private WeightedRandom(Map<T, Double> weights, boolean no) {
        initialize(weights);
    }

    private void initialize(Map<T, Double> weights) {
        this.weights = weights;
        total = 0;
        totals = new ArrayList<>();
        items = new ArrayList<>();
        int[] pos = {0};
        weights.forEach((k, v) -> {
            total += v;
            totals.add(total);
            items.add(k);
            pos[0]++;
        });
    }

    /**
     * Rolls and gets a weighted random outcome
     * @return A weighted random outcome, or null if there are no possible outcomes
     */
    public T roll() {
        if (totals.size() == 0) {
            return null;
        }
        double random = Math.random() * (total);
        int pos = Collections.binarySearch(totals, random);
        if (pos < 0) {
            pos = -(pos + 1);
        }
        pos = Math.min(pos, items.size() - 1);
        T toReturn = items.get(pos);

        if (removeOnRoll) {
            weights.remove(toReturn);
            initialize(weights);
        }

        return toReturn;


    }

    /**
     * Gets the chance each outcome has to occur in percentage (0-100)
     * @return A map of each outcome to its percentage chance to occur when calling {@link WeightedRandom#roll()}
     */
    public Map<T, Double> getPercentages() {
        Map<T, Double> percentages = new HashMap<>();
        weights.forEach((k, v) -> {
            percentages.put(k, (v / total) * 100d);
        });
        return percentages;
    }

    /**
     * Gets the map of weights for this WeightedRandom
     * @return The weight map
     */
    public Map<T, Double> getWeights() {
        return weights;
    }

    /**
     * Sets another weight in this WeightedRandom, replacing the weight of the outcome if it has already been added
     * @param outcome The weight to set
     * @param weight The outcome to set
     */
    public void set(T outcome, int weight) {
        set(outcome, (double) weight);
    }

    /**
     * Sets another weight in this WeightedRandom, replacing the weight of the outcome if it has already been added
     * @param outcome The weight to set
     * @param weight The outcome to set
     */
    public void set(T outcome, double weight) {
        remove(outcome);
        total += weight;
        weights.put(outcome, weight);
        totals.add(total);
        items.add(outcome);
    }

    /**
     * Removes an outcome from this WeightedRandom
     * @param outcome The outcome to remove
     */
    public void remove(T outcome) {
        Double value = weights.remove(outcome);
        if (value == null) {
            return;
        }
        int index = items.indexOf(outcome);
        items.remove(index);
        totals.remove(index);
        for (int i = index; i < totals.size(); i++) {
            totals.set(i, totals.get(i) - value);
        }
    }

    /**
     * Creates a copy of this WeightedRandom
     * @return An identical copy of this WeightedRandom
     */
    public WeightedRandom<T> clone() {
        return new WeightedRandom<>(new HashMap<>(weights), false);
    }

    /**
     * Performs a single roll given a map of outcomes to weights. If you need to roll multiple times, instantiate a WeightedRandom and call roll on that each time instead.
     * @param map The map of outcomes to weights
     * @param <T> The type being returned
     * @return A weighted random outcome
     */
    public static <T> T roll(Map<T, Integer> map) {
        return new WeightedRandom<T>(map).roll();
    }

}