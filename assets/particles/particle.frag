#version 430

out vec4 FinalColour;

#define EXCLUDE_PARTICLE_BUFFER
#pragma include("compute/includes/particle.glsl")

in flat ParticleData datum;

in vec2 TexCoords;
uniform sampler2D u_texture;
uniform float u_alphaCutoff = 0.25;
uniform int u_alphaChannel = 3;

void main() {

    if(datum.Life <= 0)
        discard;

    vec4 tex = texture(u_texture, TexCoords);

    tex.a = tex[u_alphaChannel];

    vec4 col = tex * datum.Colour;

    col.a = col.a * smoothstep(0.0, 0.5, datum.Life / datum.MaxLife);

    if(col.a <= u_alphaCutoff)
        discard;

	FinalColour = col;
}
