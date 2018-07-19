#version 330
#define PI 3.14

out vec4 FinalColour;

in vec2 TexCoords;
in vec4 Colour;

uniform sampler2D u_texture;
uniform vec2 u_resolution;


float Sample(vec2 coord, float r) {
    return step(r, texture(u_texture, coord).r);
}

void main() {
    // Cartesian to Polar
    vec2 norm = TexCoords.st * 2.0 - 1.0;
    float theta = atan(norm.y, norm.x);
    float r = length(norm);
    float coord = (theta + PI) / (2.0 * PI);
    vec2 tc = vec2(coord, 0.0);

    // Sample hard shadows
    float center = Sample(tc, r);

    // Blur for softer shadows
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

    // Application
    float dst = texture(u_texture, TexCoords).r;

    FinalColour = vec4(vec3(1.0), sum * smoothstep(1.0, 0.0, r)) * Colour;
}