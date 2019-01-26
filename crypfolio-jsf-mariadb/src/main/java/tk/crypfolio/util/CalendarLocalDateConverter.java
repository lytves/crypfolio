package tk.crypfolio.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tk.crypfolio.common.Constants;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * A utility class-converter, is using  in p:calendar to convert  Date (String) <-> LocalDate
 */
@FacesConverter("calendarLocalDateConverter")
public class CalendarLocalDateConverter implements Converter {

    private static final Logger LOGGER = LogManager.getLogger(CalendarLocalDateConverter.class);

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String submittedString) {

        if (submittedString == null || submittedString.isEmpty()) {
            return null;
        }

        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.datePattern, Constants.mainLocale);
            return LocalDate.parse(submittedString, formatter);

        } catch (DateTimeParseException e) {

            LOGGER.error("DateTimeParseException en CalendarLocalDateConverter getAsObject!");
            throw new ConverterException(new FacesMessage(submittedString + " is not a valid LocalDate"), e);
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {

        if (object == null) {
            return "";
        }

        try {
            LocalDate localDate;

            if ((("today").toLowerCase()).equals(object.toString().toLowerCase())) {
                localDate = LocalDate.now();
            } else {
                localDate = (LocalDate) object;
            }

            return localDate.format(DateTimeFormatter.ofPattern(Constants.datePattern, Constants.mainLocale));

        } catch (ConverterException e) {

            LOGGER.error("ConverterException en CalendarLocalDateConverter getAsObject!");
            throw new ConverterException(new FacesMessage(object.toString() + " is not a valid LocalDate"), e);
        }
    }
}