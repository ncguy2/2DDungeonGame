struct ParticleData {
    vec2 Position;  // 4,8
    float Life;     // 12
    float MaxLife;  // 16
    vec4 BaseColour;// 20,24,28,32
    vec4 Colour;    // 36,40,44,48
    vec2 Velocity;  // 52, 56
    vec2 Scale;     // 60, 64
    vec2 TexCoords; // 68, 72
};


#ifndef EXCLUDE_PARTICLE_BUFFER
layout(std430, binding = p_BindingPoint) buffer Particles {
    ParticleData Data[];
};
#endif