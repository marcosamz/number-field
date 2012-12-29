package it.portus.addon.numberfield.widgetset.shared;

/**
 * @author Francesco Portus (portusgraphics@gmail.com)
 * 
 * @since 14/dic/2012
 */
public enum TextAlignment {
	CENTER {
		@Override
		String getTextAlignString() {
			return "center";
		}
	},
	JUSTIFY {
		@Override
		String getTextAlignString() {
			return "justify";
		}
	},
	LEFT {
		@Override
		String getTextAlignString() {
			return "left";
		}
	},
	RIGHT {
		@Override
		String getTextAlignString() {
			return "right";
		}

	};
	abstract String getTextAlignString();
}