#version 330

layout (location = 0) out vec4 Diffuse;
layout (location = 1) out vec4 Normal;
layout (location = 2) out vec4 Emissive;
layout (location = 3) out vec4 TexCoordsOcclusion;

in VS_OUT {
    vec4 Position;
    vec4 Colour;
    vec2 TexCoords;
} fs_in;

uniform sampler2D u_texture;
uniform int u_castShadow;
uniform float u_alphaTest = .1;

uniform vec4 u_diffuseColour;

void main() {

	Diffuse = texture(u_texture, fs_in.TexCoords) * fs_in.Colour * u_diffuseColour;

	if(Diffuse.a <= u_alphaTest)
	    discard;

	TexCoordsOcclusion.rg = fs_in.TexCoords;
	TexCoordsOcclusion.b = float(u_castShadow) * Diffuse.a;
	TexCoordsOcclusion.a = 1.0;

    Normal = vec4(fs_in.Position.xyz, 1.0);

}
