#version 330

out vec4 FinalColour;

in vec4 Colour;
in vec3 Position;
in vec2 TexCoords;

uniform sampler2D u_texture;
uniform sampler2D u_cloud;

uniform float u_time;
uniform float u_windScale;
uniform float u_colourStrength;
uniform vec2 u_windVelocity;

uniform bool u_flipTexture;

float interp(float a) {
    // smooth
//    a = a * a * (3 - 2 * a);
    // smoother
    a = a * a * a * (a * (a * 6 - 15) + 10);

    int power = 2;
    // powOut
    a = pow(a - 1, power) * (power % 2 == 0 ? -1 : 1) + 1;
    return a;
}

void main() {

    vec2 coords = TexCoords;
    if(u_flipTexture)
        coords.y = 1 - coords.y;

    vec4 baseTex = texture(u_texture, coords);

    vec2 cloudCoords = coords;
    cloudCoords += ((u_windVelocity.yx * -u_windScale));
    vec4 cloudTex = texture(u_cloud, cloudCoords);

    vec4 col = baseTex;

    float dst = distance(coords, vec2(0.5));

    float mask = interp(dst);

    vec4 cloudCol = cloudTex * mask;
    col += cloudCol * u_colourStrength;

	FinalColour = col;
}
