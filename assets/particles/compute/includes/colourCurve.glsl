#define MAX_CURVE_SIZE 8

struct CurveEntry {
    float Key;
    vec4 Value;
};

struct Curve {
    int Length;
    CurveEntry Entries[MAX_CURVE_SIZE];
};

uniform Curve u_curve;

int Get(in Curve curve, in float alpha, out CurveEntry entry) {
    for(int i = 0; i < curve.Length; i++) {
        if(alpha <= curve.Entries[i].Key) {
            entry = curve.Entries[i - 1];
            return i - 1;
        }
    }
    return -1;
}

vec4 Sample(in Curve curve, in float alpha) {
    if(alpha <= curve.Entries[0].Key)
        return curve.Entries[0].Value;

    if(alpha >= curve.Entries[curve.Length - 1].Key)
        return curve.Entries[curve.Length - 1].Value;

    CurveEntry a;
    CurveEntry b;

    int idx = Get(curve, alpha, a);
    if(idx > -1) {
        b = curve.Entries[idx + 1];

        float bNorm = b.Key - a.Key;
        float alphaNorm = alpha - a.Key;
        float normalized = alphaNorm / bNorm;
        return mix(a.Value, b.Value, normalized);
    }

    return vec4(0.0);
}

