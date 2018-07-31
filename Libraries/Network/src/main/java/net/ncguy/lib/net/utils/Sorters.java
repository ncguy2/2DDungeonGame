package net.ncguy.lib.net.utils;

import java.util.Comparator;
import java.util.function.Function;

public class Sorters {

    /**
     * For use in stream.sorted() <br>
     * Sorts the stream of class elements to match the inheritance hierarchy, in ascending order
     * @param o1
     * @param o2
     * @return
     */
    public static int ClassHierarchyAscent(Class o1, Class o2) {
        return -ClassHierarchyDescent(o1, o2);
    }

    /**
     * For use in stream.sorted() <br>
     * Sorts the stream of class elements to match the inheritance hierarchy, in descending order
     * @param o1
     * @param o2
     * @return
     */
    public static int ClassHierarchyDescent(Class o1, Class o2) {

        Class o1Super = o1.getSuperclass();
        Class o2Super = o2.getSuperclass();

        if(o2Super == null || o2.equals(o1Super))
            return 1;
        if(o1Super == null || o1.equals(o2Super))
            return -1;

        return 0;
    }

    public static <T> Comparator<T> ClassHierarchyAscent(Function<T, Class> classSupplier) {
        return (o1, o2) -> ClassHierarchyAscent(classSupplier.apply(o1), classSupplier.apply(o2));
    }

    public static <T> Comparator<T> ClassHierarchyDescent(Function<T, Class> classSupplier) {
        return (o1, o2) -> ClassHierarchyDescent(classSupplier.apply(o1), classSupplier.apply(o2));
    }

}
