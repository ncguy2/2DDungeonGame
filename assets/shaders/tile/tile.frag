#version 330

out vec4 Colour;

in VS_OUT {
    vec4 Position;
    vec4 Colour;
    vec2 TexCoords;
} fs_in;

uniform sampler2D u_texture;

uniform int u_neighbourSolidMask;

void main() {

    vec2 texCoords = fs_in.TexCoords;

    bool tl = (u_neighbourSolidMask & 0xF0000000) == 0xF0000000;
    bool tm = (u_neighbourSolidMask & 0x0F000000) == 0x0F000000;
    bool tr = (u_neighbourSolidMask & 0x00F00000) == 0x00F00000;

    bool ml = (u_neighbourSolidMask & 0x000F0000) == 0x000F0000;
    bool mr = (u_neighbourSolidMask & 0x0000F000) == 0x0000F000;

    bool bl = (u_neighbourSolidMask & 0x00000F00) == 0x00000F00;
    bool bm = (u_neighbourSolidMask & 0x000000F0) == 0x000000F0;
    bool br = (u_neighbourSolidMask & 0x0000000F) == 0x0000000F;

    if(texCoords.x <= .5) {
        if(texCoords.y <= .5) {
            // tl, tm, ml

        }else{
            // tm, tr, mr
        }
    }else{
        if(texCoords.y <= .5) {

        }else{

        }
    }

    texCoords.y *= .2;

    Colour = texture(u_texture, texCoords);
//    Colour = vec4(texCoords, 0.0, 1.0);

}
