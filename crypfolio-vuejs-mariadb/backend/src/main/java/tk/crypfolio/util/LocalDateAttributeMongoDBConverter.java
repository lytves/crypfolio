package tk.crypfolio.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * {@link LocalDateAttributeMongoDBConverter} dedicated to deal conversion between {@link LocalDate} and {@link Date},
 * especially is used to convert dates to/from MongoDB
 * This converter is part of JDK 8/JPA 2.1 compatibility workaround in the waiting of a new version JPA.
 */

@Converter(autoApply = true)
public class LocalDateAttributeMongoDBConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDate locDate) {
        return (locDate == null ? null : java.sql.Date.valueOf(locDate));
    }

    @Override
    public LocalDate convertToEntityAttribute(Date dateToConvert) {
        if (dateToConvert != null) {
            return dateToConvert.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return null;
    }
}