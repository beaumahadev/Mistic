uniform vec2 res;

uniform float strenth;
uniform vec2 dir;

void main() {
    float theta = atan(dir.y/dir.x);

    float indicatorX = .05*cos(theta);
    float indicatorY = .05*sin(theta);
    vec2 indicatorPos = vec2(.5 + indicatorX, .9 + indicatorY);

    float D = length(indicatorPos - (gl_FragCoord.xy / res));
    float attenuation = pow(clamp(1.0 - D*D/(.03*.03), 0.0, 1.0), 2.0);

    gl_FragColor.rgb = vec3(0.7, 1.0, 1.0);
    gl_FragColor.a = attenuation;
}
