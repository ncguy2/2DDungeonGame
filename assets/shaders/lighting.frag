#version 330
#define PI 3.14

out vec4 Lighting;

in VS_OUT {
    vec4 Position;
    vec4 Colour;
    vec2 TexCoords;
} fs_in;

uniform vec2 u_resolution;

uniform sampler2D u_texture;
uniform sampler2D gDiffuse;
uniform sampler2D gNormal;
uniform sampler2D gEmissive;
uniform sampler2D gTexOcc;
uniform int u_castShadow;

const float THRESHOLD = 0.25;

void main() {
//	Lighting = vec4(vec3(texture(gTexOcc, fs_in.TexCoords).b, 0.0, 0.0), 1.0);

    float distance = 1.0;

    for(float y = 0.0; y < u_resolution.y; y += 1.0) {
        float dst = y / u_resolution.y;

        vec2 norm = vec2(fs_in.TexCoords.s, dst) * 2.0 - 1.0;
        float theta = PI * 1.5 + norm.x * PI;
        float r = (1.0 + norm.y) * 0.5;

        vec2 coord = vec2(-r * sin(theta), -r * cos(theta)) * 0.5 + 0.5;
        float datum = texture(u_texture, fs_in.TexCoords).b;
        if(datum < THRESHOLD)
            distance = min(distance, dst);
    }

    Lighting = vec4(vec3(distance), 1.0);

}
