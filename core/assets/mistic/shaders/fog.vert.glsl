attribute vec4 a_position;
//attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
//uniform vec2 texOffset;
//uniform vec2 res;

//varying vec4 vColor;
varying vec2 vTexCoord;

void main() {
//	vColor = a_color;
    vTexCoord = a_texCoord0.xy;
//	vTexCoord.x = a_texCoord0.x + texOffset.x/res.x;
//	vTexCoord.y = a_texCoord0.y + texOffset.y/res.y;
    gl_Position = u_projTrans * a_position;
}
