package tk.crypfolio.util;

import tk.crypfolio.model.CoinEntity;
import tk.crypfolio.business.ApplicationContainer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class-converter, is using to convert CoinEntity object to String to show it in the dialog window
 */
@Named
public class AutocompleteCoinConverter implements Converter {

    private static final Logger logger = Logger.getLogger(AutocompleteCoinConverter.class.getName());

    @Inject
    private ApplicationContainer applicationContainer;

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {

        if (value != null && value.trim().length() > 0) {

            try {
                for (CoinEntity coin : applicationContainer.getAllCoinsListing()) {
                    if (coin.getId().equals(Long.valueOf(value))) {
                        return coin;
                    }
                }
            } catch (NumberFormatException e) {
                logger.log(Level.WARNING, "Error AutocompleteCoinConverter getAsObject!");
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        if (object != null) {
            return String.valueOf(((CoinEntity) object).getId());
        }
        return null;
    }
}