package be.hepl.benbear.cornanalysis.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CornStat {

    public static class Corn {
        public final Integer id;
        public final Integer height;
        public final Integer weight;
        public final Integer grainCount;
        public final Double grainWeight;
        public final Color color;
        public final Boolean germinated;
        public final Rooting rooting;
        public final Boolean lodging;
        public final Boolean attack;
        public final Orientation plot;
        public final Double heightJ7;
        public final Boolean lodgingHandling;
        public final Integer attackTime;
        public final Boolean straightFurrow;

        public Corn(Integer id, Integer height, Integer weight, Integer grainCount, Double grainWeight, Color color, Boolean germinated, Rooting rooting, Boolean lodging, Boolean attack, Orientation plot, Double heightJ7, Boolean lodgingHandling, Integer attackTime, Boolean straightFurrow) {
            this.id = id;
            this.height = height;
            this.weight = weight;
            this.grainCount = grainCount;
            this.grainWeight = grainWeight;
            this.color = color;
            this.germinated = germinated;
            this.rooting = rooting;
            this.lodging = lodging;
            this.attack = attack;
            this.plot = plot;
            this.heightJ7 = heightJ7;
            this.lodgingHandling = lodgingHandling;
            this.attackTime = attackTime;
            this.straightFurrow = straightFurrow;
        }
    }

    private final List<Integer> id;
    private final List<Integer> height;
    private final List<Integer> weight;
    private final List<Integer> grainCount;
    private final List<Double> grainWeight;
    private final List<Color> color;
    private final List<Boolean> germinated;
    private final List<Rooting> rooting;
    private final List<Boolean> lodging;
    private final List<Boolean> attack;
    private final List<Orientation> plot;
    private final List<Double> heightJ7;
    private final List<Boolean> lodgingHandling;
    private final List<Integer> attackTime;
    private final List<Boolean> straightFurrow;

    public CornStat() {
        id = new ArrayList<>();
        height = new ArrayList<>();
        weight = new ArrayList<>();
        grainCount = new ArrayList<>();
        grainWeight = new ArrayList<>();
        color = new ArrayList<>();
        germinated = new ArrayList<>();
        rooting = new ArrayList<>();
        lodging = new ArrayList<>();
        attack = new ArrayList<>();
        plot = new ArrayList<>();
        heightJ7 = new ArrayList<>();
        lodgingHandling = new ArrayList<>();
        attackTime = new ArrayList<>();
        straightFurrow = new ArrayList<>();
    }

    public CornStat add(Integer id, Integer height, Integer weight, Integer grainCount,
                        Double grainWeight, Color color, Boolean germinated, Rooting rooting,
                        Boolean lodging, Boolean attack, Orientation plot, Double heightJ7,
                        Boolean lodgingHandling, Integer attackTime, Boolean straightFurrow) {
        this.id.add(id);
        this.height.add(height);
        this.weight.add(weight);
        this.grainCount.add(grainCount);
        this.grainWeight.add(grainWeight);
        this.color.add(color);
        this.germinated.add(germinated);
        this.rooting.add(rooting);
        this.lodging.add(lodging);
        this.attack.add(attack);
        this.plot.add(plot);
        this.heightJ7.add(heightJ7);
        this.lodgingHandling.add(lodgingHandling);
        this.attackTime.add(attackTime);
        this.straightFurrow.add(straightFurrow);

        return this;
    }

    public Stream<Corn> stream() {
        return IntStream.range(0, id.size()).mapToObj(i -> new Corn(
            id.get(i),
            height.get(i),
            weight.get(i),
            grainCount.get(i),
            grainWeight.get(i),
            color.get(i),
            germinated.get(i),
            rooting.get(i),
            lodging.get(i),
            attack.get(i),
            plot.get(i),
            heightJ7.get(i),
            lodgingHandling.get(i),
            attackTime.get(i),
            straightFurrow.get(i)
        ));
    }

    public List<Integer> getId() {
        return id;
    }

    public List<Integer> getHeight() {
        return height;
    }

    public List<Integer> getWeight() {
        return weight;
    }

    public List<Integer> getGrainCount() {
        return grainCount;
    }

    public List<Double> getGrainWeight() {
        return grainWeight;
    }

    public List<Color> getColor() {
        return color;
    }

    public List<Boolean> getGerminated() {
        return germinated;
    }

    public List<Rooting> getRooting() {
        return rooting;
    }

    public List<Boolean> getLodging() {
        return lodging;
    }

    public List<Boolean> getAttack() {
        return attack;
    }

    public List<Orientation> getPlot() {
        return plot;
    }

    public List<Double> getHeightJ7() {
        return heightJ7;
    }

    public List<Boolean> getLodgingHandling() {
        return lodgingHandling;
    }

    public List<Integer> getAttackTime() {
        return attackTime;
    }

    public List<Boolean> getStraightFurrow() {
        return straightFurrow;
    }

    @Override
    public String toString() {
        return "CornStat{" +
            "id=" + id +
            ", height=" + height +
            ", weight=" + weight +
            ", grainCount=" + grainCount +
            ", grainWeight=" + grainWeight +
            ", color=" + color +
            ", germinated=" + germinated +
            ", rooting=" + rooting +
            ", lodging=" + lodging +
            ", attack=" + attack +
            ", plot=" + plot +
            ", heightJ7=" + heightJ7 +
            ", lodgingHandling=" + lodgingHandling +
            ", attackTime=" + attackTime +
            ", straightFurrow=" + straightFurrow +
            '}';
    }
}
