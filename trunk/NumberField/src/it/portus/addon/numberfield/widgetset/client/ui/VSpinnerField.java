package it.portus.addon.numberfield.widgetset.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ui.Field;

/**
 * @author Francesco Portus (portusgraphics@gmail.com)
 * 
 * @since 20/nov/2012
 */
public class VSpinnerField extends VTextualNumber implements Paintable, Field {

	private final SpinnerPanel spinner;

	private final String SPINNER_BUTTONS_ID = "spinnerButtons";

	public VSpinnerField() {
		super();

		spinner = new SpinnerPanel();
		spinner.getButtonUp().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (isEnabled() && !isReadOnly()) {
					increaseValue();
				}
			}
		});

		spinner.getButtonDown().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (isEnabled() && !isReadOnly()) {
					decreaseValue();
				}
			}
		});

		// sinkEvents(Event.ONKEYDOWN);
	}

	@Override
	protected int getFieldExtraWidth() {
		if (fieldExtraWidth < 0) {
			fieldExtraWidth = super.getFieldExtraWidth();
			fieldExtraWidth += spinner.getOffsetWidth();
		}
		return fieldExtraWidth;
	}

	@Override
	public Element getSubPartElement(String subPart) {
		if (subPart.equals(SPINNER_BUTTONS_ID)) {
			return spinner.getElement();
		}

		return super.getSubPartElement(subPart);
	}

	@Override
	public String getSubPartName(Element subElement) {
		if (spinner.getElement().isOrHasChild(subElement)) {
			return SPINNER_BUTTONS_ID;
		}

		return super.getSubPartName(subElement);
	}

	@Override
	public void setStyleName(String style) {
		// make sure the style is there before size calculation
		super.setStyleName(style + " " + CLASSNAME + "-spinner");
	}

	@Override
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		boolean lastReadOnlyState = readOnly;
		boolean lastEnabledState = isEnabled();

		remove(spinner);

		super.updateFromUIDL(uidl, client);

		if (readOnly) {
			spinner.addStyleName(CLASSNAME + "-spinner-readonly");
		} else {
			spinner.removeStyleName(CLASSNAME + "-spinner-readonly");
		}

		if (lastReadOnlyState != readOnly || lastEnabledState != isEnabled()) {
			// Enabled or readonly state changed. Differences in theming might
			// affect the width (for instance if the popup spinner is hidden) so
			// we have to recalculate the width (IF the width of the field is
			// fixed)
			updateWidth();
		}

		spinner.setEnabled(enabled);
		spinner.setReadOnly(readOnly);

		if (!readOnly) {
			text.addStyleName(TEXT_CLASS_SUFFIX);
			rightSymbol.addStyleName("symbol-spinner");
			add(spinner);
		}

		updateWidth();
	}
}
