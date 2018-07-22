#version 330

out vec4 Result;

uniform sampler2D u_texture;
uniform sampler2D u_distortionMap;

in vec4 Colour;
in vec3 Position;
in vec2 TexCoords;

void main() {
	vec4 map = texture(u_distortionMap, TexCoords) * 2.0 - 1.0;

	vec2 coords = TexCoords;
	coords += (map.rg * 0.01) * map.a;
//	coords.y = 1 - mod(coords.y, 1.0);

	Result = texture(u_texture, coords);
}
