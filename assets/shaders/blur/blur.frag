#version 330

out vec4 FragColour;

in vec2 TexCoords;

uniform sampler2D u_texture;

uniform int kernelSize = 5;

uniform bool u_horizontal;
uniform float weight[5] = float[] (0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);

void main() {
    vec2 texOffset = 1.0 / textureSize(u_texture, 0);
    vec3 result = texture(u_texture, TexCoords).rgb * weight[0];

    if(u_horizontal) {
        for(int i = 1; i < kernelSize; i++) {
            result += texture(u_texture, TexCoords + vec2(texOffset.x * i, 0.0)).rgb * weight[i % 5];
            result += texture(u_texture, TexCoords - vec2(texOffset.x * i, 0.0)).rgb * weight[i % 5];
        }
    }else{
        for(int i = 1; i < kernelSize; i++) {
            result += texture(u_texture, TexCoords + vec2(0.0, texOffset.y * i)).rgb * weight[i % 5];
            result += texture(u_texture, TexCoords - vec2(0.0, texOffset.y * i)).rgb * weight[i % 5];
        }
    }

    FragColour = vec4(result, texture(u_texture, TexCoords).a);
//    FragColour = vec4(TexCoords, 0.0, 1.0);

}