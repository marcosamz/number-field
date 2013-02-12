package it.portus.addon.numberfield.widgetset.shared;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.vaadin.terminal.gwt.client.VConsole;

/**
 * @author Francesco Portus (portusgraphics@gmail.com)
 * 
 * @since 20/nov/2012
 */
public class NumberFormatter {

	public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;
	private static NumberFormatter instance = null;

	public static NumberFormatter getInstance() {
		if (instance == null) {
			instance = new NumberFormatter();
		}

		return instance;
	}

	public static void main(String[] args) {
		NumberFormatter numberFormatter = new NumberFormatter();
		numberFormatter.setDecimalsSeparator(',');
		numberFormatter.setGroupingSeparator('.');
		numberFormatter.setScale(0);

		String value = "104,99000000000001";
		Number val = numberFormatter.unformat(value);
		System.out.println("unformat: " + val);

		BigDecimal bigDecimal = new BigDecimal(value);
		bigDecimal = bigDecimal.setScale(numberFormatter.getScale(), DEFAULT_ROUNDING_MODE);

		value = bigDecimal.toString();
		String res = numberFormatter.format(value);
		System.out.println("format: " + res);

		val = numberFormatter.unformat(value);
		System.out.println("unformat: " + val);
	}

	private static String removeChar(String s, char c) {
		String r = "";
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) != c) r += s.charAt(i);
		}
		return r;
	}

	private static String toOnlyNumbers(String str) {
		String formatted = "";
		for (int i = 0; i < str.length(); i++) {
			char char_ = str.charAt(i);
			if (formatted.length() == 0 && char_ == '0') continue;
			if ((char_ + "").matches("[0-9]")) {
				formatted = formatted + char_;
			}
		}

		return formatted;
	}

	private String currencySymbol = "";
	private char percentageSymbol = 0;
	private char decimalsSeparator = '.';
	private char groupingSeparator = 0;

	private boolean allowNegative = true;

	private int scale = 0;

	public NumberFormatter() {
	}

	public NumberFormatter(int scale) {
		super();
		this.scale = scale;
	}

	private String fillWithZeroes(String str, int scale) {
		while (str.length() < (scale + 1))
			str = '0' + str;
		return str;
	}

	public String format(Number value) {
		return format(value, scale);
	}

	public String format(Number value, int scale) {
		String str = null;
		if (value != null) {
			BigDecimal bigDecimal = new BigDecimal(value.toString());
			bigDecimal = bigDecimal.setScale(scale, DEFAULT_ROUNDING_MODE);

			str = bigDecimal.toString();
		}
		return format(str, scale);
	}

	public String format(Number value, NumberFormat numberFormat) {
		return format(value, numberFormat, numberFormat.getScale());
	}

	public String format(Number value, NumberFormat numberFormat, int scale) {
		String format = format(value, scale);
		switch (numberFormat) {
		case PERCENTAGE:
			format += percentageSymbol;
			break;
		case CURRENCY:
			format = currencySymbol + " " + format;
			break;

		default:
			break;
		}

		return format;
	}

	public String format(String str) {
		return format(str, scale);
	}

	public String format(String str, int scale) {
		if (str != null && str.length() > 0) {

			// formatting settings
			String formatted = fillWithZeroes(toOnlyNumbers(str), scale);
			String thousandsFormatted = "";
			int thousandsCount = 0;

			// split integer from cents
			String centsVal = formatted.substring(formatted.length() - scale, (formatted.length() - scale) + scale);
			String integerVal = formatted.substring(0, (formatted.length() - scale));

			String decimalSec = "";
			if (centsVal != null && !"".equals(centsVal.trim())) {
				decimalSec = decimalsSeparator + centsVal;
			}

			// apply cents pontuation
			formatted = integerVal + decimalSec;

			// apply thousands pontuation
			if (groupingSeparator != 0) {
				for (int j = integerVal.length(); j > 0; j--) {
					String char_ = integerVal.substring(j - 1, (j - 1) + 1);
					thousandsCount++;
					if (thousandsCount % 3 == 0) char_ = groupingSeparator + char_;
					thousandsFormatted = char_ + thousandsFormatted;
				}
				if ((groupingSeparator + "").equals(thousandsFormatted.substring(0, 1))) {
					thousandsFormatted = thousandsFormatted.substring(1, thousandsFormatted.length());
				}
				formatted = thousandsFormatted + decimalSec;
			}

			// if the string contains a dash, it is negative - add it to the
			// begining (except for zero)
			if (allowNegative && str.indexOf('-') != -1 && (integerVal.length() > 0 || centsVal.length() > 0)) formatted = '-' + formatted;
			return formatted;
		} else {
			return "";
		}
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public char getDecimalsSeparator() {
		return decimalsSeparator;
	}

	public char getGroupingSeparator() {
		return groupingSeparator;
	}

	public char getPercentageSymbol() {
		return percentageSymbol;
	}

	public int getScale() {
		return scale;
	}

	public boolean isAllowNegative() {
		return allowNegative;
	}

	public void setAllowNegative(boolean allowNegative) {
		this.allowNegative = allowNegative;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public void setDecimalsSeparator(char decimalsSeparator) {
		this.decimalsSeparator = decimalsSeparator;
	}

	public void setGroupingSeparator(char groupingSeparator) {
		this.groupingSeparator = groupingSeparator;
	}

	public void setPercentageSymbol(char percentageSymbol) {
		this.percentageSymbol = percentageSymbol;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public Number unformat(String text) {
		Number result = null;
		if (text != null && !"".equals(text.trim())) {
			try {

				// FIXME CONTROLLARE!!
				if (groupingSeparator == '.') {
					String[] temp = text.split("\\.");
					if (/* NOT */!(temp.length == 2 && (temp[1].length() != 3))) {
						text = removeChar(text, groupingSeparator);
					}
				}
				text = text.replace(decimalsSeparator, '.');
				BigDecimal bigDecimal = new BigDecimal(text);
				bigDecimal = bigDecimal.setScale(getScale(), DEFAULT_ROUNDING_MODE);

				result = bigDecimal;
			} catch (Exception e) {
				VConsole.log(e);

				result = -1;
			}
		}

		return result;
	}
}
