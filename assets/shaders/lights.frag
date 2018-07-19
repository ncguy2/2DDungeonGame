#version 330
#define PI 3.14

out vec4 Lighting;

in VS_OUT {
    vec4 Position;
    vec2 TexCoords;
    vec2 ScreenPosition;
} fs_in;

uniform float u_radius;
uniform vec2 u_lightPos;
uniform vec2 u_lightScreenPos;
uniform vec4 u_lightColour;

uniform mat4 u_projectionMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_combinedMatrix;
uniform mat4 u_projTrans;

uniform sampler2D u_occluderMap;
uniform int u_occluderMapChannel;

uniform float safeRadiusPixels;
uniform float safeRadiusPixelBias;
vec2 safeRadius;

//// Returns 1 if nothing is found
//// Returns 0 if something is found
//float rayMarch(vec2 start, float stepSize, int stepCount, vec2 direction, out vec3 sampledCoords[128]) {
//    vec2 step = direction * stepSize;
//    for(int i = stepCount; i > 0; i--) {
//        vec2 coords = start + (step * i);
//        vec4 tmp = (u_combinedMatrix * vec4(coords, 0.0, 1.0));
//        coords = (tmp.xyz / tmp.w).xy;
//        coords.xy = coords.xy * 2.0 - 1.0;
////        coords.xy = coords.xy * 0.5 + 0.5;
////        coords.y = 1.0 - coords.y;
//
//        float occluderValue = texture(u_occluderMap, vec2(coords))[u_occluderMapChannel];
//        sampledCoords[int(mod(i, 128))] = vec3(coords, 1 - occluderValue);
//        if(occluderValue > .5)
//            return 0;
//    }
//    return 1;
//}

bool inBounds(vec2 coords) {
    if(coords.x < 0.0 || coords.x > 1.0)
        return false;
    if(coords.y < 0.0 || coords.y > 1.0)
        return false;
    return true;
}

float Sample(vec2 point, vec2 diff, float kernel[9]) {
    float val = 0;

    vec2 offsets[9] = vec2[](
        vec2(-1, -1), vec2(0, -1), vec2(1, -1),
        vec2(-1,  0), vec2(0,  0), vec2(1,  0),
        vec2(-1,  1), vec2(0,  1), vec2(1,  1)
    );

    for(int i = 0; i < 9; i++) {
        vec2 coords = point + (diff * offsets[i]);
        val += texture(u_occluderMap, vec2(coords))[u_occluderMapChannel] * kernel[i];
    }

    return val;
}

float RayMarch(vec2 start, vec2 end, int maxSteps, float stepSize, vec2 texelSize, out vec2 stepDirection) {
//    vec2 step = texelSize;
    vec2 step = normalize(end - start);
    step *= -1;
    step *= texelSize;
    step *= stepSize;
    stepDirection = step;
    float intensity = 0;
    float suggestedStepCount = (1.0 / max(texelSize.x, texelSize.y));

//    int stepCount = int(min(int(round(suggestedStepCount)), maxSteps));
//    int stepCount = int(round(suggestedStepCount));
    int stepCount = maxSteps;

    float rayDst = 0;

    float kernelA[9] = float[](
        sqrt(2), 1, sqrt(2),
        1, 0, 0,
        0, 0, 0
    );
    float kernelB[9] = float[](
        0, 0, 0,
        0, 0, 1,
        sqrt(2), 1, sqrt(2)
    );

//    while(int i = 0; i < stepCount; i++) {
    for(int i = 0; i < stepCount; i++) {
//    for(int i = stepCount; i > 0; i--) {
        vec2 coords = start + (step * i);

        if(!inBounds(coords))
            return intensity;

        float rayDst = distance(end, coords);
        if(rayDst < safeRadius.x || rayDst < safeRadius.y)
            continue;

        float occluderValue = Sample(coords, texelSize, kernelA) + Sample(coords, texelSize, kernelB) * .5;

//        float occluderValue = texture(u_occluderMap, vec2(coords))[u_occluderMapChannel];
        if(occluderValue > 0.5)
            return 1;
    }
    return intensity;
}

void main() {

    vec2 coords = fs_in.TexCoords;
    coords.y = 1 - coords.y;
    float occluderValue = texture(u_occluderMap, vec2(coords))[u_occluderMapChannel];
//    if(occluderValue > 0.5)
//        discard;

    vec3 worldPos = (inverse(u_combinedMatrix) * fs_in.Position).xyz;
    vec3 lightPos = vec3(u_lightPos, 0.0);
//    vec4 screenLightPosTmp = (u_combinedMatrix * vec4(u_lightPos, 0.0, 1.0));
//    vec3 screenLightPos = screenLightPosTmp.xyz * 0.5 + 0.5;
    vec3 screenLightPos = vec3(u_lightScreenPos, 0.0);

    float origDst = distance(worldPos.xy, u_lightPos);
    float dst = origDst;

    if(dst > u_radius)
        discard;

    vec2 texelSize = 1.0 / textureSize(u_occluderMap, 0);

    safeRadius = vec2(safeRadiusPixels + (safeRadiusPixels * safeRadiusPixelBias)) / textureSize(u_occluderMap, 0);

    vec2 start = fs_in.TexCoords.xy;
    vec2 end = screenLightPos.xy;
    start.y = 1 - start.y;
//    end.y = 1 - end.y;

//    float ray = rayMarch(lightPos.xy, stepSize, stepCount, direction.xy, sampledCoords);
    vec2 step;
    float ray = RayMarch(start, end, 32, 4, texelSize, step);

    ray = clamp(ray - occluderValue, 0.0, 1.0);
    ray = 1 - ray;

    // Normalize
    dst *= 1.5;
    dst /= u_radius;
    dst = clamp(dst, 0.0, 1.0);
    dst = 1 - dst;
    dst *= 1 - occluderValue;

    dst = smoothstep(0.0, 1.0, dst);

    Lighting = vec4(vec3(ray * dst), 1.0) * u_lightColour;
    Lighting += vec4(occluderValue, 0.0, 0.0, 1.0);

    if(origDst < 40)
        Lighting += vec4(0.0, 0.0, 1.0, 1.0);

    Lighting += vec4(step * 10, 0.0, 1.0);

//    Lighting = vec4(occluderValue, dst, ray, 1.0) * u_lightColour;
//    Lighting = vec4(fs_in.TexCoords.xy, 0.0, 1.0) * u_lightColour;
//    Lighting += vec4(screenLightPos.xy, 0.0, 1.0) * u_lightColour;

}
