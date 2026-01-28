#define PROCESSING_COLOR_SHADER

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

varying float depth;
varying vec4 vertColor;

void main() {
  float depth_inv = 1.0 - depth;
  gl_FragColor = vec4(depth_inv, depth_inv, depth_inv, vertColor.a == 0.0 ? 0.0 : 1.0);
}
