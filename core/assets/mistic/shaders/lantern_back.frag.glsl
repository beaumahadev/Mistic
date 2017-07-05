#define MAX_LANTERNS 30

uniform vec2 res;

uniform vec2 lanternsPos[MAX_LANTERNS];
uniform int numLanterns;

void main() {
    // Lantern Glows
    vec3 lanternGlowColor = vec3(1.0, .9, .3);
    float lanternAttenuations[MAX_LANTERNS];

    for (int i=0; i<MAX_LANTERNS; i++) {
        if (i>=numLanterns) {
            break;
        }
//        vec2 lanternDir = vec2(min(lanternsPos[i].x-.004 - gl_FragCoord.x / res.x, lanternsPos[i].x-.004 - (res.x - gl_FragCoord.x) / res.x), min(lanternsPos[i].y+.17 - gl_FragCoord.y / res.y, lanternsPos[i].y+.17 - (res.y - gl_FragCoord.y) / res.y));
        vec2 lanternDir = vec2(lanternsPos[i].x-.004, lanternsPos[i].y+.1) - (gl_FragCoord.xy / res.xy);
//        vec2 lanternDir = vec2(min(abs(lanternsPos[i].x-.004 - (gl_FragCoord.x / res.x)), abs(lanternsPos[i].x-.004-res.x - (gl_FragCoord.x / res.x))), min(abs(lanternsPos[i].y+.17 - (gl_FragCoord.y / res.y)), abs(lanternsPos[i].y+.17-res.y - (gl_FragCoord.y / res.y))));
        lanternDir.x *= res.x / res.y;

        float D = length(lanternDir);
        float radius = .45;
        lanternAttenuations[i] = pow(clamp(1.0 - D*D/(radius*radius), 0.0, 1.0), 2.0) * .8;
    }

    gl_FragColor = vec4(lanternGlowColor, 0.0);
    for (int i=0; i<MAX_LANTERNS; i++) {
        if (i>=numLanterns) {
            break;
        }
        gl_FragColor.a = max(gl_FragColor.a, lanternAttenuations[i]);
    }
}
