#version 330 core

#define PROCESSING_TEXTURE_SHADER

uniform mat4 transformMatrix;
uniform mat4 modelviewMatrix;
uniform mat4 texMatrix;

uniform float near;
uniform float far;

in vec4 position;
in vec4 color;
in vec2 texCoord;

out float depth;
out vec4 vertColor;
out vec4 vertTexCoord;

void main() {
  gl_Position = transformMatrix * position;
  depth = clamp((-(modelviewMatrix * position).z - near) / (far - near), 0.0, 1.0);

  vertColor = color;
  vertTexCoord = texMatrix * vec4(texCoord, 1.0, 1.0);
}