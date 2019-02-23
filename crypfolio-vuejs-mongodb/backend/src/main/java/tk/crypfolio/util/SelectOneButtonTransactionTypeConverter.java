package tk.crypfolio.util;

import tk.crypfolio.common.TransactionType;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class-converter, is using to convert TransactionType object to String to show it in the dialog window
 */
@Named
public class SelectOneButtonTransactionTypeConverter implements Converter {

    private static final Logger logger = Logger.getLogger(SelectOneButtonTransactionTypeConverter.class.getName());

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {

        if (value != null && value.trim().length() > 0) {

            try {

                for (TransactionType type : TransactionType.values()) {

                    if (type.getType().equals(String.valueOf(value))) {
                        return type;
                    }
                }

            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Error SelectOneButtonTransactionTypeConverter getAsObject!");
            }

        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {

        if (object != null) {
            return (String) object;
        }
        return null;
    }
}