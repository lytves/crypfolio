package tk.crypfolio.util;

import tk.crypfolio.common.CurrencyType;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class-converter, is using to convert CurrencyType object to String to show it in the dialog window
 */
@Named
public class SelectOneMenuCurrencyTypeConverter implements Converter {

    private static final Logger logger = Logger.getLogger(SelectOneMenuCurrencyTypeConverter.class.getName());

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {

        if (value != null && value.trim().length() > 0) {

            try {

                for (CurrencyType currency : CurrencyType.values()) {

                    if (currency.getCurrency().equals(String.valueOf(value))) {
                        return currency;
                    }
                }

            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Error selectOneMenuCurrencyConverter getAsObject!");
            }

        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        if (object != null) {
            return String.valueOf(((CurrencyType) object).getCurrency());
        } else {
            return null;
        }
    }
}