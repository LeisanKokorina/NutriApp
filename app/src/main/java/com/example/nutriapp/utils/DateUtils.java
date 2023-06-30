package com.example.nutriapp.utils;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class DateUtils {
    public static String getDateOfBirth(Spinner spinnerDay, Spinner spinnerMonth, Spinner spinnerYear) {
        String selectedDay = spinnerDay.getSelectedItem().toString();
        String selectedMonth = spinnerMonth.getSelectedItem().toString();
        String selectedYear = spinnerYear.getSelectedItem().toString();

        return selectedDay + "-" + getMonthNumber(selectedMonth) + "-" + selectedYear;
    }

    public static String getMonthNumber(String monthName) {
        switch (monthName.toLowerCase()) {
            case "january":
                return "01";
            case "february":
                return "02";
            case "march":
                return "03";
            case "april":
                return "04";
            case "may":
                return "05";
            case "june":
                return "06";
            case "july":
                return "07";
            case "august":
                return "08";
            case "september":
                return "09";
            case "october":
                return "10";
            case "november":
                return "11";
            case "december":
                return "12";
            default:
                return "-1"; // Invalid month name
        }
    }

    public static boolean isValidDate(String date) {
        try {
            // Split the date into day, month, and year
            String[] parts = date.split("-");
            int day = Integer.parseInt(parts[0]);  //31
            int month = Integer.parseInt(parts[1]); //02
            int year = Integer.parseInt(parts[2]); //1994

            // Check if the day is valid for the given month
            boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || year % 400 == 0;
            int[] daysInMonth = {31, isLeapYear ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            int maxDays = daysInMonth[month - 1];
            if (day > maxDays) {
                return false;
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static int getSpinnerIndex(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                return i;
            }
        }
        return 0; // Default index if value not found
    }


    public static String getMonthName(String monthNumber) {
        switch (monthNumber) {
            case "01":
                return "January";
            case "02":
                return "February";
            case "03":
                return "March";
            case "04":
                return "April";
            case "05":
                return "May";
            case "06":
                return "June";
            case "07":
                return "July";
            case "08":
                return "August";
            case "09":
                return "September";
            case "10":
                return "October";
            case "11":
                return "November";
            case "12":
                return "December";
            default:
                return ""; // Empty string if month number is invalid
        }
    }
    public static void setSpinnerSelectionByValue(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        if (adapter != null) {
            int position = adapter.getPosition(value);
            spinner.setSelection(position);
        }
    }
}


    // Rest of the save


