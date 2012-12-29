package it.portus.addon.numberfield.widgetset.client.ui;

import it.portus.addon.numberfield.widgetset.shared.NumberFormat;
import it.portus.addon.numberfield.widgetset.shared.NumberFormatter;

import java.math.BigDecimal;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase.TextAlignment;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.BrowserInfo;
import com.vaadin.terminal.gwt.client.ContainerResizedListener;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.Focusable;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;
import com.vaadin.terminal.gwt.client.ui.Field;
import com.vaadin.terminal.gwt.client.ui.SubPartAware;
import com.vaadin.terminal.gwt.client.ui.VTextField;

/**
 * @author Francesco Portus (portusgraphics@gmail.com)
 * 
 * @since 20/nov/2012
 */
public class VTextualNumber extends VNumberField implements Paintable, Field, KeyDownHandler, KeyUpHandler, ChangeHandler, ContainerResizedListener,
		Focusable, SubPartAware {

	// private static final String PARSE_ERROR_CLASSNAME = CLASSNAME +
	// "-parseerror";

	protected final TextBox text;
	protected final FlowPanel leftSymbol;
	protected final FlowPanel rightSymbol;

	private String width;
	private boolean needLayout;
	protected int fieldExtraWidth = -1;

	private static final String CLASSNAME_PROMPT = "prompt";
	private static final String ATTR_INPUTPROMPT = "prompt";

	private static final String CLASS_NAME_SYMBOL = CLASSNAME + "-symbol";
	private static final String CLASS_NAME_SYMBOL_LEFT = "left";
	private static final String CLASS_NAME_SYMBOL_RIGHT = "right";

	public static final String TEXT_CLASS_SUFFIX = "has-suffix";
	public static final String TEXT_CLASS_PREFIX = "has-prefix";

	private String inputPrompt = "";
	private boolean prompting = false;

	private final String TEXTFIELD_ID = "field";

	private NumberFormat numberFormat;

	private NumberFormatter numberFormatter = new NumberFormatter();

	public VTextualNumber() {
		super();
		text = new TextBox();

		// use normal textfield styles as a basis
		text.setStyleName(VTextField.CLASSNAME);
		// add numberfield spesific style name also
		text.addStyleName(CLASSNAME + "-textfield");
		text.addKeyDownHandler(this);
		text.addKeyUpHandler(this);
		text.addChangeHandler(this);
		text.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				text.addStyleName(VTextField.CLASSNAME + "-" + VTextField.CLASSNAME_FOCUS);
				leftSymbol.addStyleName(VTextField.CLASSNAME + "-" + VTextField.CLASSNAME_FOCUS);
				rightSymbol.addStyleName(VTextField.CLASSNAME + "-" + VTextField.CLASSNAME_FOCUS);
				if (prompting) {
					text.setText("");
					setPrompting(false);
				}
				if (getClient() != null && getClient().hasEventListeners(VTextualNumber.this, EventId.FOCUS)) {
					getClient().updateVariable(getId(), EventId.FOCUS, "", true);
				}
			}
		});
		text.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				text.removeStyleName(VTextField.CLASSNAME + "-" + VTextField.CLASSNAME_FOCUS);
				leftSymbol.removeStyleName(VTextField.CLASSNAME + "-" + VTextField.CLASSNAME_FOCUS);
				rightSymbol.removeStyleName(VTextField.CLASSNAME + "-" + VTextField.CLASSNAME_FOCUS);
				String value = getText();
				setPrompting(inputPrompt != null && (value == null || "".equals(value)));
				if (prompting) {
					text.setText(readOnly ? "" : inputPrompt);
				}
				if (getClient() != null && getClient().hasEventListeners(VTextualNumber.this, EventId.BLUR)) {
					getClient().updateVariable(getId(), EventId.BLUR, "", true);
				}
			}
		});
		text.addMouseWheelHandler(new MouseWheelHandler() {

			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				if (event.isNorth()) {
					increaseValue();
				}
				if (event.isSouth()) {
					decreaseValue();
				}
			}
		});

		add(text);

		leftSymbol = new FlowPanel();
		leftSymbol.addStyleName(CLASS_NAME_SYMBOL);
		rightSymbol = new FlowPanel();
		rightSymbol.addStyleName(CLASS_NAME_SYMBOL);
	}

	@Override
	public void decreaseValue() {
		if (isEnabled() && !isReadOnly()) {
			super.decreaseValue();

			BigDecimal bigDecimal = new BigDecimal(getStringValue());
			bigDecimal = bigDecimal.setScale(getScale(), NumberFormatter.DEFAULT_ROUNDING_MODE);
			setText(numberFormatter.format(bigDecimal.toString()));
		}
	}

	@Override
	public void focus() {
		text.setFocus(true);
	}

	/**
	 * Returns pixels in x-axis reserved for other than textfield content.
	 * 
	 * @return extra width in pixels
	 */
	protected int getFieldExtraWidth() {
		if (fieldExtraWidth < 0) {
			text.setWidth("0");
			fieldExtraWidth = text.getOffsetWidth();
			fieldExtraWidth += (leftSymbol.isAttached() ? leftSymbol.getOffsetWidth() : 0);
			fieldExtraWidth += (rightSymbol.isAttached() ? rightSymbol.getOffsetWidth() : 0);
			// fieldExtraWidth += 20;
			if (BrowserInfo.get().isFF3()) {
				// Firefox somehow always leaves the INPUT element 2px wide
				fieldExtraWidth -= 2;
			}
		}
		return fieldExtraWidth;
	}

	@Override
	public Element getSubPartElement(String subPart) {
		if (subPart.equals(TEXTFIELD_ID)) {
			return text.getElement();
		}

		return null;
	}

	@Override
	public String getSubPartName(Element subElement) {
		if (text.getElement().isOrHasChild(subElement)) {
			return TEXTFIELD_ID;
		}

		return null;
	}

	protected String getText() {
		if (prompting) {
			return "";
		}
		return text.getText();
	}

	@Override
	public void iLayout() {
		iLayout(false);
	}

	public void iLayout(boolean force) {
		if (needLayout || force) {
			int textFieldWidth = getOffsetWidth() - getFieldExtraWidth();
			if (textFieldWidth < 0) {
				// Field can never be smaller than 0 (causes exception in IE)
				textFieldWidth = 0;
			}
			text.setWidth(textFieldWidth + "px");
		}
	}

	@Override
	public void increaseValue() {
		if (isEnabled() && !isReadOnly()) {
			super.increaseValue();

			BigDecimal bigDecimal = new BigDecimal(getStringValue());
			bigDecimal = bigDecimal.setScale(getScale(), NumberFormatter.DEFAULT_ROUNDING_MODE);
			setText(numberFormatter.format(bigDecimal.toString()));
		}
	}

	protected boolean isUndefinedWidth() {
		return width == null || "".equals(width);
	}

	@Override
	public void onChange(ChangeEvent event) {
		updateContent();
	}

	@Override
	public void onKeyDown(KeyDownEvent event) {
		int code = event.getNativeKeyCode();
		// String typed = event.getNativeEvent().getCharCode() + "";
		// String str = getText();
		// String newValue = str + typed;
		boolean functional = false;

		// allow key numbers, 0 to 9
		if ((code >= 48 && code <= 57) || (code >= 96 && code <= 105)) functional = true;

		if (event.isAnyModifierKeyDown()) functional = true;

		// check Backspace, Escape, Tab, Enter, Delete, and left/right arrows
		if (code == KeyCodes.KEY_BACKSPACE) functional = true;
		if (code == KeyCodes.KEY_ESCAPE) functional = true;
		if (code == KeyCodes.KEY_TAB) functional = true;
		if (code == KeyCodes.KEY_ENTER) functional = true;
		if (code == KeyCodes.KEY_DELETE) functional = true;
		if (code == KeyCodes.KEY_LEFT) functional = true;
		if (code == KeyCodes.KEY_RIGHT) functional = true;
		if (code == KeyCodes.KEY_HOME) functional = true;
		// dash as well
		if (isAllowNegative() && (code == 189 || code == 109)) functional = true;
		// dot symbol
		if (isAllowNegative() && (code == 190 || code == 110)) functional = true;

		if (!event.isAnyModifierKeyDown() && event.isUpArrow()) {
			increaseValue();
		}

		if (!event.isAnyModifierKeyDown() && event.isDownArrow()) {
			decreaseValue();
		}

		if (!functional) {
			event.preventDefault();
			event.stopPropagation();
		}

		// setText(newValue);
	}

	@Override
	public void onKeyUp(KeyUpEvent event) {
		if (!event.isAnyModifierKeyDown()) {
			int cursorPos = text.getCursorPos();
			// updateContent();
			text.setCursorPos(cursorPos);
		}
	}

	private void setPrompting(boolean prompting) {
		this.prompting = prompting;
		if (prompting) {
			setValue(null);

			text.addStyleDependentName(CLASSNAME_PROMPT);
			leftSymbol.addStyleDependentName(CLASSNAME_PROMPT);
			rightSymbol.addStyleDependentName(CLASSNAME_PROMPT);
		} else {
			text.removeStyleDependentName(CLASSNAME_PROMPT);
			leftSymbol.removeStyleDependentName(CLASSNAME_PROMPT);
			rightSymbol.removeStyleDependentName(CLASSNAME_PROMPT);
		}
	}

	@Override
	public void setStyleName(String style) {
		// make sure the style is there before size calculation
		super.setStyleName(style + " " + CLASSNAME + "-textual");
	}

	private void setText(String text) {
		if (inputPrompt != null && (text == null || "".equals(text))) {
			text = readOnly ? "" : inputPrompt;
			setPrompting(true);

			this.text.setText(text);
		} else {
			setPrompting(false);

			Number value = null;
			if (text != null && !"".equals(text.trim())) {
				value = numberFormatter.unformat(text);
			}

			setValue(value);
			value = getValue();

			text = numberFormatter.format(value);
			this.text.setText(text);

			ValueChangeEvent.fire(this, value);
		}
	}

	@Override
	public void setWidth(String newWidth) {
		if (!"".equals(newWidth.trim()) && (isUndefinedWidth() || !newWidth.equals(width))) {
			if (BrowserInfo.get().isIE6()) {
				// in IE6 cols ~ min-width
				DOM.setElementProperty(text.getElement(), "size", "1");
			}
			needLayout = true;
			width = newWidth;
			super.setWidth(width);
			iLayout();
			if (newWidth.indexOf("%") < 0) {
				needLayout = false;
			}
		} else {
			if ("".equals(newWidth) && !isUndefinedWidth()) {
				// Changing from defined to undefined
				if (BrowserInfo.get().isIE6()) {
					// revert IE6 hack
					DOM.setElementProperty(text.getElement(), "size", "");
				}
				super.setWidth("");
				iLayout(true);
				width = null;
			}
		}
	}

	private void updateContent() {
		updateContent(getText());
	}

	private void updateContent(final String inputText) {
		setPrompting(inputPrompt != null && (inputText != null && inputText.equals("")));

		if (BrowserInfo.get().isFF3()) {
			/*
			 * Firefox 3 is really sluggish when updating input attached to dom.
			 * Some optimizations seems to work much better in Firefox3 if we
			 * update the actual content lazily when the rest of the DOM has
			 * stabilized. In tests, about ten times better performance is
			 * achieved with this optimization. See for eg. #2898
			 */
			Scheduler.get().scheduleDeferred(new Command() {
				@Override
				public void execute() {
					String fieldValue;
					if (prompting) {
						fieldValue = isReadOnly() ? "" : inputPrompt;
						setPrompting(true);

						text.setText(fieldValue);
					} else {
						setPrompting(false);

						fieldValue = inputText;

						if (fieldValue != null && !"".equals(fieldValue)) {
							try {
								BigDecimal bigDecimal = new BigDecimal(numberFormatter.unformat(fieldValue) + "");
								bigDecimal = bigDecimal.setScale(getScale(), NumberFormatter.DEFAULT_ROUNDING_MODE);
								fieldValue = bigDecimal.toString();
							} catch (Exception e) {
								VConsole.log(e);
							}
						}
						fieldValue = numberFormatter.format(fieldValue);
						setText(fieldValue);
					}
				}
			});
		} else {
			String fieldValue;
			if (prompting) {
				fieldValue = isReadOnly() ? "" : inputPrompt;
				setPrompting(true);

				text.setText(fieldValue);
			} else {
				setPrompting(false);

				fieldValue = inputText;
				if (fieldValue != null && !"".equals(fieldValue)) {
					try {
						BigDecimal bigDecimal = new BigDecimal(numberFormatter.unformat(fieldValue) + "");
						bigDecimal = bigDecimal.setScale(getScale(), NumberFormatter.DEFAULT_ROUNDING_MODE);
						fieldValue = bigDecimal.toString();
					} catch (Exception e) {
						VConsole.log(e);
					}
				}
				fieldValue = numberFormatter.format(fieldValue);
				setText(fieldValue);
			}
		}
	}

	@Override
	public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
		super.updateFromUIDL(uidl, client);

		inputPrompt = uidl.getStringAttribute(ATTR_INPUTPROMPT);

		text.setEnabled(enabled);
		text.setReadOnly(readOnly);

		if (readOnly) {
			text.addStyleName("v-readonly");
			leftSymbol.addStyleName("v-readonly");
			rightSymbol.addStyleName("v-readonly");
		} else {
			text.removeStyleName("v-readonly");
			leftSymbol.removeStyleName("v-readonly");
			rightSymbol.removeStyleName("v-readonly");
		}

		// not a FocusWidget -> needs own tabindex handling
		if (uidl.hasAttribute("tabindex")) {
			text.setTabIndex(uidl.getIntAttribute("tabindex"));
		}

		if (readOnly) {
			text.addStyleDependentName("readonly");
			leftSymbol.addStyleDependentName("readonly");
			rightSymbol.addStyleDependentName("readonly");
		} else {
			text.removeStyleDependentName("readonly");
			leftSymbol.removeStyleDependentName("readonly");
			rightSymbol.removeStyleDependentName("readonly");
		}

		setAllowNull(uidl.getBooleanAttribute("allowNull"));

		setAllowNegative(uidl.getBooleanAttribute("allowNegative"));
		numberFormatter.setAllowNegative(isAllowNegative());

		if (uidl.hasAttribute("decimalsSeparator")) {
			numberFormatter.setDecimalsSeparator(uidl.getStringAttribute("decimalsSeparator").charAt(0));
		}
		if (uidl.hasAttribute("groupingSeparator")) {
			numberFormatter.setGroupingSeparator(uidl.getStringAttribute("groupingSeparator").charAt(0));
		}
		if (uidl.hasAttribute("percentageSymbol")) {
			numberFormatter.setPercentageSymbol(uidl.getStringAttribute("percentageSymbol").charAt(0));
		}
		if (uidl.hasAttribute("currencySymbol")) {
			numberFormatter.setCurrencySymbol(uidl.getStringAttribute("currencySymbol"));
		}

		remove(leftSymbol);
		remove(rightSymbol);

		boolean hasPrefix = false;
		boolean hasSuffix = false;

		if (uidl.hasAttribute("numberFormat")) {
			numberFormat = NumberFormat.valueOf(uidl.getStringAttribute("numberFormat"));
		}

		if (uidl.hasAttribute("scale")) {
			setScale(uidl.getIntAttribute("scale"));
		} else if (numberFormat != null) {
			setScale(numberFormat.getScale());
		}
		numberFormatter.setScale(getScale());

		if (!uidl.hasAttribute("maxVal") && numberFormat != null && numberFormat.getMaxVal() != null) {
			setMaxVal(numberFormat.getMaxVal());
		}

		if (!uidl.hasAttribute("minVal") && numberFormat != null && numberFormat.getMinVal() != null) {
			setMinVal(numberFormat.getMinVal());
		}

		if (numberFormat != null) {
			switch (numberFormat) {
			case CURRENCY:
				leftSymbol.getElement().setInnerText(numberFormatter.getCurrencySymbol() + "");
				text.setAlignment(TextAlignment.RIGHT);
				hasPrefix = true;
				break;
			case PERCENTAGE:
				rightSymbol.getElement().setInnerText(numberFormatter.getPercentageSymbol() + "");
				text.setAlignment(TextAlignment.RIGHT);
				hasSuffix = true;
				break;

			default:
				break;
			}
		}

		leftSymbol.removeStyleDependentName(CLASS_NAME_SYMBOL_LEFT);
		leftSymbol.removeStyleDependentName(CLASS_NAME_SYMBOL_RIGHT);
		rightSymbol.removeStyleDependentName(CLASS_NAME_SYMBOL_LEFT);
		rightSymbol.removeStyleDependentName(CLASS_NAME_SYMBOL_RIGHT);

		if (uidl.hasAttribute("textAlignment")) {
			text.setAlignment(TextAlignment.valueOf(uidl.getStringAttribute("textAlignment").toUpperCase()));
		}

		if (uidl.hasAttribute("prefix")) {
			leftSymbol.getElement().setInnerText(uidl.getStringAttribute("prefix"));
			hasPrefix = true;
		}
		if (uidl.hasAttribute("suffix")) {
			rightSymbol.getElement().setInnerText(uidl.getStringAttribute("suffix"));
			hasSuffix = true;
		}

		if (hasPrefix) {
			text.addStyleName(TEXT_CLASS_PREFIX);
			leftSymbol.addStyleDependentName(CLASS_NAME_SYMBOL_LEFT);
			leftSymbol.addStyleName(VTextField.CLASSNAME);

			insert(leftSymbol, 0);
		}

		if (hasSuffix) {
			text.addStyleName(TEXT_CLASS_SUFFIX);
			rightSymbol.addStyleDependentName(CLASS_NAME_SYMBOL_RIGHT);
			rightSymbol.addStyleName(VTextField.CLASSNAME);

			add(rightSymbol);
		}

		final String text = uidl.getStringVariable("text");
		updateContent(text);

		updateWidth();
	}

	/**
	 * Force an recalculation of the width of the component IF the width has
	 * been defined. Does nothing if width is undefined as the width will be
	 * automatically adjusted by the browser.
	 */
	public void updateWidth() {
		if (isUndefinedWidth()) {
			return;
		}
		needLayout = true;
		fieldExtraWidth = -1;
		iLayout(true);
	}
}
