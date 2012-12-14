package it.portus.addon.numberfield.widgetset.shared;

/**
 * @author Francesco Portus (portusgraphics@gmail.com)
 * 
 * @since 20/nov/2012
 */
public enum NumberFormat {
	PERCENTAGE(0, 0, 100), //
	CURRENCY(2);

	private int scale = 0;

	private Number maxVal = null;
	private Number minVal = null;

	private NumberFormat(int scale) {
		this.scale = scale;
	}

	private NumberFormat(int scale, Number minVal, Number maxVal) {
		this(scale);
		this.maxVal = maxVal;
		this.minVal = minVal;
	}

	public Number getMaxVal() {
		return maxVal;
	}

	public Number getMinVal() {
		return minVal;
	}

	public int getScale() {
		return scale;
	}

	public void setMaxVal(Number maxVal) {
		this.maxVal = maxVal;
	}

	public void setMinVal(Number minVal) {
		this.minVal = minVal;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}
}