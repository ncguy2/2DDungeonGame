#version 430

layout (location = 0) in vec3 a_position;
layout (location = 1) in vec2 a_texCoord0;

struct ParticleData {
    vec2 Position;
    float Life;
    vec4 Colour;
    vec2 Velocity;
    vec2 Scale;
    uint Type;
};

layout(std430, binding = 0) readonly buffer Particles {
    ParticleData Data[];
};

uniform mat4 u_projViewTrans;

out flat ParticleData datum;

out vec2 TexCoords;

void main() {
    TexCoords = a_texCoord0;

    ParticleData d = Data[gl_InstanceID];

    datum = d;

    vec2 worldPos = a_position.xy * d.Scale;
    worldPos += d.Position;

    gl_Position = u_projViewTrans * vec4(worldPos, 0.0, 1.0);
}
