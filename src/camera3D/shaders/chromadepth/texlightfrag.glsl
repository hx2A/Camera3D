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
  vec4 rgb;
  float d = depth;
  float d2 = d * d;
  if (d < 0.5) {
    rgb.g = 1.6 * d2 + 1.2 * d;
    rgb.b = 0.0;
  } else {
    rgb.g = 3.2 * d2 - 6.8 * d + 3.6;
    rgb.b = d2 * -4.8 + 9.2 * d - 3.4;
  }
  d = d / 0.9;
  d2 = d2 / 0.81;
  rgb.r = -2.14 * d2 * d2 -1.07 * d2 * d + 0.133 * d2 + 0.0667 * d + 1.0;

  // original
  // gl_FragColor = texture2D(texture, vertTexCoord.st) * (gl_FrontFacing ? vertColor : backVertColor);

  // `tex` is the value gl_FragColor would otherwise be
  vec4 tex = texture2D(texture, vertTexCoord.st) * (gl_FrontFacing ? vertColor : backVertColor);

  // I want the hue to come from `rgb` but dim it based on the luminence of `tex`
  float luminence = dot(tex, vec4(0.2126, 0.7152, 0.0722, 0.0));
  rgb.r = rgb.r * luminence;
  rgb.g = rgb.g * luminence;
  rgb.b = rgb.b * luminence;

  // If `tex` is partially or completely transparent, retain that
  rgb.a = tex.a;

  gl_FragColor = rgb;
}
