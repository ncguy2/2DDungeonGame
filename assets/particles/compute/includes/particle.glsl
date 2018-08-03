struct ParticleData {
    vec2 Position;
    float Life;
    uint Type;
    vec4 Colour;
    vec2 Velocity;
    vec2 Scale;
};


#ifndef EXCLUDE_PARTICLE_BUFFER
layout(std430, binding = p_BindingPoint) buffer Particles {
    ParticleData Data[];
};
#endif