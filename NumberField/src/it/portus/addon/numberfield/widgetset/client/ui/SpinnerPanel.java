package it.portus.addon.numberfield.widgetset.client.ui;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;

/**
 * @author Francesco Portus (portusgraphics@gmail.com)
 * 
 * @since 20/nov/2012
 */
class SpinnerPanel extends FlowPanel implements Paintable {

	private static final String SPINNER_CLASSNAME = VSpinnerField.CLASSNAME + "-spinner";

	private final Button buttonUp;
	private final Button buttonDown;
	private boolean readOnly = false;
	private boolean enabled = true;

	SpinnerPanel() {
		setStyleName(SPINNER_CLASSNAME);

		buttonUp = new Button();
		// -2 instead of -1 to avoid FocusWidget.onAttach to reset it
		buttonUp.getElement().setTabIndex(-2);
		buttonUp.setStyleName("up");

		buttonDown = new Button();
		buttonDown.setStyleName("down");
		// -2 instead of -1 to avoid FocusWidget.onAttach to reset it
		buttonDown.getElement().setTabIndex(-2);

		add(buttonUp);
		add(buttonDown);
	}

	public Button getButtonDown() {
		return buttonDown;
	}

	public Button getButtonUp() {
		return buttonUp;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		this.buttonUp.setEnabled(enabled);
		this.buttonDown.setEnabled(enabled);
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		this.buttonUp.setEnabled(!readOnly);
		this.buttonDown.setEnabled(!readOnly);
	}

	@Override
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		setReadOnly(readOnly);
		setEnabled(enabled);

		// Ensure the update is meant for me
		if (client.updateComponent(this, uidl, true)) {
			return;
		}
	}
}
