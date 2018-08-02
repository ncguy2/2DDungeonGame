package net.ncguy.particles;

import net.ncguy.profile.ProfilerHost;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class ParticleManager {

    private static ParticleManager instance;

    public static ParticleManager instance() {
        if (instance == null)
            instance = new ParticleManager();
        return instance;
    }

    private ParticleManager() {
        Init();
    }

//    public static final int ParticleBufferBytes = 134_217_728; // 128MB
//    public static final int DeadBufferBytes = 16_384; // 16KB
//
//    public static final int maxEstimatedParticles = 2982616;
//
//
    protected List<Integer> availableIndices;

    protected List<AbstractParticleSystem> systems;

    public void Systems(Consumer<AbstractParticleSystem> task) {
        systems.forEach(task);
    }

    public void Init() {
        availableIndices = new ArrayList<>();
        systems = new ArrayList<>();
    }

    public void Update(final float delta) {
        ProfilerHost.Start("ParticleManager::Update");

        UpdateIndices();

        ProfilerHost.Start("System update");
        systems.forEach(sys -> sys.Update(delta));
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
