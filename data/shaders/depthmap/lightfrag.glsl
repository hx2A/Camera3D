#define PROCESSING_LIGHT_SHADER

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

varying float depth;
varying vec4 vertColor;
varying vec4 backVertColor;

void main() {
  vec4 color = gl_FrontFacing ? vertColor : backVertColor;
  float depth_inv = 1.0 - depth;
  gl_FragColor = vec4(depth_inv, depth_inv, depth_inv, color.a == 0.0 ? 0.0 : 1.0);
}
