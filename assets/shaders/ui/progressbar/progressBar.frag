#version 330

out vec4 FinalColour;

in VS_OUT {
    vec4 Position;
    vec4 Colour;
    vec2 TexCoords;
} fs_in;

struct Colours {
    vec4 Empty;
    vec4 Full;
    vec4 Overfill;
};

uniform Colours u_colours;
uniform sampler2D u_texture;

uniform float value;
uniform float overfill;

void main() {
    vec4 colour;

    float x = fs_in.TexCoords.x;
    if(x <= overfill)
        colour = u_colours.Overfill;
    else if(x <= value)
        colour = u_colours.Full;
    else colour = u_colours.Empty;

	FinalColour = texture(u_texture, fs_in.TexCoords) * colour;
}
