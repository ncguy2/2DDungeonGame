#version 330
#define PI 3.14
#define TAU (PI*2)

out vec4 Shadows;

in VS_OUT {
    vec4 Position;
    vec4 Colour;
    vec2 TexCoords;
} fs_in;

uniform vec2 u_resolution;
uniform sampler2D u_texture;

float Sample(vec2 coord, float r) {
    return step(r, texture(u_texture, coord).r);
}

void main() {
    vec2 norm = fs_in.TexCoords * 2.0 - 1.0;
    float theta = atan(norm.y, norm.x);
    float r = length(norm);
    float coord = (theta + PI) / TAU;

    vec2 tc = vec2(coord, 0.0);
    float center = Sample(tc, r);
    float blur = (1.0 / u_resolution.x) * smoothstep(0.0, 1.0, r);
    float sum = 0.0;

    sum += Sample(vec2(tc.x - 4.0 * blur, tc.y), r) * 0.05;
    sum += Sample(vec2(tc.x - 3.0 * blur, tc.y), r) * 0.09;
    sum += Sample(vec2(tc.x - 2.0 * blur, tc.y), r) * 0.12;
    sum += Sample(vec2(tc.x - 1.0 * blur, tc.y), r) * 0.15;

    sum += center * 0.16;

    sum += Sample(vec2(tc.x + 1.0 * blur, tc.y), r) * 0.15;
    sum += Sample(vec2(tc.x + 2.0 * blur, tc.y), r) * 0.12;
    sum += Sample(vec2(tc.x + 3.0 * blur, tc.y), r) * 0.09;
    sum += Sample(vec2(tc.x + 4.0 * blur, tc.y), r) * 0.05;

    Shadows = vec4(vec3(sum * smoothstep(1.0, 0.0, r)), 1.0);
//    Shadows = vec4(theta, r, 0.0, 1.0);
}
