package com.bwgjoseph.springbootdebeziummongodbes.partialdate;

import java.time.DateTimeException;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

/**
 * A year-month in the ISO-8601 calendar system, such as {@code 0000-00-01}.
 * <p>
 * This class does not store or represent a year, month, time or time-zone.
 * For example, the value "01" can be stored in a {@code Day}.
 * <p>
 */
public final class Day implements TemporalAccessor {

    /**
     * The day being represented.
     */
    private final int day;

    /**
     * Constructor.
     *
     * @param year the day to represent
     */
    public Day(int day) {
        this.day = day;
    }

    /**
     * Obtains an instance of {@code Day} from a day.
     *
     * @param day the day-of-month to represent, from 1 to 31
     * @return the day, not null
     * @throws DateTimeException if field value is invalid
     */
    public static Day of(int day) {
        // since 0000-00-00 will be parsed as Dateless, so to use Day, it must be
        // in the range of 1 - 31
        if (day < 1 || day > 31) {
            throw new DateTimeException("Illegal value for Day field, value " + day);
        }
        return new Day(day);
    }

    /**
     * Gets the day field.
     * <p>
     * This method returns the primitive {@code int} value for the day-of-month.
     *
     * @return the day-of-month, from 0 to 31
     */
    public int getValue() {
        return day;
    }

    @Override
    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField) {
            return field == ChronoField.DAY_OF_MONTH;
        }
        return field != null && field.isSupportedBy(this);
    }

    @Override
    public long getLong(TemporalField field) {
        if (field instanceof ChronoField chronoField) {
            if (chronoField == ChronoField.DAY_OF_MONTH) {
                return day;
            }
            throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        return field.getFrom(this);
    }
}
