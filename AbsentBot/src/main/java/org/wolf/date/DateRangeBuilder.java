package org.wolf.date;

import org.wolf.regex.ConstantsRegex;
import org.wolf.regex.DTRegex;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateRangeBuilder {
    private static final String ANY_STRING =ConstantsRegex.ANY+ConstantsRegex.INFINITE;
    private static final Integer ANY_STRING_KEY = 1000;

    private static final String START_AND_END = ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                                ConstantsRegex.DATE +
                                                ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                                ConstantsRegex.DATE +
                                                ConstantsRegex.ANY + ConstantsRegex.INFINITE;
    private static final Integer START_END_KEY = 10;

    private static final String END_ONLY =      ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                                ConstantsRegex.UNTIL +
                                                ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                                ConstantsRegex.DATE +
                                                ConstantsRegex.ANY + ConstantsRegex.INFINITE;
    private static final Integer END_ONLY_KEY = 20;

    private static final String START_ONLY =    ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                                ConstantsRegex.FROM +
                                                ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                                ConstantsRegex.DATE +
                                                ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                                ConstantsRegex.ANY_NUMBER + "+" +
                                                ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                                "(" + ConstantsRegex.DAYS + "|" + ConstantsRegex.WEEKS + "|" + ConstantsRegex.MONTHS + ")" +
                                                ConstantsRegex.ANY + ConstantsRegex.INFINITE;
    private static final Integer START_ONLY_KEY = 30;

    private static final String NEXT_DAY_OR_WEEKEND =   ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                                        "(" + ConstantsRegex.NEXT + "|" + ConstantsRegex.THIS + ")" +
                                                        ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                                        "(" + ConstantsRegex.ANY_DAY + "|" + ConstantsRegex.WEEKEND + ")" +
                                                        ConstantsRegex.ANY + ConstantsRegex.INFINITE;
    private static final Integer NEXT_DAY_OR_WEEKEND_KEY = 50;

    private static final String NUMBER_IN_TIME_UNITS =  ConstantsRegex.ANY_NUMBER + "+" +
                                                        ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                                        "(" + ConstantsRegex.DAYS + "|" + ConstantsRegex.WEEKS + "|" + ConstantsRegex.MONTHS + ")";

    private static final String NEXT_SPAN = ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                            "(" + ConstantsRegex.NEXT + "|" + ConstantsRegex.FOR + "){0,1}" +
                                            ConstantsRegex.ANY + ConstantsRegex.INFINITE +
                                            ConstantsRegex.ANY_NUMBER + ConstantsRegex.INFINITE +
                                            "(" + ConstantsRegex.DAYS + "|" + ConstantsRegex.WEEKS + "|" + ConstantsRegex.MONTHS + ")" +
                                            ConstantsRegex.ANY + ConstantsRegex.INFINITE;
    private static final Integer NEXT_SPAN_KEY = 60;

    /*tbd...
    private static final String RELATIVE_SPAN_BY_DAY_BACKWARDS = "";
    private static final Integer RELATIVE_SPAN_BY_DAY_BACKWARDS_KEY = 0;
    private static final String RELATIVE_SPAN_BY_DAY_FORWARD = "";
    private static final Integer RELATIVE_SPAN_BY_DAY_FORWARD_KEY = 0;
	*/
    private Date startDate;
    private Date endDate;
    private DTRegex regex;

    public DateRangeBuilder(){
        super();

        this.regex = new DTRegex();

        this.regex.registerExpression(ANY_STRING_KEY, ANY_STRING);
        this.regex.registerExpression(START_END_KEY, START_AND_END);
        this.regex.registerExpression(END_ONLY_KEY, END_ONLY);
        this.regex.registerExpression(START_ONLY_KEY, START_ONLY);
        this.regex.registerExpression(NEXT_DAY_OR_WEEKEND_KEY, NEXT_DAY_OR_WEEKEND);
        this.regex.registerExpression(NEXT_SPAN_KEY, NEXT_SPAN);
    }

    public void buildFromMessage(String message) throws ParseException {
        message = message.toLowerCase();

        ArrayList<Date> dates = new ArrayList<Date>();

        switch (this.regex.checkStringForExpression(message)){
            case 10: //START_END_KEY
                dates.addAll(this.getAllDatesFromMessage(message));
                break;
            case 20: //END_ONLY_KEY
                dates.add(new Date());
                dates.addAll(this.getAllDatesFromMessage(message));
                break;
            case 30: //START_ONLY_KEY
                //build substring with number and time unit
                String[] parts = message.split("\\s"+ConstantsRegex.DATE+"\\s");
                String sub = "";
                for (String msgPart : parts){
                    if (msgPart.matches(ConstantsRegex.ANY + ConstantsRegex.INFINITE + NUMBER_IN_TIME_UNITS + ConstantsRegex.ANY + ConstantsRegex.INFINITE)) {
                        sub = msgPart;
                        break;
                    }
                } 
                //extract number and time unit
                Pattern pattern = Pattern.compile(NUMBER_IN_TIME_UNITS);
                Matcher matcher = pattern.matcher(sub);
                if (matcher.find()){
                    //starts exactly with the number and ends exactly with the unit.
                    String numberAndUnit = matcher.group();
                    String[] splitted = numberAndUnit.split(" ");
                    String number = splitted[0];
                    String unit = " " + splitted[splitted.length-1];

                    Integer amount = Integer.valueOf(number);

                    //calculate start and end date
                    Date date = this.getAllDatesFromMessage(message).get(0);
                    dates.add(date);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    if (unit.matches(ConstantsRegex.DAYS)){
                        calendar.add(Calendar.DATE, amount);
                    }
                    if (unit.matches(ConstantsRegex.WEEKS)) {
                        calendar.add(Calendar.DATE, amount*7);
                    }
                    if (unit.matches(ConstantsRegex.MONTHS)) {
                        calendar.add(Calendar.MONTH, amount);
                    }

                    dates.add(calendar.getTime());
                }
                break;
            case 50:
                dates.addAll(this.getDatesForNextDayOrWeekend(message));
                break;
            case 60:
                dates.addAll(this.getDatesForSpan(message));
                break;
            default:
            	this.startDate = null;
            	this.endDate = null;
            	return;
        }

        dates.sort(null);
        this.startDate = dates.get(0);
        this.endDate = dates.get(1);
    }

    private ArrayList<Date> getDatesForSpan(String message){
        ArrayList<Date> dates = new ArrayList<Date>();

        try {
            Matcher nextAvailableMatcher = Pattern.compile(ConstantsRegex.NEXT).matcher(message);
            String processString = (nextAvailableMatcher.find() ? message.split(ConstantsRegex.NEXT)[1] : message);
            Pattern numberPattern = Pattern.compile(ConstantsRegex.ANY_NUMBER + "+");
            Matcher numberMatcher = numberPattern.matcher(processString);

            int amount = (numberMatcher.find() ? Integer.valueOf(numberMatcher.group()) : 1);

            Pattern unitPattern = Pattern.compile("(" + ConstantsRegex.DAYS + "|" + ConstantsRegex.WEEKS + "|" + ConstantsRegex.MONTHS + ")");
            Matcher unitMatcher = unitPattern.matcher(processString);

            int numberOfDaysPerUnit = 1;
            if (unitMatcher.find()){
                String unit = unitMatcher.group();
                if (unit.matches(ConstantsRegex.WEEKS))         numberOfDaysPerUnit = 7;
                else if (unit.matches(ConstantsRegex.MONTHS))   numberOfDaysPerUnit = 30;
            }
            this.startDate = new Date();
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();

            //prüfen ob es ab jetzt ist....
            Pattern pattern = Pattern.compile(ConstantsRegex.FOR);
            Matcher matcher = pattern.matcher(message);

            //Start erst mit anfang der nächsten Zeiteinheit
            //z.b. Ich bin nächste Woche nicht da.
            if (!matcher.find() && amount == 1) {

                if (numberOfDaysPerUnit == 7) {
                    int offsetInDays = 7 - startCalendar.get(Calendar.DAY_OF_WEEK) + 2;
                    startCalendar.add(Calendar.DATE, offsetInDays);
                    endCalendar.add(Calendar.DATE, offsetInDays + amount * 7 - 1);
                    this.startDate = startCalendar.getTime();
                    this.endDate = endCalendar.getTime();
                } else if (numberOfDaysPerUnit == 30) {
                    startCalendar.add(Calendar.MONTH, 1);
                    startCalendar.set(Calendar.DAY_OF_MONTH, 1);
                    this.startDate = startCalendar.getTime();
                    endCalendar.setTime(this.startDate);
                    endCalendar.add(Calendar.MONTH, amount);
                    endCalendar.add(Calendar.DATE, -1);
                    this.endDate = endCalendar.getTime();
                } else {
                    endCalendar.add(Calendar.DATE, amount);
                    this.endDate = endCalendar.getTime();
                }

            } else {
                endCalendar.add(Calendar.DATE, amount*numberOfDaysPerUnit);
                this.endDate = endCalendar.getTime();
            }


        } catch (Exception e){
            e.printStackTrace();
        }

        dates.add(this.startDate);
        dates.add(this.endDate);

        return dates;
    }

    private  ArrayList<Date> getDatesForNextDayOrWeekend(String message){
        ArrayList<Date> dates = new ArrayList<Date>();
        Pattern pattern = Pattern.compile(ConstantsRegex.ANY_DAY);
        Matcher matcher = pattern.matcher(message);
        Pattern startPattern = Pattern.compile(ConstantsRegex.THIS);
        Matcher startMatcher = startPattern.matcher(message);

        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        int startOffset = 0;
        int durationInDays = 1;

        //single weekday stuff
        if (matcher.find()) {
            String day = matcher.group();
            int currentDayOfWeek = startCalendar.get(Calendar.DAY_OF_WEEK);
            int targetDayOfWeek = 0;
            if (day.matches(ConstantsRegex.MONDAY)){
                targetDayOfWeek = 1;
            } else if (day.matches(ConstantsRegex.TUESDAY)){
                targetDayOfWeek = 2;
            } else if (day.matches(ConstantsRegex.WEDNESDAY)){
                targetDayOfWeek = 3;
            } else if (day.matches(ConstantsRegex.THURSDAY)){
                targetDayOfWeek = 4;
            } else if (day.matches(ConstantsRegex.FRIDAY)){
                targetDayOfWeek = 5;
            } else if (day.matches(ConstantsRegex.SATURDAY)){
                targetDayOfWeek = 6;
            } else if (day.matches(ConstantsRegex.SUNDAY)){
                targetDayOfWeek = 7;
            }

            startOffset = (currentDayOfWeek < targetDayOfWeek ? (targetDayOfWeek - currentDayOfWeek) : (7 - currentDayOfWeek + targetDayOfWeek)) + 1;
        }
        //weekend stuff
        else {
            startOffset = (startMatcher.find() ? 0 : 7) + 6 - startCalendar.get(Calendar.DAY_OF_WEEK);
            durationInDays = 3;
        }

        startCalendar.add(Calendar.DATE, startOffset);
        endCalendar.add(Calendar.DATE, startOffset + durationInDays - 1);

        dates.add(startCalendar.getTime());
        dates.add(endCalendar.getTime());
        return dates;
    }

    private ArrayList<Date> getAllDatesFromMessage(String message) throws ParseException {
        DateTransform dt = new DateTransform();

        ArrayList<Date> dates = new ArrayList<Date>();
        Pattern pattern = Pattern.compile(ConstantsRegex.DATE);
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()){
            int start = matcher.start();
            int end = matcher.end();
            
            //je nach eingabe matched der monat bzw tag falsch. daher nochmal die korrekte länge prüfen
            //damit 10-10 nicht zum 10.1. wird.
            int endLong = (end + 4 < message.length() ? end + 4 : end);
            int endShort = (end + 2 < message.length() ? end + 2 : end);
            int endDdFix = (end + 1 < message.length() ? end + 1 : end);
            String dateString = matcher.group();
            if (message.substring(start, endLong).matches(ConstantsRegex.DATE_EXACT_LONG)) dateString = message.substring(start, endLong);
            else if (message.substring(start, endShort).matches(ConstantsRegex.DATE_EXACT_SHORT)) dateString = message.substring(start, endShort);
            else if (message.substring(start, endDdFix).matches(ConstantsRegex.DATE_NO_YEAR)) dateString = message.substring(start, endDdFix);
            Date date = dt.dateFromString(dateString);
            dates.add(date);
        }
        return dates;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }
}
