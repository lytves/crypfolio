package tk.crypfolio.util;

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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A utility class-converter, is using  in p:calendar to convert  Date (String) <-> LocalDate
 */
@FacesConverter("calendarLocalDateConverter")
public class CalendarLocalDateConverter implements Converter {

    private static final Logger logger = Logger.getLogger(CalendarLocalDateConverter.class.getName());

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String submittedString) {

        if (submittedString == null || submittedString.isEmpty()) {
            return null;
        }

        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.datePattern, Constants.mainLocale);
            return LocalDate.parse(submittedString, formatter);

        } catch (DateTimeParseException e) {

            logger.log(Level.WARNING, "DateTimeParseException en CalendarLocalDateConverter getAsObject!");
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

            logger.log(Level.WARNING, "ConverterException en CalendarLocalDateConverter getAsObject!");
            throw new ConverterException(new FacesMessage(object.toString() + " is not a valid LocalDate"), e);
        }
    }
}