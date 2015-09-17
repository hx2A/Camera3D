class DivergenceListener implements ControlListener {
	public void controlEvent(ControlEvent theEvent) {
		camera3D.setCameraDivergence(cp5.getController("divergence")
				.getValue());
	}
}
