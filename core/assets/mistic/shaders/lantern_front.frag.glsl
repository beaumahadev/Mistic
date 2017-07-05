#define MAX_LANTERNS 30

uniform vec2 res;

uniform vec2 lanternsPos[MAX_LANTERNS];
uniform int numLanterns;
uniform vec2 offset[MAX_LANTERNS];

void main() {
    // Lantern Glows
    vec3 lanternGlowColor = vec3(1.0, .9, .3);
    float lanternAttenuations[MAX_LANTERNS];

    for (int i=0; i<MAX_LANTERNS; i++) {
        if (i>=numLanterns) {
            break;
        }
//        vec2 lanternDir = vec2(min(lanternsPos[i].x-.004, 1.0 - (lanternsPos[i].x-.004)) - gl_FragCoord.x / res.x, min(lanternsPos[i].y+.17, 1.0 - (lanternsPos[i].y+.17)) - gl_FragCoord.y / res.y);
        vec2 lanternDir = vec2(lanternsPos[i].x-.004, lanternsPos[i].y+.17) - (gl_FragCoord.xy / res.xy);
//        vec2 lanternDir = vec2(min(lanternsPos[i].x-.004 - (gl_FragCoord.x / res.x), 1.0 - (lanternsPos[i].x-.004-offset.x) - (gl_FragCoord.x / res.x)), min(lanternsPos[i].y+.17 - (gl_FragCoord.y / res.y), 1.0 - (lanternsPos[i].y+.17-offset.y) - (gl_FragCoord.y / res.y)));

        lanternDir.x *= res.x / res.y;

        float D = length(lanternDir);
        vec3 lanternFalloff = vec3(.6, 4.0, 40.0);
        lanternAttenuations[i] = 1.0 / (lanternFalloff.x + lanternFalloff.y*D + lanternFalloff.z*D*D) * .6;
    }

    gl_FragColor = vec4(lanternGlowColor, 0.0);
    for (int i=0; i<MAX_LANTERNS; i++) {
        if (i>=numLanterns) {
            break;
        }
        gl_FragColor.a = max(gl_FragColor.a, lanternAttenuations[i]);
    }
}
