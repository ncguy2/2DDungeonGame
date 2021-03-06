#version 330

layout (location = 0) in vec3 a_position;
layout (location = 1) in vec2 a_texCoord0;
layout (location = 2) in vec4 a_color;

uniform mat4 u_projTrans;

    out vec4 Colour;
    out vec3 Position;
    out vec2 TexCoords;

void main() {

    Position = a_position;
    TexCoords = a_texCoord0;
    Colour = a_color;

	gl_Position = u_projTrans * vec4(a_position, 1.0);
}
