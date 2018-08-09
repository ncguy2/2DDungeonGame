package net.ncguy.particles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import net.ncguy.lib.foundation.io.Json;
import net.ncguy.profile.ProfilerHost;
import net.ncguy.script.ScriptHost;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public class ParticleManager {

    private static ParticleManager instance;

    public static ParticleManager instance() {
        if (instance == null)
            instance = new ParticleManager();
        return instance;
    }

    protected List<ParticleBlock> blockRegistry;

    private ParticleManager() {
        ScriptHost.AddGlobalBinding("ParticleManager", this);
        blockRegistry = new ArrayList<>();
        systems = new ArrayList<>();
        Init();
        RegisterDefaultProfiles();
        RegisterDefaultParticleBlocks();
    }

    protected void RegisterDefaultParticleBlocks() {
        String path = "metadata/shaders/";
        GetDefaults(path, s -> s.endsWith(".json")).map(s -> Json.From(s, ParticleBlock.class)).forEach(this::RegisterParticleBlock);
    }

    protected void RegisterParticleBlock(ParticleBlock block) {
        blockRegistry.add(block);
    }

    public Optional<ParticleBlock> GetParticleBlock(String name) {
        return blockRegistry.stream().filter(e -> e.name.equalsIgnoreCase(name)).findFirst();
    }

    protected Map<String, ParticleProfile> profiles;

    protected void RegisterDefaultProfiles() {

        String path = "metadata/particles/";
        GetDefaults(path, s -> s.endsWith(".json")).map(s -> Json.From(s, ParticleProfile.class)).forEach(this::RegisterProfile);

//        FileHandle dir = Gdx.files.internal(path);
//        String s = dir.readString();
//
//        String[] fileNames = s.split("\n");
//        List<FileHandle> handles = new ArrayList<>();
//        for (String fileName : fileNames) {
//            if (fileName.endsWith(".json"))
//                handles.add(Gdx.files.internal(path + "/" + fileName));
//        }
//
//        handles.stream().map(FileHandle::readString).map(str -> Json.From(str, ParticleProfile.class)).forEach(this::RegisterProfile);

//        RegisterProfile(Json.From(Gdx.files.internal("metadata/particles/default.json").readString(), ParticleProfile.class));
//        RegisterProfile(Json.From(Gdx.files.internal("metadata/particles/blink.json").readString(), ParticleProfile.class));

//        ParticleProfile p = new ParticleProfile();
//        p.type = AbstractParticleSystem.SystemType.Temporal;
//        p.name = "Default";
//        p.curve = new GLColourCurve();
//        p.curve.Add(Color.RED.cpy(), 0.f);
//        p.curve.Add(Color.GREEN.cpy(), 2.5f);
//        p.curve.Add(Color.BLUE.cpy(), 5f);
//        p.curve.Add(Color.WHITE.cpy(), 7.5f);
//        p.duration = 15.f;
//        p.spawnOverTime =  5.f;
//
//        String to = Json.To(p);
//        System.out.println(to);
    }

    protected Stream<String> GetDefaults(String root, Predicate<String> fileFilter) {
        FileHandle dir = Gdx.files.internal(root);
        String s = dir.readString();

        String[] fileNames = s.split("\n");
        List<FileHandle> handles = new ArrayList<>();
        for (String fileName : fileNames) {
            if (fileFilter.test(fileName))
                handles.add(Gdx.files.internal(root + "/" + fileName));
        }

        return handles.stream().map(FileHandle::readString);
    }

    public void RegisterProfile(ParticleProfile profile) {
        profiles.put(profile.name, profile);
    }

    public Optional<ParticleProfile> GetProfile(String name) {
        return profiles.keySet().stream().filter(name::equalsIgnoreCase).map(profiles::get).findFirst();
    }

    public Optional<AbstractParticleSystem> BuildSystem(String name) {
        return GetProfile(name).flatMap(this::BuildSystem);
    }
    public Optional<AbstractParticleSystem> BuildSystem(ParticleProfile profile) {
        return BuildSystemImpl(profile).map(p -> {
            p.loopingBehaviour = profile.loopingBehaviour;
            p.loopingAmount = profile.loopingAmount;
            return p;
        });
    }

    private Optional<AbstractParticleSystem> BuildSystemImpl(ParticleProfile profile) {
        switch(profile.type) {
            case Burst: return Optional.of(new BurstParticleSystem(profile.particleCount, profile.duration, profile.blocks));
            case Temporal: return Optional.of(new TemporalParticleSystem(profile.particleCount, profile.spawnOverTime, profile.duration, profile.blocks));
            case TextureBurst: return Optional.of(TextureBurstParticleSystem.Build(null, null, 0, new Vector2(1, 1), profile));
        }
        return Optional.empty();
    }

    //    public static final int ParticleBufferBytes = 134_217_728; // 128MB
//    public static final int DeadBufferBytes = 16_384; // 16KB
//
//    public static final int maxEstimatedParticles = 2982616;
//
//
    protected List<Integer> availableIndices;

    protected final List<AbstractParticleSystem> systems;

    public static final int MaxBindingPoints = 96;
    protected List<Integer> takenBindingPoints;

    int GetAvailableBindingPoint() {
        int i1 = IntStream.range(1, MaxBindingPoints)
                .filter(i -> !takenBindingPoints.contains(i))
                .findFirst()
                .orElse(-1);
        System.out.println("Issued binding point at id " + i1);
        return i1;
    }

    public void AddSystem(AbstractParticleSystem system) {
        system.bufferId = GetAvailableBindingPoint();
        synchronized (systems) {
            systems.add(system);
        }
        takenBindingPoints.add(system.bufferId);
    }

    public void RemoveSystem(AbstractParticleSystem system) {
        synchronized (systems) {
            systems.remove(system);
        }
        takenBindingPoints.remove((Integer) system.bufferId);
    }

    public void Systems(Consumer<AbstractParticleSystem> task) {
        synchronized (systems) {
            systems.forEach(task);
        }
    }

    public void Init() {
        profiles = new HashMap<>();
        takenBindingPoints = new ArrayList<>();
        availableIndices = new ArrayList<>();
    }

    public void Update(final float delta) {
        ProfilerHost.Start("ParticleManager::Update");

        UpdateIndices();

        ProfilerHost.Start("System update");
        synchronized (systems) {
            new ArrayList<>(systems).forEach(sys -> sys.Update(delta));
        }
        ProfilerHost.End("System update");

        ProfilerHost.End("ParticleManager::Update");
    }

    public int[] RequestIndices(int amount) {
        ProfilerHost.Start("ParticleManager::RequestIndices");
        int[] ints = availableIndices.stream()
                .limit(amount)
                .collect(Collectors.toList())
                .stream()
                .peek(availableIndices::remove)
                .mapToInt(i -> i)
                .toArray();
        ProfilerHost.End("ParticleManager::RequestIndices");
        return ints;
    }

    public void UpdateIndices() {
//        ProfilerHost.Start("ParticleManager::UpdateIndices");
//        ProfilerHost.Start("Fetch");
//        ByteBuffer dead = deadBuffer.GetData();
//        IntBuffer intBuffer = dead.asIntBuffer();
//        ProfilerHost.End("Fetch");
//
//        ProfilerHost.Start("Build");
//        intBuffer.position(0);
//        int[] arr;
//        if (intBuffer.hasArray())
//            arr = intBuffer.array();
//        else {
//            arr = new int[intBuffer.capacity()];
//            intBuffer.get(arr);
//        }
//        ProfilerHost.End("Build");
//
//        ProfilerHost.Start("Populate");
//        availableIndices.clear();
//        for (int i : arr) {
//            if (i >= 0)
//                availableIndices.add(i);
//        }
//        ProfilerHost.End("Populate");
//
//        ProfilerHost.End("ParticleManager::UpdateIndices");
    }

//    public void BindBuffers(ComputeShader program) {
//        BindBuffers(program, false);
//    }
//    public void BindBuffers(ComputeShader program, boolean includeDeadPool) {
//        ProfilerHost.Start("ParticleManager::BindBuffers " + (includeDeadPool ? " + DeadBuffer" : ""));
//        program.BindSSBO(0, particleBuffer);
//        if(includeDeadPool)
//            program.BindSSBO(1, deadBuffer);
//        ProfilerHost.End("ParticleManager::BindBuffers");
//    }
}
