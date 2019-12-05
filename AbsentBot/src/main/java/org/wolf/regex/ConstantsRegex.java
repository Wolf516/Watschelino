package org.wolf.regex;

public interface ConstantsRegex {
    String INFINITE = "*";
    String ANY = ".";
    String ANY_NUMBER = "[0-9]";
    String ANY_ALPHA = "[a-zA-Z]";
    String ANY_ALPHA_LOWER = "[a-z]";
    String ANY_ALPHA_UPPER = "[A-Z]";

    //Dates
    String YEAR = "(([12]\\d){0,1}\\d{2})";
    String YEAR_SHORT = "(\\d{2})";
    String YEAR_LONG = "([12]\\d{3})";
    String MONTH = "((0{0,1}[1-9])|(1[012]))";
    String DAY = "((0{0,1}[1-9])|([12]{0,1}[0-9])|(3[01]))";
    String DATE_SEPERATOR = "[./-]";
    String DATE_EXACT_SHORT = "(" + DAY + DATE_SEPERATOR + MONTH + DATE_SEPERATOR + YEAR_SHORT +")";
    String DATE_EXACT_LONG = "(" + DAY + DATE_SEPERATOR + MONTH + DATE_SEPERATOR + YEAR_LONG +")";
    String DATE = "(" + DAY + DATE_SEPERATOR + MONTH + "(" + DATE_SEPERATOR +"|(" + DATE_SEPERATOR + YEAR + ")){0,1})";
    String DATE_YEAR = "(" + DAY + DATE_SEPERATOR + MONTH + DATE_SEPERATOR + YEAR + ")";
    String DATE_NO_YEAR = "("+ DAY + DATE_SEPERATOR + MONTH + DATE_SEPERATOR + "{0,1})";

    //Week Days
    String MONDAY = "(montag|monday)";
    String TUESDAY = "(dienstag|tuesday)";
    String WEDNESDAY = "(mittwoch|wednesday)";
    String THURSDAY = "(donnerstag|thursday)";
    String FRIDAY = "(freitag|friday)";
    String SATURDAY = "(samstag|saturday)";
    String SUNDAY = "(sonntag|sunday)";
    String ANY_DAY = "(" +  MONDAY + "|" + TUESDAY + "|" + WEDNESDAY + "|" + THURSDAY + "|" + FRIDAY + "|" + SATURDAY + "|" + SUNDAY + ")";

    //Months
    String JANUARY = "(january|januar)";
    String FEBRUARY = "(february|februar)";
    String MARCH = "(march|m\\u00e4rz)";
    String APRIL = "(april|april)";
    String MAY = "(may|mai)";
    String JUNE = "(june|juni)";
    String JULY = "(july|juli)";
    String AUGUST = "(august|august)";
    String SEPTEMBER = "(september|september)";
    String OCTOBER = "(october|oktober)";
    String NOVEMBER = "(november|november)";
    String DECEMBER = "(december|dezember)";
    String ANY_MONTH = "(" + JANUARY  + "|" + FEBRUARY + "|" + MARCH + "|" + APRIL + "|" + MAY + "|" + JUNE + "|" + JULY + "|" + AUGUST + "|" + SEPTEMBER + "|" + OCTOBER + "|" + NOVEMBER + "|" + DECEMBER + ")";

    //time units
    String DAYS = "(\\stag|\\sday)";
    String WEEKS = "(\\swoche|\\sweek)";
    String MONTHS = "(\\smonat|\\smonths)";

    //Start/ End
    String UNTIL = "(\\sbis\\s|\\sto\\s|\\suntil\\s)";
    String FOR = "(\\sf\\u00fcr\\s|\\sfor\\s)";
    String FROM = "(ab\\s|from\\s)";
    String NEXT = "(n\\u00e4chst|next)";
    String THIS = "(am\\s|diese|this)";

    //Spans
    String WEEKEND = "(\\swochenende|weekend)";
}
