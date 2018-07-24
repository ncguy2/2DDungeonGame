#version 330
#define PI 3.14

out vec4 FinalColour;

in vec2 TexCoords;

uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform float u_scale;

const float THRESHOLD = 0.75;

void main() {
    float distance = 1.0;

    vec2 coord;
    for(float y = 0.0; y < u_resolution.y; y += 1.0) {
        float dst = y / u_resolution.y;
        vec2 norm = vec2(TexCoords.s, dst) * 2.0 - 1.0;
        float theta = PI * 1.5 + norm.x * PI;
        float r = (1.0 + norm.y) * 0.5;

        coord = vec2(-r * sin(theta), -r * cos(theta)) * 0.5 + 0.5;
//        dst /= u_scale;
        vec4 datum = texture(u_texture, coord);
        float caster = 1 - datum.b;
        if(caster < THRESHOLD)
            distance = min(distance, dst);
    }

    FinalColour = vec4(vec3(distance), 1.0);
//    FinalColour = vec4(texture(u_texture, TexCoords).rgb, 1.0);
}