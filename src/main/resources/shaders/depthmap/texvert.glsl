#define PROCESSING_TEXTURE_SHADER

uniform mat4 transformMatrix;
uniform mat4 modelviewMatrix;
uniform mat4 texMatrix;

uniform float near;
uniform float far;

attribute vec4 position;
attribute vec4 color;
attribute vec2 texCoord;

varying float depth;
varying vec4 vertColor;
varying vec4 vertTexCoord;

void main() {
  gl_Position = transformMatrix * position;
  depth = clamp((-(modelviewMatrix * position).z - near) / (far - near), 0.0, 1.0);

  vertColor = color;
  vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
}