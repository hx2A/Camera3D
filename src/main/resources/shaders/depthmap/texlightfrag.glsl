#version 330 core

#define PROCESSING_TEXLIGHT_SHADER

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

uniform sampler2D texture;

uniform vec2 texOffset;

in float depth;
in vec4 vertColor;
in vec4 backVertColor;
in vec4 vertTexCoord;

layout(location = 0) out vec4 fragColor;

void main() {
  vec4 tex = texture2D(texture, vertTexCoord.st) * (gl_FrontFacing ? vertColor : backVertColor);
  float depth_inv = 1.0 - depth;
  fragColor = vec4(depth_inv, depth_inv, depth_inv, tex.a);
}
