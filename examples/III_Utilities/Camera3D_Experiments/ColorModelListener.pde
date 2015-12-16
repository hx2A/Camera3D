class ColorModellListener implements ControlListener {
	public void controlEvent(ControlEvent theEvent) {
		if (!rgbFlag && theEvent.getName().startsWith("RGB")) {
			// get current colors before changing mode
			int backgroundColor = color(background_v1, background_v2,
					background_v3);
			int fillColor = color(fill_v1, fill_v2, fill_v3);
			int strokeColor = color(stroke_v1, stroke_v2, stroke_v3);

			colorMode(RGB, 255, 255, 255);
			rgbFlag = true;

			((Slider) cp5.getController("background_v1"))
					.setCaptionLabel("background red").setRange(0, 255)
					.setValue(red(backgroundColor));
			((Slider) cp5.getController("background_v2"))
					.setCaptionLabel("background green").setRange(0, 255)
					.setValue(green(backgroundColor));
			((Slider) cp5.getController("background_v3"))
					.setCaptionLabel("background blue").setRange(0, 255)
					.setValue(blue(backgroundColor));

			((Slider) cp5.getController("fill_v1"))
					.setCaptionLabel("fill red").setRange(0, 255)
					.setValue(red(fillColor));
			((Slider) cp5.getController("fill_v2"))
					.setCaptionLabel("fill green").setRange(0, 255)
					.setValue(green(fillColor));
			((Slider) cp5.getController("fill_v3"))
					.setCaptionLabel("fill blue").setRange(0, 255)
					.setValue(blue(fillColor));

			((Slider) cp5.getController("stroke_v1"))
					.setCaptionLabel("stroke red").setRange(0, 255)
					.setValue(red(strokeColor));
			((Slider) cp5.getController("stroke_v2"))
					.setCaptionLabel("stroke green").setRange(0, 255)
					.setValue(green(strokeColor));
			((Slider) cp5.getController("stroke_v3"))
					.setCaptionLabel("stroke blue").setRange(0, 255)
					.setValue(blue(strokeColor));
		} else if (rgbFlag && theEvent.getName().startsWith("HSB")) {
			// get current colors before changing mode
			int backgroundColor = color(background_v1, background_v2,
					background_v3);
			int fillColor = color(fill_v1, fill_v2, fill_v3);
			int strokeColor = color(stroke_v1, stroke_v2, stroke_v3);

			colorMode(HSB, 360, 100, 100);
			rgbFlag = false;

			((Slider) cp5.getController("background_v1"))
					.setCaptionLabel("background hue").setRange(0, 360)
					.setValue(hue(backgroundColor));
			((Slider) cp5.getController("background_v2"))
					.setCaptionLabel("bg saturation").setRange(0, 100)
					.setValue(saturation(backgroundColor));
			((Slider) cp5.getController("background_v3"))
					.setCaptionLabel("bg brightness").setRange(0, 100)
					.setValue(brightness(backgroundColor));

			((Slider) cp5.getController("fill_v1"))
					.setCaptionLabel("fill hue").setRange(0, 360)
					.setValue(hue(fillColor));
			((Slider) cp5.getController("fill_v2"))
					.setCaptionLabel("fill saturation").setRange(0, 100)
					.setValue(saturation(fillColor));
			((Slider) cp5.getController("fill_v3"))
					.setCaptionLabel("fill brightness").setRange(0, 100)
					.setValue(brightness(fillColor));

			((Slider) cp5.getController("stroke_v1"))
					.setCaptionLabel("stroke hue").setRange(0, 360)
					.setValue(hue(strokeColor));
			((Slider) cp5.getController("stroke_v2"))
					.setCaptionLabel("stroke saturation").setRange(0, 100)
					.setValue(saturation(strokeColor));
			((Slider) cp5.getController("stroke_v3"))
					.setCaptionLabel("stroke brightness").setRange(0, 100)
					.setValue(brightness(strokeColor));
		}
	}
}
