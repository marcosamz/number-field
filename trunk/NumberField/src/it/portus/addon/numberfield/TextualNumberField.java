package it.portus.addon.numberfield;

import it.portus.addon.numberfield.widgetset.client.ui.VTextualNumber;
import it.portus.addon.numberfield.widgetset.shared.NumberFormat;
import it.portus.addon.numberfield.widgetset.shared.TextAlignment;

import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents.BlurEvent;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;

/**
 * @author Francesco Portus (portusgraphics@gmail.com)
 * 
 * @since 20/nov/2012
 */
@SuppressWarnings("serial")
@com.vaadin.ui.ClientWidget(VTextualNumber.class)
public class TextualNumberField<T extends Number> extends AbstractTextField {

	//private static final NumberFormat DEFAULT_NUMBER_FORMAT = NumberFormat.INTEGER_NUMBER;

	private NumberFormat numberFormat = null;
	private Number maxVal;
	private Number minVal;

	private boolean allowNegative = true;
	private boolean allowNull = false;

	private Locale locale = null;

	private Integer scale = null;

	private String prefix = null;
	private String suffix = null;
	private TextAlignment textAlignment = null;

	@SuppressWarnings("unchecked")
	private Class<T> clsType = (Class<T>) Number.class;

	/* Constructors */

	/**
	 * Constructs an empty <code>SpinnerTextField</code> with no caption.
	 */
	public TextualNumberField(Class<T> clsType) {
		this.clsType = clsType;
		//setNumberFormat(DEFAULT_NUMBER_FORMAT);
		setNullRepresentation(null);

		if (Double.class.isAssignableFrom(clsType) || Float.class.isAssignableFrom(clsType)) {
			scale = 2;
		} else {
			scale = 0;
		}
	}

	/**
	 * Constructs an empty <code>SpinnerTextField</code> with caption.
	 * 
	 * @param numberFormat
	 *            the Number type {@link NumberFormat}.
	 */
	public TextualNumberField(Class<T> clsType, NumberFormat numberFormat) {
		this(clsType, null, numberFormat);
	}

	/**
	 * Constructs a new <code>SpinnerTextField</code> with the given caption and
	 * initial text contents. The editor constructed this way will not be bound
	 * to a Property unless
	 * {@link com.vaadin.data.Property.Viewer#setPropertyDataSource(Property)}
	 * is called to bind it.
	 * 
	 * @param numberFormat
	 *            the Number type {@link NumberFormat}.
	 * @param value
	 *            the Number value.
	 */
	public TextualNumberField(Class<T> clsType, NumberFormat numberFormat, Number value) {
		this(clsType, null, numberFormat, value);
	}

	/**
	 * Constructs a new <code>SpinnerTextField</code> that's bound to the
	 * specified <code>Property</code> and has no caption.
	 * 
	 * @param dataSource
	 *            the Property to be edited with this editor.
	 */
	public TextualNumberField(Class<T> clsType, Property dataSource) throws IllegalArgumentException {
		this(clsType);

		if (!Number.class.isAssignableFrom(dataSource.getType())) {
			throw new IllegalArgumentException("Can't use " + dataSource.getType().getName() + " typed property as datasource");
		}

		setPropertyDataSource(dataSource);
	}

	public TextualNumberField(Class<T> clsType, String caption) {
		this(clsType, caption, (NumberFormat) null);
	}

	/**
	 * Constructs an empty <code>SpinnerTextField</code> with caption.
	 * 
	 * @param caption
	 *            the caption of the SpinnerTextField.
	 * @param numberFormat
	 *            the Number type {@link NumberFormat}.
	 */
	public TextualNumberField(Class<T> clsType, String caption, NumberFormat numberFormat) {
		this(clsType, caption, numberFormat, null);
	}

	/**
	 * Constructs a new <code>SpinnerTextField</code> with the given caption and
	 * initial text contents. The editor constructed this way will not be bound
	 * to a Property unless
	 * {@link com.vaadin.data.Property.Viewer#setPropertyDataSource(Property)}
	 * is called to bind it.
	 * 
	 * @param caption
	 *            the caption <code>String</code> for the editor.
	 * @param numberFormat
	 *            the Number type {@link NumberFormat}.
	 * @param value
	 *            the Number value.
	 */
	public TextualNumberField(Class<T> clsType, String caption, NumberFormat numberFormat, Number value) {
		this(clsType);
		setCaption(caption);
		setNumberFormat(numberFormat);
		setValue(value);
	}

	/**
	 * Constructs a new <code>SpinnerTextField</code> that's bound to the
	 * specified <code>Property</code> and has the given caption
	 * <code>String</code>.
	 * 
	 * @param caption
	 *            the caption <code>String</code> for the editor.
	 * @param dataSource
	 *            the Property to be edited with this editor.
	 */
	public TextualNumberField(Class<T> clsType, String caption, Property dataSource) {
		this(clsType, dataSource);
		setCaption(caption);
	}

	/*
	 * Invoked when a variable of the component changes. Don't add a JavaDoc
	 * comment here, we use the default documentation from implemented
	 * interface.
	 */
	@Override
	public void changeVariables(Object source, Map<String, Object> variables) {
		super.changeVariables(source, variables);

		if (!isReadOnly() && (variables.containsKey("value"))) {
			Object value = variables.get("value");
			if (value == null || "".equals(value)) {
				setValue(null, true); // Don't require a repaint, client
			} else {
				BigDecimal bigDecimal = new BigDecimal(value.toString());
				if (Integer.class.isAssignableFrom(clsType)) {
					value = bigDecimal.intValue();
				} else if (Double.class.isAssignableFrom(clsType)) {
					value = bigDecimal.doubleValue();
				} else if (Long.class.isAssignableFrom(clsType)) {
					value = bigDecimal.longValue();
				} else if (Float.class.isAssignableFrom(clsType)) {
					value = bigDecimal.floatValue();
				} else if (Short.class.isAssignableFrom(clsType)) {
					value = bigDecimal.shortValue();
				} else if (Byte.class.isAssignableFrom(clsType)) {
					value = bigDecimal.byteValue();
				} else {
					value = bigDecimal;
				}
				setValue(value, true); // Don't require a repaint, client
			}
		}

		if (variables.containsKey(FocusEvent.EVENT_ID)) {
			fireEvent(new FocusEvent(this));
		}

		if (variables.containsKey(BlurEvent.EVENT_ID)) {
			fireEvent(new BlurEvent(this));
		}
	}

	/* Property features */

	@Override
	public Locale getLocale() {
		if (locale != null) {
			return locale;
		} else {
			return super.getLocale();
		}
	}

	private Number getMaxMinValueByType(boolean max) {
		Number val = null;
		if (Integer.class.isAssignableFrom(clsType)) {
			val = (max ? Integer.MAX_VALUE : Integer.MIN_VALUE);
		} else if (Double.class.isAssignableFrom(clsType)) {
			val = (max ? Double.MAX_VALUE : Double.MIN_VALUE);
		} else if (Long.class.isAssignableFrom(clsType)) {
			val = (max ? Long.MAX_VALUE : Long.MIN_VALUE);
		} else if (Float.class.isAssignableFrom(clsType)) {
			val = (max ? Float.MAX_VALUE : Float.MIN_VALUE);
		} else if (Short.class.isAssignableFrom(clsType)) {
			val = (max ? Short.MAX_VALUE : Short.MIN_VALUE);
		} else if (Byte.class.isAssignableFrom(clsType)) {
			val = (max ? Byte.MAX_VALUE : Byte.MIN_VALUE);
		}
		return val;
	}

	public Number getMaxVal() {
		return maxVal;
	}

	public Number getMinVal() {
		return minVal;
	}

	public NumberFormat getNumberFormat() {
		return numberFormat;
	}

	public String getPrefix() {
		return prefix;
	}

	public Integer getScale() {
		return scale;
	}

	public String getSuffix() {
		return suffix;
	}

	public TextAlignment getTextAlignment() {
		return textAlignment;
	}

	/*
	 * Gets the edited property's type. Don't add a JavaDoc comment here, we use
	 * the default documentation from implemented interface.
	 */
	@Override
	public Class<T> getType() {
		return clsType;
	}

	public boolean isAllowNegative() {
		return allowNegative;
	}

	public boolean isAllowNull() {
		return allowNull;
	}

	/**
	 * Detects if this field is used in a Form (logically) and if so, notifies
	 * it (by repainting it) that the validity of this field might have changed.
	 */
	private void notifyFormOfValidityChange() {
		Component parentOfSpinnerTextField = getParent();
		boolean formFound = false;
		while (parentOfSpinnerTextField != null || formFound) {
			if (parentOfSpinnerTextField instanceof Form) {
				Form f = (Form) parentOfSpinnerTextField;
				Collection<?> visibleItemProperties = f.getItemPropertyIds();
				for (Object fieldId : visibleItemProperties) {
					Field field = f.getField(fieldId);
					if (field == this) {
						/*
						 * this SpinnerTextField is logically in a form. Do the
						 * same thing as form does in its value change listener
						 * that it registers to all fields.
						 */
						f.requestRepaint();
						formFound = true;
						break;
					}
				}
			}
			if (formFound) {
				break;
			}
			parentOfSpinnerTextField = parentOfSpinnerTextField.getParent();
		}
	}

	/*
	 * Paints this component. Don't add a JavaDoc comment here, we use the
	 * default documentation from implemented interface.
	 */
	@Override
	public void paintContent(PaintTarget target) throws PaintException {
		super.paintContent(target);

		if (numberFormat != null) {
			target.addAttribute("numberFormat", numberFormat.name());
		} else {
			System.out.println();
		}

		if (maxVal != null) {
			target.addAttribute("maxVal", maxVal.toString());
		} else {
			Number n = null;
			if (numberFormat != null && numberFormat.getMaxVal() != null) {
				n = numberFormat.getMaxVal();
			} else {
				n = getMaxMinValueByType(true);
			}
			if (n != null) {
				target.addAttribute("maxVal", n.toString());
			}
		}

		if (minVal != null) {
			target.addAttribute("minVal", minVal.toString());
		} else {
			Number n = null;
			if (numberFormat != null && numberFormat.getMinVal() != null) {
				n = numberFormat.getMinVal();
			} else {
				n = getMaxMinValueByType(false);
			}
			if (n != null) {
				target.addAttribute("minVal", n.toString());
			}
		}

		if (scale != null) {
			target.addAttribute("scale", scale);
		}

		if (prefix != null) {
			target.addAttribute("prefix", prefix);
		}

		if (suffix != null) {
			target.addAttribute("suffix", suffix);
		}

		if (textAlignment != null) {
			target.addAttribute("textAlignment", textAlignment.name().toLowerCase());
		}

		// Adds the locale's attributes
		final Locale locale = getLocale();
		if (locale != null) {
			DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(locale);

			target.addAttribute("decimalsSeparator", decimalFormatSymbols.getDecimalSeparator() + "");
			target.addAttribute("groupingSeparator", decimalFormatSymbols.getGroupingSeparator() + "");
			target.addAttribute("percentageSymbol", decimalFormatSymbols.getPercent() + "");
			target.addAttribute("currencySymbol", decimalFormatSymbols.getCurrencySymbol());
		}

		target.addAttribute("allowNegative", allowNegative);
		target.addAttribute("allowNull", allowNull);
	}

	public void setAllowNegative(boolean allowNegative) {
		this.allowNegative = allowNegative;
	}

	public void setAllowNull(boolean allowNull) {
		this.allowNull = allowNull;
	}

	/**
	 * <p>
	 * Sets the input prompt - a textual prompt that is displayed when the field
	 * would otherwise be empty, to prompt the user for input.
	 * </p>
	 * <p>
	 * If {@code inputPrompt} is not {@code null}, allow {@code null value} is
	 * permitted.
	 * 
	 * @param inputPrompt
	 */
	@Override
	public void setInputPrompt(String inputPrompt) {
		if (inputPrompt != null && !"".equals(inputPrompt.trim())) {
			setAllowNull(true);
		}

		super.setInputPrompt(inputPrompt);
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setMaxVal(Number maxVal) {
		this.maxVal = maxVal;
	}

	public void setMinVal(Number minVal) {
		this.minVal = minVal;
	}

	@Override
	public void setNullRepresentation(String nullRepresentation) {
		if (nullRepresentation == null || nullRepresentation.toLowerCase().contains("null".toLowerCase())) {
			super.setNullRepresentation("");
		} else {
			super.setNullRepresentation(nullRepresentation);
		}
	}

	public void setNumberFormat(NumberFormat numberFormat) {
		this.numberFormat = numberFormat;
		if (numberFormat != null) {
			this.scale = numberFormat.getScale();
			this.maxVal = numberFormat.getMaxVal();
			this.minVal = numberFormat.getMinVal();
		}
		requestRepaint();
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public void setPropertyDataSource(Property newDataSource) {
		if (newDataSource == null || Number.class.isAssignableFrom(newDataSource.getType())) {
			super.setPropertyDataSource(newDataSource);
		} else {
			throw new IllegalArgumentException("SpinnerTextField only supports Number properties");
		}
	}

	@Override
	public void setRequired(boolean required) {
		super.setRequired(required);

		setAllowNull(required);
	}

	public void setScale(Integer scale) {
		this.scale = scale;
	}

	public void setSuffix(String postfix) {
		this.suffix = postfix;
	}

	public void setTextAlignment(TextAlignment textAlignment) {
		this.textAlignment = textAlignment;
	}

	@Override
	protected void setValue(Object newValue, boolean repaintIsNotNeeded) throws Property.ReadOnlyException, Property.ConversionException {

		/*
		 * First handle special case when the client side component have a
		 * number string but value is null (e.g. unparsable number string typed
		 * in by the user). No value changes should happen, but we need to do
		 * some internal housekeeping.
		 */
		if (newValue == null) {
			/*
			 * Side-effects of setInternalValue clears possible previous strings
			 * and flags about invalid input.
			 */
			setInternalValue(null);

			/*
			 * Due to SpinnerTextField's special implementation of isValid(),
			 * SpinnerTextFields validity may change although the logical value
			 * does not change. This is an issue for Form which expects that
			 * validity of Fields cannot change unless actual value changes.
			 * 
			 * So we check if this field is inside a form and the form has
			 * registered this as a field. In this case we repaint the form.
			 * Without this hacky solution the form might not be able to clean
			 * validation errors etc. We could avoid this by firing an extra
			 * value change event, but feels like at least as bad solution as
			 * this.
			 */
			notifyFormOfValidityChange();
			requestRepaint();
			return;
		} else {
			super.setValue(newValue, repaintIsNotNeeded);
		}
	}


	@SuppressWarnings("unchecked")
	@Override
	public Number getValue() {
		Object v = super.getValue();
		if (v != null) {
			return (T) v;
		} else if (v == null && !isAllowNull()) {
			return (T) new Double(0);
		} else {
			return null;
		}
	}
}