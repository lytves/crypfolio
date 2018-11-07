package tk.crypfolio.util;

import tk.crypfolio.model.CoinEntity;
import tk.crypfolio.business.ApplicationContainer;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * A utility class-converter, is using to convert coin object to normal form to show it in the modal choose windows (and vice versa)
 */
@Named
public class AutocompleteCoinConverter implements Converter {

    @Inject
    private ApplicationContainer applicationContainer;

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String value) {

        if (value != null && value.trim().length() > 0) {

            try {
                for (CoinEntity coin: applicationContainer.getAllCoinsListing()){
                    if (coin.getId().equals(Long.valueOf(value))){
                        return coin;
                    }
                }
            } catch (NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid coin."));
            }

        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        if (object != null) {
            return String.valueOf(((CoinEntity) object).getId());
        } else {
            return null;
        }
    }
}
