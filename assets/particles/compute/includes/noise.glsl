uniform float u_noiseScale = .001;
mat2 mm2(in float a){float c = cos(a), s = sin(a);return mat2(c,-s,s,c);}
float tri(in float x){return abs(fract(x)-.5);}
vec2 tri2(in vec2 p){return vec2(tri(p.x+tri(p.y*2.)),tri(p.y+tri(p.x*2.)));}
mat2 m2 = mat2( 0.970,  0.242, -0.242,  0.970 );

float triangleNoise(in vec2 p)
{
    float z=1.5;
    float z2=1.5;
	float rz = 0.;
    vec2 bp = p;
	for (float i=0.; i<=3.; i++ )
	{
        vec2 dg = tri2(bp*2.)*.8;
        dg *= mm2(gTime*.3);
        p += dg/z2;

        bp *= 1.6;
        z2 *= .6;
		z *= 1.8;
		p *= 1.2;
        p*= m2;

        rz+= (tri(p.x+tri(p.y)))/z;
	}
	return rz;
}

float GetAngle(in vec2 p, in float scale) {
    float alpha = triangleNoise(p * vec2(scale));
    return alpha = radians(alpha * 360.0);
}

vec2 GetDirection(in vec2 p, in float scale) {
    float angle = GetAngle(p, scale);
    vec2 q = vec2(cos(angle), sin(angle));
    return q;
}