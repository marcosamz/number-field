package it.portus.addon.numberfield.widgetset.shared;


/**
 * @author Francesco Portus (portusgraphics@gmail.com)
 *
 * @since 14/dic/2012
 */
public enum TextAlignment {
	CENTER {
		String getTextAlignString() {
			return "center";
		}
	},
	JUSTIFY {
		String getTextAlignString() {
			return "justify";
		}
	},
	LEFT {
		String getTextAlignString() {
			return "left";
		}
	},
	RIGHT {
		String getTextAlignString() {
			return "right";
		}

	};
	abstract String getTextAlignString();
}