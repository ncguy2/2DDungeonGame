#version 330
#define PI 3.14
#define TAU (PI*2)

out vec4 FinalColour;

    in vec3 Position;
    in vec2 TexCoords;

uniform sampler2D u_texture;
uniform sampler2D u_BaseColour;
uniform sampler2D u_Lighting;

uniform float gamma = 2.2;
uniform float intensity = 0.5;

void main() {

    vec2 coords = TexCoords;
    coords.y = 1 - coords.y;

//    FinalColour = vec4(fs_in.TexCoords, 0.0, 1.0);
    FinalColour = texture(u_BaseColour, coords);

    vec3 lightColour = texture(u_texture, coords).rgb;

    lightColour *= intensity;
    lightColour = pow(lightColour, vec3(1.0 / gamma));

    FinalColour.rgb += max(lightColour.rgb, vec3(0.0));
//    FinalColour.a = 1.0;
}
