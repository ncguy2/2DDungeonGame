#version 330

out vec4 Bloom;

uniform sampler2D u_texture;
uniform sampler2D u_light;
uniform sampler2D u_particles;

in vec4 Colour;
in vec3 Position;
in vec2 TexCoords;

vec4 blur13(sampler2D image, vec2 uv, vec2 resolution, vec2 direction) {
  vec4 color = vec4(0.0);
  vec2 off1 = vec2(1.411764705882353) * direction;
  vec2 off2 = vec2(3.2941176470588234) * direction;
  vec2 off3 = vec2(5.176470588235294) * direction;
  color += texture2D(image, uv) * 0.1964825501511404;
  color += texture2D(image, uv + (off1 / resolution)) * 0.2969069646728344;
  color += texture2D(image, uv - (off1 / resolution)) * 0.2969069646728344;
  color += texture2D(image, uv + (off2 / resolution)) * 0.09447039785044732;
  color += texture2D(image, uv - (off2 / resolution)) * 0.09447039785044732;
  color += texture2D(image, uv + (off3 / resolution)) * 0.010381362401148057;
  color += texture2D(image, uv - (off3 / resolution)) * 0.010381362401148057;
  return color;
}

void main() {
	vec4 texSample = texture(u_texture, TexCoords);

	vec2 dir = normalize(vec2(0.5) - TexCoords);

//	vec4 partSample = texture(u_particles, TexCoords);
	vec4 partSample = blur13(u_particles, TexCoords, textureSize(u_particles, 0), dir);

	texSample.rgb += partSample.rgb;

    Bloom = texSample;
}
