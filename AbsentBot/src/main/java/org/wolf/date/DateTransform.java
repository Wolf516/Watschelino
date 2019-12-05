package org.wolf.date;

import org.wolf.regex.ConstantsRegex;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTransform {

    public DateTransform(){
        super();
    }

    public Date dateFromString(String dateString) throws ParseException {
        Date result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        dateString = dateString.replaceAll(ConstantsRegex.DATE_SEPERATOR, "-");

        //normalizing...
        String[] components = dateString.split("-");
        dateString =  (components[0].length() == 1 ? "0" + components[0] : components[0]) + "-" +
                      (components[1].length() == 1 ? "0" + components[1] : components[1]) +
                      (components.length < 3 ? "" : "-" + (
                                    components[2].length() == 2 ? "20" + components[2] : components[2]
                      ));
        //add the year.
        if (dateString.matches(ConstantsRegex.DATE_NO_YEAR)){
            if(!dateString.substring(dateString.length()-1).equals("-")) dateString = dateString + "-";
            Date today = new Date();
            Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
            //dateString = "" + currentYear + dateString;
            result = sdf.parse(dateString + currentYear.toString());

            //make sure, the date is today or in the future
            if (result.compareTo(today) < 0) {
                currentYear++;
                result = sdf.parse(dateString + currentYear.toString());
            }
        }
        else if(dateString.matches(ConstantsRegex.DATE_EXACT_LONG)){
            result = sdf.parse(dateString);
        }

        return result;
    }

}
