class RendererListener implements ControlListener {
	public void controlEvent(ControlEvent theEvent) {
		String rendererChoice = rendererMenuItems.get((int) theEvent
				.getGroup().getValue());

		println("setting renderer to: " + rendererChoice + " renderer");

		switch ((int) theEvent.getGroup().getValue()) {
		case 0:
			camera3D.renderRegular();
			break;
		case 1:
			camera3D.renderDefaultAnaglyph();
			break;
		case 2:
			camera3D.renderBitMaskRedCyanAnaglyph();
			break;
		case 3:
			camera3D.renderBitMaskMagentaGreenAnaglyph();
			break;
		case 4:
			camera3D.renderTrueAnaglyph();
			break;
		case 5:
			camera3D.renderGrayAnaglyph();
			break;
		case 6:
			camera3D.renderHalfColorAnaglyph();
			break;
		case 7:
			camera3D.renderDuboisRedCyanAnaglyph();
			break;
		case 8:
			camera3D.renderDuboisMagentaGreenAnaglyph();
			break;
		case 9:
			camera3D.renderDuboisAmberBlueAnaglyph();
			break;
		case 10:
			camera3D.renderBarrelDistortion();
			break;
		case 11:
			camera3D.renderSplitDepthIllusion();
			break;
		case 12:
			camera3D.renderInterlaced();
			break;
		case 13:
			camera3D.renderSplitFrameSideBySide();
			break;
		case 14:
			camera3D.renderSplitFrameSideBySideHalfWidth();
			break;
		case 15:
			camera3D.renderSplitFrameOverUnder();
			break;
		case 16:
			camera3D.renderSplitFrameOverUnderHalfHeight();
			break;
		default:
			println("Unknown Renderer " + rendererChoice
					+ ". Please report bug.");
		}
	}
}
