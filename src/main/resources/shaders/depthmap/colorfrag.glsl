#version 330 core

#define PROCESSING_COLOR_SHADER

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

in float depth;
in vec4 vertColor;

layout(location = 0) out vec4 fragColor;

void main() {
  float depth_inv = 1.0 - depth;
  fragColor = vec4(depth_inv, depth_inv, depth_inv, vertColor.a == 0.0 ? 0.0 : 1.0);
}
