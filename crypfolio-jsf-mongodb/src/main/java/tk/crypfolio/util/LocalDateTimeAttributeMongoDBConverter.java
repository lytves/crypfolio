package tk.crypfolio.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * {@link LocalDateTimeAttributeMongoDBConverter} dedicated to deal conversion between {@link LocalDateTime} and {@link Date},
 * especially is used to convert dates to/from MongoDB
 * This converter is part of JDK 8/JPA 2.1 compatibility workaround in the waiting of a new version JPA.
 */

@Converter(autoApply = true)
public class LocalDateTimeAttributeMongoDBConverter implements AttributeConverter<LocalDateTime, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDateTime locDateTime) {
        return (locDateTime == null ? null : Timestamp.valueOf(locDateTime));
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Date dateToConvert) {
        if (dateToConvert != null) {
            return dateToConvert.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        return null;
    }
}