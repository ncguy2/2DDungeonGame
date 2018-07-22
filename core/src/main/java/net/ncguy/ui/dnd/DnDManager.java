package net.ncguy.ui.dnd;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import net.ncguy.util.StringUtils;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.BiPredicate;

public class DnDManager {

    private static DnDManager instance;
    public static DnDManager instance() {
        if (instance == null)
            instance = new DnDManager();
        return instance;
    }

    DragAndDrop dnd;
    Map<DragAndDrop.Source, Tags[]> srcTags;
    List<FunctionalTarget> targets;
    List<WeakReference<Table>> highlightedActors;

    private DnDManager() {
        dnd = new DragAndDrop();
        srcTags = new HashMap<>();
        targets = new ArrayList<>();
        highlightedActors = new ArrayList<>();
    }

    public void DragStart(FunctionalSource src) {
        Tags[] tags = GetSourceTags(src);
        long mask = Tags.Encode(tags);
        targets.stream().filter(t -> t.condition.Pass(mask)).filter(t -> t.getActor() instanceof Table).map(DragAndDrop.Target::getActor).map(t -> (Table) t).forEach(this::HighlightActor);
    }

    protected void HighlightActor(Table table) {
        highlightedActors.add(new WeakReference<>(table));
        // TODO highlight drag targets
    }

    public void DragStop() {
        for (WeakReference<Table> actorRef : highlightedActors) {
            Table actor = actorRef.get();
            if(actor != null)
                actor.setBackground((Drawable) null);
        }
        highlightedActors.clear();
    }

    public FunctionalSource AddSource(Actor actor, Tags... tags) {
        FunctionalSource src = new FunctionalSource(actor);
        srcTags.put(src, tags);
        dnd.addSource(src);
        return src;
    }

    public void RemoveSource(FunctionalSource source) {
        dnd.removeSource(source);
        srcTags.remove(source);
    }

    public FunctionalTarget AddTarget(Actor actor, ConditionType condition, Tags... tags) {
        FunctionalTarget target = new FunctionalTarget(actor, new TagCondition(condition, tags));
        dnd.addTarget(target);
        targets.add(target);
        return target;
    }

    public void RemoveTarget(FunctionalTarget target) {
        dnd.removeTarget(target);
        targets.remove(target);
    }

    public long GetSourceMask(DragAndDrop.Source source) {
        Tags[] tags = GetSourceTags(source);
        if(tags == null)
            return 0;
        return Tags.Encode(tags);
    }

    public Tags[] GetSourceTags(DragAndDrop.Source source) {
        return srcTags.get(source);
    }

    public static class TagCondition {
        public ConditionType type;
        public Tags[] tags;

        public TagCondition(ConditionType type, Tags[] tags) {
            this.type = type;
            this.tags = tags;
        }

        public boolean Pass(Tags... tags) {
            return Pass(Tags.Encode(tags));
        }
        public boolean Pass(long value) {
            return type.Pass(value, tags);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TagCondition that = (TagCondition) o;
            return type == that.type && Tags.Encode(tags) == Tags.Encode(that.tags);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(type);
            result = 31 * result + Math.toIntExact(Tags.Encode(tags));
            return result;
        }
    }
    

    public static enum Tags {
        Tag_0("Ability"),
        Tag_1,
        Tag_2,
        Tag_3,
        Tag_4,
        Tag_5,
        Tag_6,
        Tag_7,
        Tag_8,
        Tag_9,
        Tag_10,
        Tag_11,
        Tag_12,
        Tag_13,
        Tag_14,
        Tag_15,
        Tag_16,
        Tag_17,
        Tag_18,
        Tag_19,
        Tag_20,
        Tag_21,
        Tag_22,
        Tag_23,
        Tag_24,
        Tag_25,
        Tag_26,
        Tag_27,
        Tag_28,
        Tag_29,
        Tag_30,
        Tag_31,


        // Aliases, do not use ordinal beyond this point
        Ability(Tag_0),
        ;

        public final String alias;
        public final long bit;

        Tags() {
            this((String) null);
        }

        Tags(String alias) {
            if(StringUtils.IsNullOrEmpty(alias))
                alias = StringUtils.ToDisplayCase(name());

            this.alias = alias;
            this.bit = 1 << ordinal();
        }

        Tags(Tags ref) {
            this.alias = ref.alias;
            this.bit = ref.bit;
        }

        public static long Encode(Tags... tags) {
            long val = 0;
            for (Tags tag: tags)
                val |= tag.bit;
            return val;
        }
    }

    public static enum ConditionType {
        All ((value, mask) -> (value & mask) == mask),
        Any ((value, mask) -> (value & mask) != 0),
        None((value, mask) -> (value & mask) == 0),
        ;

        private final BiPredicate<Long, Long> condition;
        ConditionType(BiPredicate<Long, Long> condition) {
            this.condition = condition;
        }

        public boolean Pass(long value, Tags... tags) {
            return condition.test(value, Tags.Encode(tags));
        }
    }


}
