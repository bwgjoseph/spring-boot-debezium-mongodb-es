package com.bwgjoseph.springbootdebeziummongodbes.partialdate;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * This is a wrapper class around TemporalAccessor to support Partial Local Date
 * <p>
 * This allows for all various type of partial date to be declared:
 * <ul>
 *  <li>2022-01-01 - [LocalDate]</li>
 *  <li>2022-01-00 - [YearMonth]</li>
 *  <li>2022-00-01 - [YearDay]</li>
 *  <li>0000-01-01 - [MonthDay]</li>
 *  <li>2022-00-00 - [Year]</li>
 *  <li>0000-01-00 - [Month]</li>
 *  <li>0000-00-01 - [Day]</li>
 *  <li>0000-00-00 - [Dateless]</li>
 * </ul>
 * </p>
 * Java supports all except {@code Day} and {@code Dateless}. Hence, to support it,
 * a simple {@link Day} is implemented and classify {@code Dateless} as {@link java.time.Year Year}
 * <p>
 * The format it support is only {@code yyyy-MM-dd} and nothing else
 * </p>
 */
public class PartialLocalDate {
    private static final Logger log = Logger.getLogger(PartialLocalDate.class.getName());

    private static final String YEAR_FORMAT = "%04d-00-00";
    private static final String YEAR_MONTH_FORMAT = "%04d-%02d-00";
    private static final String YEAR_MONTH_DAY_FORMAT = "%04d-%02d-%02d";
    private static final String VALID_DATE_FORMAT_REGEX = "(\\d{4})-(\\d{2})-(\\d{2})";

    enum Classifier { LOCAL_DATE, YEAR_MONTH, YEAR_DAY, MONTH_DAY, YEAR, MONTH, DAY, DATELESS }

    /**
     * Representation of Date object
     */
    private final TemporalAccessor temporalAccessor;
    /**
     * Representation of a classification of a Date object
     *
     * @see {@link PartialLocalDate.Classifier Classifier}
     */
    private Classifier classifier;

    /**
     * Constructor
     *
     * @param localDate accept only valid date string in yyyy-MM-dd format
     * @throws UnsupportedOperationException if value is of invalid date format
     */
    public PartialLocalDate(String localDate) {
        if (!this.isValidDateString(localDate)) {
            throw new UnsupportedOperationException("Not a valid date format, ensure in yyyy-MM-dd format");
        }

        this.temporalAccessor = this.toTemporalAccessor(this.toToken(localDate));
    }

    /**
     * Obtains an instance of {@code PartialLocalDate} from a year.
     *
     * @param year year in 4 digit
     * @return PartialLocalDate, not null
     */
    public static PartialLocalDate of(int year) { // do i want to use Year instance of int?
        String sf = String.format(YEAR_FORMAT, year);

        return new PartialLocalDate(sf);
    }

    /**
     * Obtains an instance of {@code PartialLocalDate} from a year and month.
     *
     * @param year in 4 digit
     * @param month in 2 digit
     * @return PartialLocalDate, not null
     */
    public static PartialLocalDate of(int year, int month) {
        String sf = String.format(YEAR_MONTH_FORMAT, year, month);

        return new PartialLocalDate(sf);
    }

    /**
     * Obtains an instance of {@code PartialLocalDate} from a year, month and day.
     *
     * @param year year in 4 digit,
     * @param month in 2 digit
     * @param day in 2 digit
     * @return PartialLocalDate, not null
     */
    public static PartialLocalDate of(int year, int month, int day) {
        String sf = String.format(YEAR_MONTH_DAY_FORMAT, year, month, day);

        return new PartialLocalDate(sf);
    }

    /**
     * Obtains an instance of {@code PartialLocalDate} from {@code LocalDate.now()}
     *
     * @return PartialLocalDate, not null
     */
    public static PartialLocalDate now() {
        LocalDate localDate = LocalDate.now();

        return of(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth());
    }

    /**
     * Validate if the given date string is the expected format
     *
     * @param localDate given date
     * @return is valid date format
     */
    private boolean isValidDateString(String localDate) {
        return localDate.matches(VALID_DATE_FORMAT_REGEX);
    }

    /**
     * Given valid date, tokenize it to year, month, day section
     *
     * @param date valid date format
     * @return array of size 3; year, month, day
     */
    private List<String> toToken(String date) {
        return Collections.list(new StringTokenizer(date, "-"))
            .stream()
            .map(String.class::cast)
            .toList();
    }

    /**
     * Converts a tokenize date to {@code TemporalAccessor} object
     *
     * @param tokens tokenize date in [yyyy, mm, dd] format
     * @return TemporalAccessor object
     */
    private TemporalAccessor toTemporalAccessor(List<String> tokens) {
        List<Integer> intTokens = tokens.stream().map(Integer::parseInt).toList();

        // to handle direct Year, Month and Day so we don't have to brute force
        if (isYearOnly(intTokens)) {
            return asYear(intTokens);
        }

        if (isMonthOnly(intTokens)) {
            return asMonth(intTokens);
        }

        if (isDayOnly(intTokens)) {
            return asDay(intTokens);
        }

        // otherwise, we let the parser do it so we don't need to write
        // the various permuation in order to fit them into the various date type
        try {
            /**
             * Given that 0000 is a valid Year, to prevent {@code 0000-01-01} from being parse as a {@code LocalDate}
             * we must first attempt to let it parse as a {@code MONTH_DAY}
             *
             * if either month or day is 00, it will throw an exception and let the next parser continue
             */
            if (getYearToken(intTokens) == 0000) {
                return asMonthDay(intTokens);
            }

            return asLocalDate(intTokens);
        } catch (DateTimeException e) {
            try {
                return asYearMonth(intTokens);
            } catch (DateTimeException ee) {
                try {
                    return asMonthDay(intTokens);
                } catch (DateTimeException eee) {
                    try {
                        return asYearDay(intTokens);
                    } catch (DateTimeException eeee) {
                        log.fine("in exception");
                    }
                }
            }

        }

        return asDateless(intTokens);
    }

    /**
     * Determine is this is a Year only formatted date (2022-00-00)
     *
     * @param intTokens year, month, day token
     * @return true for year only date
     */
    private boolean isYearOnly(List<Integer> intTokens) {
        return getYearToken(intTokens) != 0000
            && getMonthToken(intTokens) == 00
            && getDayToken(intTokens) == 00;
    }

    /**
     * Determine is this is a Month only formatted date (0000-01-00)
     *
     * @param intTokens year, month, day token
     * @return true for month only date
     */
    private boolean isMonthOnly(List<Integer> intTokens) {
        return getYearToken(intTokens) == 0000
            && getMonthToken(intTokens) != 00
            && getDayToken(intTokens) == 00;
    }

    /**
     * Determine is this is a Day only formatted date (0000-00-10)
     *
     * @param intTokens year, month, day token
     * @return true for day only date
     */
    private boolean isDayOnly(List<Integer> intTokens) {
        return getYearToken(intTokens) == 0000
            && getMonthToken(intTokens) == 00
            && getDayToken(intTokens) != 00;
    }

    /**
     * Extract Year token
     *
     * @param intTokens year, month, day token
     * @return year field value
     */
    private int getYearToken(List<Integer> intTokens) {
        return intTokens.get(0);
    }

    /**
     * Extract Month token
     *
     * @param intTokens year, month, day token
     * @return month field value
     */
    private int getMonthToken(List<Integer> intTokens) {
        return intTokens.get(1);
    }

    /**
     * Extract Day token
     *
     * @param intTokens year, month, day token
     * @return day field value
     */
    private int getDayToken(List<Integer> intTokens) {
        return intTokens.get(2);
    }

    /**
     * Parse given date as LocalDate
     *
     * @param intTokens year, month, day token
     * @return LocalDate TemporalAccessor object
     */
    private TemporalAccessor asLocalDate(List<Integer> intTokens) {
        log.fine("attempting to parse as LocalDate");
        LocalDate date = LocalDate.of(getYearToken(intTokens), getMonthToken(intTokens), getDayToken(intTokens));
        this.classifier = Classifier.LOCAL_DATE;
        return date;
    }

    /**
     * Parse given date as YearMonth
     *
     * @param intTokens year, month, day token
     * @return YearMonth TemporalAccessor object
     */
    private TemporalAccessor asYearMonth(List<Integer> intTokens) {
        log.fine("attempting to parse as YearMonth");
        YearMonth date = YearMonth.of(getYearToken(intTokens), getMonthToken(intTokens));
        this.classifier = Classifier.YEAR_MONTH;
        return date;
    }

    /**
     * Parse given date as YearDay
     *
     * @param intTokens year, month, day token
     * @return YearDay TemporalAccessor object
     */
    private TemporalAccessor asYearDay(List<Integer> intTokens) {
        log.fine("attempting to parse as YearDay");
        LocalDate date = LocalDate.ofYearDay(getYearToken(intTokens), getDayToken(intTokens));
        this.classifier = Classifier.YEAR_DAY;
        return date;
    }

    /**
     * Parse given date as MonthDay
     *
     * @param intTokens year, month, day token
     * @return MonthDay TemporalAccessor object
     */
    private TemporalAccessor asMonthDay(List<Integer> intTokens) {
        log.fine("attempting to parse as MonthDay");
        MonthDay date = MonthDay.of(getMonthToken(intTokens), getDayToken(intTokens));
        this.classifier = Classifier.MONTH_DAY;
        return date;
    }

    /**
     * Parse given date as Year
     *
     * @param intTokens year, month, day token
     * @return Year TemporalAccessor object
     */
    private TemporalAccessor asYear(List<Integer> intTokens) {
        log.fine("attempting to parse as Year");
        Year date = Year.of(getYearToken(intTokens));
        this.classifier = Classifier.YEAR;
        return date;
    }

    /**
     * Parse given date as Month
     *
     * @param intTokens year, month, day token
     * @return Month TemporalAccessor object
     */
    private TemporalAccessor asMonth(List<Integer> intTokens) {
        log.fine("attempting to parse as Month");
        Month date = Month.of(getMonthToken(intTokens));
        this.classifier = Classifier.MONTH;
        return date;
    }

    /**
     * Parse given date as Day
     *
     * @param intTokens year, month, day token
     * @return Day TemporalAccessor object
     */
    private TemporalAccessor asDay(List<Integer> intTokens) {
        log.fine("attempting to parse as Day");
        Day date = Day.of(getDayToken(intTokens));
        this.classifier = Classifier.DAY;
        return date;
    }

    /**
     * Parse given date as Dateless
     *
     * @param intTokens year, month, day token
     * @return Dateless TemporalAccessor object
     */
    private TemporalAccessor asDateless(List<Integer> intTokens) {
        log.fine("attempting to parse as Dateless");
        Year date = Year.of(getYearToken(intTokens));
        this.classifier = Classifier.DATELESS;
        return date;
    }

    /**
     * Getter for {@code TemporalAccessor}
     *
     * @return TemporalAccessor object
     */
    public TemporalAccessor getTemporalAccessor() {
        return this.temporalAccessor;
    }

    /**
     * Getter for {@code Year} field value
     *
     * @return year field value
     */
    public int getYearValue() {
        return this.temporalAccessor.isSupported(ChronoField.YEAR)
            ? this.temporalAccessor.get(ChronoField.YEAR)
            : 0000;
    }

    /**
     * Getter for {@code Month} field value
     *
     * If the {@code Classifier} is {@code YEAR_DAY}, we
     * return directly instead of parsing it
     *
     * @return month field value
     */
    public int getMonthValue() {
        // don't handle month
        if (this.classifier == Classifier.YEAR_DAY) return 00;

        return this.temporalAccessor.isSupported(ChronoField.MONTH_OF_YEAR)
            ? this.temporalAccessor.get(ChronoField.MONTH_OF_YEAR)
            : 00;
    }

    /**
     * Getter for {@code Day} field value
     *
     * @return day field value
     */
    public int getDayValue() {
        return this.temporalAccessor.isSupported(ChronoField.DAY_OF_MONTH)
        ? this.temporalAccessor.get(ChronoField.DAY_OF_MONTH)
        : 00;
    }

    /**
     * Getter for {@link PartialLocalDate.Classifier Classifier}
     *
     * @return Classifier enum value
     */
    public Classifier getClassifier() {
        return this.classifier;
    }

    /**
     * The only valid date would be LOCAL_DATE
     *
     * @return valid if Classifier is LOCAL_DATE
     */
    public boolean isValidLocalDate() {
        return this.classifier == Classifier.LOCAL_DATE;
    }

    public String toString() {
        return String.format(YEAR_MONTH_DAY_FORMAT,
            this.getYearValue(),
            this.getMonthValue(),
            this.getDayValue()
        );
    }

}
