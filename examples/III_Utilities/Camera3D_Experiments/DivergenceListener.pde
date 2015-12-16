class DivergenceListener implements ControlListener {
	public void controlEvent(ControlEvent theEvent) {
		Generator generator = camera3D.getGenerator();
		if (generator instanceof StereoscopicGenerator) {
			((StereoscopicGenerator) camera3D.getGenerator())
					.setDivergence(cp5.getController("divergence")
							.getValue());
		}
	}
}