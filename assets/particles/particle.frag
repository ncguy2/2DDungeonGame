#version 430

out vec4 FinalColour;

struct ParticleData {
    vec2 Position;  // 8
    float Life;     // 9
    vec4 Colour;    // 25
    vec2 Velocity;  // 33
    vec2 Scale;     // 41
    uint Type;      // 45
};

in flat ParticleData datum;

in vec2 TexCoords;
uniform sampler2D u_texture;
uniform float u_alphaCutoff = 0.4;

void main() {

    if(datum.Life <= 0)
        discard;

    vec4 col = texture(u_texture, TexCoords) * datum.Colour;

    if(col.a <= u_alphaCutoff)
        discard;

	FinalColour = texture(u_texture, TexCoords) * datum.Colour;
}
