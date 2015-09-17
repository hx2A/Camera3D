class BackgroundColorListener implements ControlListener {
	public void controlEvent(ControlEvent theEvent) {
		int colorLabel;
		int colorBackground;
		int colorForeground;
		int colorActive;

		int backgroundColor = color(cp5.getValue("background_v1"),
				cp5.getValue("background_v2"),
				cp5.getValue("background_v3"));
		float brightness;

		colorMode(HSB, 360, 100, 100);
		brightness = brightness(backgroundColor);
		if (((RadioButton) cp5.get("colorModel")).getState("RGB")) {
			colorMode(RGB, 255, 255, 255);
		}

		if (brightness > 50) {
			colorLabel = 0xFF000000;
			colorActive = 0xFF7F7F7F;
			colorBackground = 0xFF666666;
			colorForeground = 0xFF999999;
		} else {
			colorLabel = 0xFFFFFFFF;
			colorActive = 0xFF7F7F7F;
			colorBackground = 0xFF999999;
			colorForeground = 0xFF666666;
		}

		for (Slider slider : cp5.getAll(Slider.class)) {
			slider.setColorCaptionLabel(colorLabel)
					.setColorValueLabel(colorLabel)
					.setColorActive(colorActive)
					.setColorForeground(colorForeground)
					.setColorBackground(colorBackground);
		}

		for (RadioButton rb : cp5.getAll(RadioButton.class)) {
			rb.setColorLabel(colorLabel).setColorActive(colorActive)
					.setColorForeground(colorForeground)
					.setColorBackground(colorBackground);
		}

		for (DropdownList dl : cp5.getAll(DropdownList.class)) {
			dl.setColorLabel(colorLabel).setColorActive(colorActive)
					.setColorForeground(colorForeground)
					.setColorBackground(colorBackground);
		}
	}
}
