#define NX 31
#define NY 31

#define MAX_LANTERNS 30

#define BOUNDARY .8
uniform vec2 fogOrigin;
uniform sampler2D u_texture;
uniform sampler2D u_texture_perlin;

//uniform sampler2D u_texture_sew;
uniform sampler2D u_texture_new;
//uniform sampler2D u_texture_nsw;
//uniform sampler2D u_texture_nse;
//uniform sampler2D u_texture_ew;
uniform sampler2D u_texture_ns;
//uniform sampler2D u_texture_nw;
//uniform sampler2D u_texture_sw;
//uniform sampler2D u_texture_se;
uniform sampler2D u_texture_ne;
//uniform sampler2D u_texture_w;
//uniform sampler2D u_texture_s;
//uniform sampler2D u_texture_e;
uniform sampler2D u_texture_n;

uniform vec2 res;//The width and height of our screen
uniform vec2 dim;
uniform float fogBoard[NX*NY];
uniform vec2 lanterns[MAX_LANTERNS];
//uniform vec2 fogReachVec;
//uniform float fogReach;
uniform int numLanterns;
//uniform int numFireflies;
//uniform float thickness;
uniform vec2 offset;

uniform vec2 tileDim;
uniform vec2 familiarPos;

uniform float gorfRadius;

varying vec2 vTexCoord;


void main() {
    vec2 coord = (gl_FragCoord.xy / res.xy);      // FUTURE NOTE: should get coord in terms of world/map -- convert gl_FragCoord to world space by adding dim/2 and subtracting cameraPos, then divide by world width
    vec2 boardCoord = gl_FragCoord.xy / dim.xy + vec2(offset.x, offset.y);
//    vec2 origin = fogOrigin;
//    coord.x *= (res.x/res.y);
//    origin *= (dim.x/dim.y);
//    float fogReach = pow(coord.x, 2) / pow(fogReach.x/NX, 2) + pow(coord.y, 2) / pow(fogReach.y/NY, 2);
//    float dist = length((coord-origin)*(dim.x/dim.y));
//    float dx = min(abs(coord.x-origin.x), abs(coord.x + (2.0-origin.x)));       // FUTURE NOTE: upper bound (currently 1) should be the world map width normalized in terms of screen [0.1], i.e if map was 2x width of screen, then upper bound = 2
//    dx = min(dx, abs(origin.x + (2.0-coord.x)));
//    float dy = min(abs(coord.y-origin.y), abs(coord.y + (2.0-origin.y)));
//    dy = min(dy, origin.y + abs((2.0-coord.y)));
//    float theta = atan(dy/dx);
//    float fogReach = (fogReachVec.x*fogReachVec.y) / sqrt(pow(fogReachVec.x*sin(theta), 2) + pow(fogReachVec.y*cos(theta), 2));
//    float dist = length(vec2(dx,dy));
    int cellX = int(boardCoord.x*float(NX));
    int cellY = int(boardCoord.y*float(NY));

//    float fogThickness = 1.0-smoothstep(fogReach/float(NX)-.5, fogReach/float(NX), dist);

    vec4 texColor = texture2D(u_texture, vTexCoord);



    vec3 fog = vec3(.5,0.0,.65);

//    fogThickness *= fogBoard[cellY*NX + cellX];
//      float fogThickness = fogBoard[cellY*NX + cellX];

    float fogThickness = 0.0;
    vec2 boundaryTexCoord1 = vec2(mod(gl_FragCoord.x + offset.x*dim.x, tileDim.x) / tileDim.x / 21.0, 1.0 - mod(gl_FragCoord.y+offset.y*dim.y, tileDim.y) / tileDim.y);
    vec2 boundaryTexCoord2 = vec2(mod(gl_FragCoord.y + offset.y*dim.y, tileDim.y) / tileDim.y / 21.0, 1.0 - mod(gl_FragCoord.x+offset.x*dim.x, tileDim.x) / tileDim.x);
    vec2 boundaryTexCoord3 = vec2(1.0 - mod(gl_FragCoord.x + offset.x*dim.x, tileDim.x) / tileDim.x / 21.0, mod(gl_FragCoord.y+offset.y*dim.y, tileDim.y) / tileDim.y);
    vec2 boundaryTexCoord4 = vec2(1.0 - mod(gl_FragCoord.y + offset.y*dim.y, tileDim.y) / tileDim.y / 21.0, mod(gl_FragCoord.x+offset.x*dim.x, tileDim.x) / tileDim.x);
    vec2 boundaryTexCoord5 = vec2(mod(gl_FragCoord.x + offset.x*dim.x, tileDim.x) / tileDim.x / 21.0, mod(gl_FragCoord.y+offset.y*dim.y, tileDim.y) / tileDim.y);
    vec2 boundaryTexCoord6 = vec2(1.0 - mod(gl_FragCoord.x + offset.x*dim.x, tileDim.x) / tileDim.x / 21.0, 1.0 - mod(gl_FragCoord.y+offset.y*dim.y, tileDim.y) / tileDim.y);

//    vec2 boundaryTexCoord2 = vec2(mod(gl_FragCoord.y + offset.y*dim.y, tileDim.y) / tileDim.y, 1.0 - mod(gl_FragCoord.x+offset.x*dim.x, tileDim.x) / tileDim.x);
//    vec2 boundaryTexCoord3 = vec2(1.0 - mod(gl_FragCoord.x + offset.x*dim.x, tileDim.x) / tileDim.x, mod(gl_FragCoord.y+offset.y*dim.y, tileDim.y) / tileDim.y);
//    vec2 boundaryTexCoord4 = vec2(1.0 - mod(gl_FragCoord.y + offset.y*dim.y, tileDim.y) / tileDim.y, mod(gl_FragCoord.x+offset.x*dim.x, tileDim.x) / tileDim.x);

    if (fogBoard[cellY*NX + cellX] == 1.0 || fogBoard[cellY*NX + cellX] == .7) {
        fogThickness = 1.0;
    } else if (fogBoard[cellY*NX + cellX] > 1.0) {
//        int idx = 2;
        float idx = floor(fogBoard[cellY*NX + cellX] / 4.0) - 1.0;
        int orientation = int(mod(fogBoard[cellY*NX + cellX], 4.0));

        vec2 boundaryTexCoord;
        if (orientation == 0) {
            boundaryTexCoord = vec2(mod(gl_FragCoord.x + offset.x*dim.x, tileDim.x) / tileDim.x / 23.0 + idx/23.0, 1.0 - mod(gl_FragCoord.y+offset.y*dim.y, tileDim.y) / tileDim.y);
        } else if (orientation == 1) {
            boundaryTexCoord = vec2((1.0 - mod(gl_FragCoord.y + offset.y*dim.y, tileDim.y) / tileDim.y) / 23.0 + idx/23.0, 1.0 - mod(gl_FragCoord.x+offset.x*dim.x, tileDim.x) / tileDim.x);
        } else if (orientation == 2) {
            boundaryTexCoord = vec2((1.0 - mod(gl_FragCoord.x + offset.x*dim.x, tileDim.x) / tileDim.x) / 23.0 + idx/23.0, mod(gl_FragCoord.y+offset.y*dim.y, tileDim.y) / tileDim.y);
        } else {
            boundaryTexCoord = vec2(mod(gl_FragCoord.y + offset.y*dim.y, tileDim.y) / tileDim.y / 23.0 + idx/23.0, mod(gl_FragCoord.x+offset.x*dim.x, tileDim.x) / tileDim.x);
        }

        fogThickness = texture2D(u_texture, boundaryTexCoord).a;
    }

//    float attenuationLantern = 0.0f;
//    vec3 lanternColor = vec3(1.0, 1.0, 0.2);

//    vec3 lanternColor = vec3(1.0, 1.0, .2);
//    float lanternAttenuations[MAX_LANTERNS];

    for (int i=0; i<MAX_LANTERNS; i++) {
        if (i>=numLanterns) {
            break;
        }

        vec2 lantern = lanterns[i];
        float dx2 = abs(coord.x+.004-lantern.x) * (res.x/res.y);
        float dy2 = abs(coord.y-.1-lantern.y);

        float dist2 = length(vec2(dx2,dy2));
        float fogThickness2 = smoothstep(.25, .45, dist2);

//        vec3 falloff2 = vec3(0.8, 2.0, 20.0);
//        float D2 = length(vec2(dx2, dy2-.06));
//        float attenuation2 = 1.0 / ( falloff2.x + (falloff2.y*D2) + (falloff2.z*D2*D2) );

        fogThickness *= fogThickness2;
//        attenuationLantern = max(attenuation2, attenuationLantern);



//        vec2 lightDirLantern = vec2(lantern.x-.01, lantern.y+.17) - (gl_FragCoord.xy / res.xy);
//    //    // Correct for aspect ratio
//        lightDirLantern.x *= res.x / res.y;
//    //
//        float D = length(lightDirLantern);
//    //
//        vec3 falloffLantern = vec3(.5, 6.0, 10.0);
//        lanternAttenuations[i] = 1.0 / ( falloffLantern.x + (falloffLantern.y*D) + (falloffLantern.z*D*D) );
    }

    float dx3 = abs(coord.x-.5) * (res.x/res.y);
    float dy3 = abs(coord.y-.5);

    float dist3 = length(vec2(dx3, dy3));

    fogThickness *= min(1.0, .05 + smoothstep(gorfRadius, .25+gorfRadius, dist3));


    float dx4 = (familiarPos.x - gl_FragCoord.x / res.x) * (res.x/res.y);
    float dy4 = familiarPos.y - gl_FragCoord.y / res.y;
    float dist4 = length(vec2(dx4, dy4));
    fogThickness *= min(1.0, smoothstep(0.0, .15, dist4));

//    fogThickness *= min(1.0, texture2D(u_texture_perlin, vTexCoord).a+.4);
//    fog *= min(1.0,fogThickness);
//    fog *= max(.7, texture2D(u_texture_perlin, vTexCoord).a);

//    vec3 glowColor = vec3(0.7, 1.0, 1.0);
//
//    vec2 lightDir = familiarPos - (gl_FragCoord.xy / res.xy);
////    // Correct for aspect ratio
//    lightDir.x *= res.x / res.y;
////
//    float D = length(lightDir);
////
////    vec3 N = vec3(0.0, 0.0, 1.0);
////    vec3 L = normalize(lightDir);
////
////    vec3 diffuse = (glowColor.rgb * glowColor.a) * max(dot(N, L), 0.0);
////
//    vec3 falloff = vec3(1.0, 2.0, 60.0);
//    float attenuation = 1.0 / ( falloff.x + (falloff.y*D) + (falloff.z*D*D) );
//
////    vec3 intensity = diffuse * attenuation;


    gl_FragColor.rgb = fogThickness * fog * texture2D(u_texture_perlin, vTexCoord).a;
//    gl_FragColor.rgb += (1.0 - fogThickness)*glowColor*attenuation;
//    for (int i=0; i<MAX_LANTERNS; i++) {
//        if (i>=numLanterns) {
//            break;
//        }
//        gl_FragColor.rgb += (1.0 - fogThickness)*lanternColor*lanternAttenuations[i];
//        gl_FragColor.a = max(fogThickness, lanternAttenuations[i]);
//    }
    gl_FragColor.a = fogThickness;
//    gl_FragColor.a = 0;
//    gl_FragColor = vec4(fog, fogThickness);
//    gl_FragColor.rgb *= max(0.0,1.0-fogThickness);
//    gl_FragColor.rgb += fog;

//    if (fogThickness > 0) {
//        gl_FragColor.a *= texture2D(u_texture_perlin, vTexCoord).a;
//    }

//    gl_FragColor.a = 0;

//    gl_FragColor = texColor; {
 }
