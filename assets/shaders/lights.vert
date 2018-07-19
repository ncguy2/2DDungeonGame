#version 330

layout (location = 0) in vec3 a_position;
layout (location = 1) in vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;

out VS_OUT {
    vec4 Position;
    vec2 TexCoords;
    vec2 ScreenPosition;
} vs_out;

void main() {

    vs_out.Position = u_projTrans * vec4(a_position, 1.0);
    vs_out.TexCoords = a_texCoord0;
    vs_out.ScreenPosition = (inverse(u_viewMatrix) * vec4(a_position, 1.0)).xy;

	gl_Position = u_projTrans * vec4(a_position, 1.0);
}
