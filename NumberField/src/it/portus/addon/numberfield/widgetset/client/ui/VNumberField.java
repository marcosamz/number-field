package it.portus.addon.numberfield.widgetset.client.ui;

import it.portus.addon.numberfield.widgetset.shared.NumberFormatter;

import java.math.BigDecimal;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.terminal.gwt.client.ui.Field;

/**
 * @author Francesco Portus (portusgraphics@gmail.com)
 * 
 * @since 20/nov/2012
 */
public class VNumberField extends FlowPanel implements Paintable, Field, HasValueChangeHandlers<Number> {

	public static final String CLASSNAME = "portus-numberfield";

	public static final Number MAX_VALUE = Long.MAX_VALUE;
	public static final Number MIN_VALUE = Long.MIN_VALUE;

	private String id;

	private ApplicationConnection client;
	private boolean allowNegative = true;

	private boolean allowNull = true;

	private int scale;
	private Number maxVal = MAX_VALUE;

	private Number minVal = MIN_VALUE;

	protected boolean immediate;

	protected boolean readOnly;

	protected boolean enabled;

	// The current value of the VNumberField
	private Number value = null;

	public VNumberField() {
		setStyleName(CLASSNAME);
		sinkEvents(VTooltip.TOOLTIP_EVENTS);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Number> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	/**
	 * Decrease the value by -1
	 */
	public void decreaseValue() {
		Number value = getValue();
		if (value == null) {
			setValue(0);
		}
		setValue(new BigDecimal(getStringValue()).subtract(new BigDecimal(1)));
	}

	public ApplicationConnection getClient() {
		return client;
	}

	public String getId() {
		return id;
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

	protected String getStringValue() {
		return value.toString();
	}

	/**
	 * Returns the value of the spin button
	 * 
	 * @return Returns the current value
	 */
	public Number getValue() {
		return this.value;
	}

	/**
	 * Increases the value by +1
	 */
	public void increaseValue() {
		Number value = getValue();
		if (value == null) {
			setValue(0);
		}
		setValue(new BigDecimal(getStringValue()).add(new BigDecimal(1)));
	}

	public boolean isAllowNegative() {
		return allowNegative;
	}

	public boolean isAllowNull() {
		return allowNull;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isImmediate() {
		return immediate;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void onBrowserEvent(Event event) {
		super.onBrowserEvent(event);
		if (client != null) {
			client.handleTooltipEvent(event, this);
		}
	}

	public void setAllowNegative(boolean allowNegative) {
		this.allowNegative = allowNegative;
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
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

	/**
	 * Sets the value
	 * 
	 * @param value
	 *            The value of the spin button
	 */
	public void setValue(Number value) {
		if (!allowNull && value == null) {
			value = 0;
		}

		if (value != null) {
			if (minVal != null && value.doubleValue() < minVal.doubleValue()) {
				BigDecimal bigDecimal = new BigDecimal(minVal.doubleValue());
				bigDecimal = bigDecimal.setScale(getScale(), NumberFormatter.DEFAULT_ROUNDING_MODE);

				value = bigDecimal;
			} else if (maxVal != null && value.doubleValue() > maxVal.doubleValue()) {
				BigDecimal bigDecimal = new BigDecimal(maxVal.doubleValue());
				bigDecimal = bigDecimal.setScale(getScale(), NumberFormatter.DEFAULT_ROUNDING_MODE);

				value = bigDecimal;
			} else if (!allowNegative && value.doubleValue() < 0) {
				value = 0;
			}
		}

		this.value = value;

		// Send the value string
		getClient().updateVariable(getId(), "value", (this.value != null ? getStringValue() : ""), false);

		if (immediate) {
			client.sendPendingVariableChanges();
		}
	}

	@Override
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		// Save details
		this.client = client;
		id = uidl.getId();

		// Ensure correct implementation and let layout manage caption
		if (client.updateComponent(this, uidl, true)) {
			return;
		}

		if (uidl.hasAttribute("maxVal")) {
			maxVal = new BigDecimal(uidl.getStringAttribute("maxVal"));
		}

		if (uidl.hasAttribute("minVal")) {
			minVal = new BigDecimal(uidl.getStringAttribute("minVal"));
		}

		if (uidl.hasAttribute("allowNegative")) {
			allowNegative = uidl.getBooleanAttribute("allowNegative");
		}

		if (uidl.hasAttribute("allowNull")) {
			allowNull = uidl.getBooleanAttribute("allowNull");
		}

		if (uidl.hasAttribute("scale")) {
			scale = uidl.getIntAttribute("scale");
		}

		immediate = uidl.getBooleanAttribute("immediate");

		readOnly = uidl.getBooleanAttribute("readonly");
		enabled = !uidl.getBooleanAttribute("disabled");
	}
}
