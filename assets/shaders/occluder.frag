#version 330
#define PI 3.14

out vec4 FinalColour;

in vec2 TexCoords;
in vec4 Colour;

uniform sampler2D u_texture;

uniform int u_castShadow;

void main() {

    float a = texture(u_texture, TexCoords).a * Colour.a;

    float val = a > 0.5 ? 1.0 : 0.0;

    if(val < 0.1)
        discard;

    FinalColour = vec4(vec3(val), 1.0);
}