Numeric text field that allows insert only a valid numbers with keyboard and also allows spin value up/down.

This Component use generic java type for all commons Number type.

You can use your mode for the component, if you want the text field easier to use:

`TextualNumberField<?>`

If you want the UP / DOWN buttons on the right:

`SpinnerNumberField<?>`

Inputs are validated on client- and server-side.
Allows also two kind of input type so PERCENTAGE and CURRENCY.

It's possible to change commons paramaters as ,if setted, symbol, max and min value, negative values, text alignment, LOCALE (automatically adapt it for decimal and groups separator or currency symbol), etc..