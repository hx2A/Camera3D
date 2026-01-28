#define PROCESSING_COLOR_SHADER

uniform mat4 transform;
uniform mat4 modelviewMatrix;

uniform float near;
uniform float far;

attribute vec4 position;
attribute vec4 color;

varying float depth;
varying vec4 vertColor;

void main() {
  gl_Position = transform * position;
  depth = clamp((-(modelviewMatrix * position).z - near) / (far - near), 0.0, 1.0);
  vertColor = color;
}
