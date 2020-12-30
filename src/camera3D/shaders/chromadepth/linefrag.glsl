#define PROCESSING_LINE_SHADER

#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif

varying float depth;
varying vec4 vertColor;

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
  // gl_FragColor = vertColor;

  // I want the hue to come from `rgb` but dim it based on the luminence of `vertColor`
  float luminence = dot(vertColor, vec4(0.2126, 0.7152, 0.0722, 0.0));
  rgb.r = rgb.r * luminence;
  rgb.g = rgb.g * luminence;
  rgb.b = rgb.b * luminence;

  // If `vertColor` was partially or completely transparent, retain that
  rgb.a = vertColor.a;

  gl_FragColor = rgb;
}
