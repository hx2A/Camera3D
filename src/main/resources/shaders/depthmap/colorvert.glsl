#version 330 core

#define PROCESSING_COLOR_SHADER

uniform mat4 transform;
uniform mat4 modelviewMatrix;

uniform float near;
uniform float far;

in vec4 position;
in vec4 color;

out float depth;
out vec4 vertColor;

void main() {
  gl_Position = transform * position;
  depth = clamp((-(modelviewMatrix * position).z - near) / (far - near), 0.0, 1.0);
  vertColor = color;
}
