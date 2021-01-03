#define PROCESSING_TEXLIGHT_SHADER

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;

uniform vec2 texOffset;

varying float depth;
varying vec4 vertColor;
varying vec4 backVertColor;
varying vec4 vertTexCoord;

void main() {
  vec4 tex = texture2D(texture, vertTexCoord.st) * (gl_FrontFacing ? vertColor : backVertColor);
  float depth_inv = 1.0 - depth;
  gl_FragColor = vec4(depth_inv, depth_inv, depth_inv, tex.a);
}
