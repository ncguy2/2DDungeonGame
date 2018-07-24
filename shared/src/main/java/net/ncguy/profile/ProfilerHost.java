package net.ncguy.profile;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ProfilerHost {

    public static final Object mutex = new Object();

    private static ProfilerHost instance;
    public static final boolean profilerEnabled = true;

    public List<Runnable> onNotify;

    public static ProfilerHost instance() {
        if (instance == null)
            instance = new ProfilerHost();
        return instance;
    }

    public ConcurrentLinkedQueue<TaskStats> taskStats;

    private ProfilerHost() {
        taskStats = new ConcurrentLinkedQueue<>();
        onNotify = new ArrayList<>();
    }

    public static void Notify(Runnable task) {
        instance().onNotify.add(task);
    }

    public static synchronized void Post(TaskStats payload) {
        instance().Add(payload);
    }

    private void Add(TaskStats payload) {
        taskStats.add(payload);
    }

    public void Iterate(Consumer<TaskStats> task) {
        TaskStats stat;
        List<TaskStats> stats = new ArrayList<>();

        while (!taskStats.isEmpty()) {
            stat = taskStats.poll();
            stats.add(stat);
        }

        stats.stream()
                .sorted(Comparator.comparingLong(a -> a.startTime))
                .forEach(task);
    }

    public void NotifyListeners() {
        onNotify.forEach(Runnable::run);
    }

    public static void Clear() {
        synchronized (mutex) {
            instance().taskStats.clear();
        }
    }

    public String GetFullDump() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Iterate(s -> s.Dump(stream));
        return stream.toString();
    }

    public Optional<TaskStats> GetLatestOfType(String type) {
        ArrayList<TaskStats> stats;
        synchronized (mutex) {
            stats = new ArrayList<>(taskStats);
        }
        return stats.stream()
                .filter(s -> s.type.equalsIgnoreCase(type))
                .findFirst();
    }

    public List<TaskStats> FlattenLatestOfTypes(String... types) {
        List<TaskStats> roots = new ArrayList<>();

        for (String type : types)
            GetLatestOfType(type).ifPresent(roots::add);

        final List<TaskStats> stats = new ArrayList<>();
        roots.forEach(r -> FlattenLatestOfTypes(r, stats));
        return stats;
    }

    public void FlattenLatestOfTypes(TaskStats stat, List<TaskStats> list) {
        list.add(stat);
        stat.children.forEach(c -> FlattenLatestOfTypes(c, list));
    }

    public List<TaskStats> GetCurrentStats(String type) {
        return FlattenLatestOfTypes(type).stream()
                .sorted(Comparator.comparingLong(a -> a.startTime))
                .collect(Collectors.toList());
    }

    public String GetCurrentDump(String type) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        PrintStream print = new PrintStream(stream);

        List<TaskStats> flatList = FlattenLatestOfTypes(type);
        flatList.stream()
                .sorted(Comparator.comparingLong(a -> a.startTime))
                .map(TaskStats::ToDumpString)
                .forEach(print::println);

        return stream.toString();
    }


    public static void StartFrame() {
        if(!profilerEnabled)
            return;

        synchronized (mutex) {
//            Clear();
            CPUProfiler.StartFrame();
            GPUProfiler.StartFrame();
        }
    }

    public static void Start(String name) {
        if(!profilerEnabled)
            return;
        CPUProfiler.Start(name);
        GPUProfiler.Start(name);
    }

    public static void End(String name) {
        End();
    }

    public static void End() {
        if(!profilerEnabled)
            return;
        CPUProfiler.End();
        GPUProfiler.End();
    }

    public static void EndFrame() {
        if(!profilerEnabled)
            return;
        synchronized (mutex) {
            CPUProfiler.EndFrame();
            GPUProfiler.EndFrame();
        }
    }

}
