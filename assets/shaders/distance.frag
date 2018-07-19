#version 330

out vec4 FinalColour;

uniform sampler2D u_texture;

in vec3 Position;
in vec2 TexCoords;

float ComputeDistance(vec2 coords) {
    vec4 colour = texture(u_texture, coords);
    float distance = colour.b > 0.3 ? length(coords - vec2(0.5)) : 1.0;
    return distance;
}

void main() {
    float dst = ComputeDistance(TexCoords);
	FinalColour = vec4(vec3(dst), 1.0);
}
