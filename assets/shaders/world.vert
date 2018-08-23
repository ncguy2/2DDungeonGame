#version 330

layout (location = 0) in vec4 a_position;
layout (location = 1) in vec4 a_color;
layout (location = 2) in vec2 a_texCoord0;

uniform mat4 u_projTrans;

out VS_OUT {
    vec4 Position;
    vec4 Colour;
    vec2 TexCoords;
} vs_out;

void main() {

    vs_out.Position = a_position;
    vs_out.Colour = a_color;
    vs_out.TexCoords = a_texCoord0;

	gl_Position = u_projTrans * a_position;
}
