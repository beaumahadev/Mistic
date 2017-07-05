#define MAX_FIREFLIES 50

uniform vec2 res;

uniform vec2 fireflies[MAX_FIREFLIES];
uniform int numFireflies;
uniform float alphas[MAX_FIREFLIES];

void main() {
    vec3 glowColor = vec3(1.0, 1.0, .5);

    float attenuations[MAX_FIREFLIES];

    for (int i=0; i<MAX_FIREFLIES; i++) {
        if (i>=numFireflies) {
            break;
        }

        vec2 dir = vec2(fireflies[i].x+.04, fireflies[i].y+.07) - (gl_FragCoord.xy / res.xy);
        dir.x *= res.x / res.y;

        float D = length(dir);

        vec3 falloff = vec3(.2, 9.0, 90.0);
        attenuations[i] = .4 / ( falloff.x + (falloff.y*D) + (falloff.z*D*D) ) * alphas[i];

//        float radius = .04;
//        attenuations[i] = pow(clamp(1.0 - D*D/(radius*radius), 0.0, 1.0), 2.0) * .8;
    }

    gl_FragColor.rgb = glowColor;
    for (int i=0; i<MAX_FIREFLIES; i++) {
        if (i>=numFireflies) {
            break;
        }
        gl_FragColor.a = max(gl_FragColor.a, attenuations[i]);
    }
}
