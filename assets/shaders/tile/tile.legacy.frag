#version 330

out vec4 Colour;


in VS_OUT {
    vec4 Position;
    vec4 Colour;
    vec2 TexCoords;
} fs_in;

uniform sampler2D u_texture;
uniform vec2 gridCount;
uniform vec2 worldSize;

uniform bool u_map[256];

vec4 GetColour(int idx) {
    if(idx == 0)
        return vec4(1.0, 0.0, 0.0, 1.0);
    if(idx == 1)
        return vec4(1.0, 1.0, 0.0, 1.0);
    if(idx == 2)
        return vec4(0.0, 1.0, 0.0, 1.0);
    if(idx == 3)
        return vec4(0.0, 1.0, 1.0, 1.0);
    if(idx == 4)
        return vec4(0.0, 0.0, 1.0, 1.0);
    if(idx == 5)
        return vec4(1.0, 0.0, 1.0, 1.0);
    if(idx == 6)
        return vec4(1.0, 1.0, 1.0, 1.0);

    return vec4(1.0);
}

int CoordsToIdx(vec2 coords) {
    return int(round((coords.y * gridCount.x) + coords.x));
}

bool GetNeighbour(vec2 thisCoords, vec2 direction) {
    int idx = CoordsToIdx(thisCoords + direction);
    return u_map[idx];
}

void main() {

    vec2 cellSize = worldSize / gridCount;

    vec2 texCoords = fs_in.TexCoords;
    vec2 worldCoords = texCoords * worldSize;

    vec2 gridCoords = floor(texCoords * gridCount);
    int idx = int(round((gridCoords.y * gridCount.x) + gridCoords.x));

    worldCoords = mod(worldCoords, cellSize);

    texCoords = worldCoords / cellSize;
//    Colour = vec4(texCoords, 0.0, 1.0);

    texCoords.y *= .2;

    GetNeighbour(gridCoords, vec2(1.0));

    Colour = texture(u_texture, texCoords) * GetColour(int(mod(idx, 7)));

}
