#version 330

out vec4 Distortion;

uniform sampler2D u_texture;

in vec4 Colour;
in vec3 Position;
in vec2 TexCoords;

void main() {
	vec4 texSample = texture(u_texture, TexCoords);

	texSample = texSample * 2.0 - 1.0;

    float x = texSample.r;
    float y = texSample.g;
	float sampleStrength = texSample.b * 0.5 + 0.5;
	float strength = texSample.b;

	sampleStrength = 0.0;

	vec2 sampleOffset = vec2(x, y);
	vec2 compOffset = TexCoords - vec2(0.5);

	vec2 offset = mix(compOffset, sampleOffset, sampleStrength);
	Distortion = vec4(offset, 0.0, strength) * 0.5 + 0.5;
}
