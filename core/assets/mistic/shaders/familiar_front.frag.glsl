uniform vec2 res;

uniform vec2 familiarPos;

void main() {
    vec3 familiarGlowColor = vec3(0.7, 1.0, 1.0);

    vec2 familiarDir = familiarPos - (gl_FragCoord.xy / res.xy);
    familiarDir.x *= res.x / res.y;

    float D = length(familiarDir);

    vec3 familiarFalloff = vec3(1.0, 4.0, 40.0);
    float familiarAttenuation = 1.0 / ( familiarFalloff.x + (familiarFalloff.y*D) + (familiarFalloff.z*D*D) ) * .8;
//    float familiarAttenuation = pow(clamp(1.0 - D*D/(.2*.2), 0.0, 1.0), 2.0) * .5;

    gl_FragColor.rgb = familiarGlowColor;
    gl_FragColor.a = familiarAttenuation;
}
