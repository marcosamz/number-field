package it.portus.addon.numberfield;

import it.portus.addon.numberfield.widgetset.client.ui.VSpinnerField;
import it.portus.addon.numberfield.widgetset.shared.NumberFormat;

import com.vaadin.data.Property;

/**
 * @author Francesco Portus (portusgraphics@gmail.com)
 * 
 * @since 20/nov/2012
 */
@SuppressWarnings("serial")
@com.vaadin.ui.ClientWidget(VSpinnerField.class)
public class SpinnerNumberField<T extends Number> extends TextualNumberField<T> {

	public SpinnerNumberField(Class<T> clsType) {
		super(clsType);
	}

	public SpinnerNumberField(Class<T> clsType, NumberFormat numberFormat) {
		super(clsType, numberFormat);
	}

	public SpinnerNumberField(Class<T> clsType, NumberFormat numberFormat, Number value) {
		super(clsType, numberFormat, value);
	}

	public SpinnerNumberField(Class<T> clsType, Property dataSource) throws IllegalArgumentException {
		super(clsType, dataSource);
	}

	public SpinnerNumberField(Class<T> clsType, String caption) {
		super(clsType, caption, (NumberFormat) null);
	}

	public SpinnerNumberField(Class<T> clsType, String caption, NumberFormat numberFormat) {
		super(clsType, caption, numberFormat);
	}

	public SpinnerNumberField(Class<T> clsType, String caption, NumberFormat numberFormat, Number value) {
		super(clsType, caption, numberFormat, value);
	}

	public SpinnerNumberField(Class<T> clsType, String caption, Property dataSource) {
		super(clsType, caption, dataSource);
	}

}