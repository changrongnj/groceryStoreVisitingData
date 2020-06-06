package controller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.*;
import model.data.Constant;
import model.utility.*;
import view.CsvGenerator;


public class PilotSim {
    // PilotSim can be our controller class that contains Model and View instances
    // This class will be in charge of putting everything together for a month of data.
    static final int MONTH = 5;  // Todo: Figure out how to incorporate the month parameter.
    static final int DAYS_IN_MONTH = 31;
    private static CsvGenerator csvGenerator = new CsvGenerator();
    private static Util util = new Util();

    private static final double[] VISIT_DIST = new double[] {0.03, 0.05, 0.06, 0.08, 0.09, 0.11,
        0.095, 0.08, 0.07, 0.085, 0.12, 0.095, 0.035};  // Todo: Pass this as parameter.



    // (Andy): I moved the main class to PilotSim since it seemed more appropriate to run the
    // simulations here. I am currently defining the Day class as a concrete class, but I plan on
    // making it an abstract class with sub-classes later (if that still makes sense) to allow more
    // flexibilities dealing with holiday-dependent changes later. However, I think just having the
    // concept for now might help us visualize more options for the final product and help us
    // decide if one way might make more design sense than the other.
    public static void main(String[] args) {
        // List<Visit> visits = new ArrayList<>();
        List<Day> days = new ArrayList<>();  // List of days of the month.
        Constant constant = new Constant();
        Integer DAILY_VOLUME = 2000;

        for(int i=1; i <= DAYS_IN_MONTH; i++) {

            Day newDay = new Day();
            // initialize a LocalDate instance here for determination
            LocalDate ld = LocalDate.of(2016, 5, i);

            // Fixed so that getHolidayInfo accepts LocalDate instead.
            HolidayType holidayType = HolidayDeterminer.getHolidayInfo(LocalDate.of(2016, 5, i));
            switch (holidayType) {
                case IS_HOLIDAY:
                    DAILY_VOLUME = constant.getAmountOfCustomers().get("Holiday");
                case DAY_BEFORE_HOLIDAY:
                    DAILY_VOLUME = constant.getAmountOfCustomers().get("DayBeforeHoliday");
                case WEEK_TO_HOLIDAY:
                    switch (ld.getDayOfWeek()) {
                        case MONDAY:
                            DAILY_VOLUME = constant.getAmountOfCustomers().get("MondayBeforeHoliday");
                        case TUESDAY:
                            DAILY_VOLUME = constant.getAmountOfCustomers().get("TuesdayBeforeHoliday");
                        case WEDNESDAY:
                            DAILY_VOLUME = constant.getAmountOfCustomers().get("WednesdayBeforeHoliday");
                        case THURSDAY:
                            DAILY_VOLUME = constant.getAmountOfCustomers().get("ThursdayBeforeHoliday");
                        case FRIDAY:
                            DAILY_VOLUME = constant.getAmountOfCustomers().get("FridayBeforeHoliday");
                        case SATURDAY:
                            DAILY_VOLUME = constant.getAmountOfCustomers().get("SaturdayBeforeHoliday");
                        case SUNDAY:
                            DAILY_VOLUME = constant.getAmountOfCustomers().get("SundayBeforeHoliday");
                    }
                case NON_HOLIDAY:
                    switch (ld.getDayOfWeek()) {
                    case MONDAY:
                        DAILY_VOLUME = constant.getAmountOfCustomers().get("Monday");
                    case TUESDAY:
                        DAILY_VOLUME = constant.getAmountOfCustomers().get("Tuesday");
                    case WEDNESDAY:
                        DAILY_VOLUME = constant.getAmountOfCustomers().get("Wednesday");
                    case THURSDAY:
                        DAILY_VOLUME = constant.getAmountOfCustomers().get("Thursday");
                    case FRIDAY:
                        DAILY_VOLUME = constant.getAmountOfCustomers().get("Friday");
                    case SATURDAY:
                        // TODO: I use the weather at 12am to represent the whole day's weather(there could be some better idea)
                        if(util.findWeather(ld.atTime(12, 0)).getWasNiceWeather()) {
                            DAILY_VOLUME = constant.getAmountOfCustomers().get("NiceWeekend");
                        } else {
                            DAILY_VOLUME = constant.getAmountOfCustomers().get("Saturday");
                        }
                    case SUNDAY:
                        if(util.findWeather(ld.atTime(12, 0)).getWasNiceWeather()) {
                            DAILY_VOLUME = constant.getAmountOfCustomers().get("NiceWeekend");
                        } else {
                            DAILY_VOLUME = constant.getAmountOfCustomers().get("Sunday");
                        }
                }
            }

            for(int j=0; j < DAILY_VOLUME; j++) {

                LocalDateTime ldt = null;
                double[] durationDist = null;

                switch (ld.getDayOfWeek()) {
                    case MONDAY:
                        ldt = RandomGenerator.generateEntryData(i, constant.getEntryTimeDist().get("Monday"));
                        if(ldt.getHour() < 10) {
                            durationDist = constant.getDurationTimeDist().get("6-9").get("Monday");
                        } else if(ldt.getHour() < 12) {
                            durationDist = constant.getDurationTimeDist().get("10-11").get("Monday");
                        } else if(ldt.getHour() < 13) {
                            durationDist = constant.getDurationTimeDist().get("12").get("Monday");
                        } else if(ldt.getHour() < 17) {
                            durationDist = constant.getDurationTimeDist().get("13-16").get("Monday");
                        } else if(ldt.getHour() < 19) {
                            durationDist = constant.getDurationTimeDist().get("17-18").get("Monday");
                        } else {
                            durationDist = constant.getDurationTimeDist().get("19-20").get("Monday");
                        }
                    case TUESDAY:
                        ldt = RandomGenerator.generateEntryData(i, constant.getEntryTimeDist().get("Tuesday"));
                        if(ldt.getHour() < 10) {
                            durationDist = constant.getDurationTimeDist().get("6-9").get("Tuesday");
                        } else if(ldt.getHour() < 12) {
                            durationDist = constant.getDurationTimeDist().get("10-11").get("Tuesday");
                        } else if(ldt.getHour() < 13) {
                            durationDist = constant.getDurationTimeDist().get("12").get("Tuesday");
                        } else if(ldt.getHour() < 17) {
                            durationDist = constant.getDurationTimeDist().get("13-16").get("Tuesday");
                        } else if(ldt.getHour() < 19) {
                            durationDist = constant.getDurationTimeDist().get("17-18").get("Tuesday");
                        } else {
                            durationDist = constant.getDurationTimeDist().get("19-20").get("Tuesday");
                        }
                    case WEDNESDAY:
                        ldt = RandomGenerator.generateEntryData(i, constant.getEntryTimeDist().get("Wednesday"));
                        if(ldt.getHour() < 10) {
                            durationDist = constant.getDurationTimeDist().get("6-9").get("Wednesday");
                        } else if(ldt.getHour() < 12) {
                            durationDist = constant.getDurationTimeDist().get("10-11").get("Wednesday");
                        } else if(ldt.getHour() < 13) {
                            durationDist = constant.getDurationTimeDist().get("12").get("Wednesday");
                        } else if(ldt.getHour() < 17) {
                            durationDist = constant.getDurationTimeDist().get("13-16").get("Wednesday");
                        } else if(ldt.getHour() < 19) {
                            durationDist = constant.getDurationTimeDist().get("17-18").get("Wednesday");
                        } else {
                            durationDist = constant.getDurationTimeDist().get("19-20").get("Wednesday");
                        }
                    case THURSDAY:
                        ldt = RandomGenerator.generateEntryData(i, constant.getEntryTimeDist().get("Thursday"));
                        if(ldt.getHour() < 10) {
                            durationDist = constant.getDurationTimeDist().get("6-9").get("Thursday");
                        } else if(ldt.getHour() < 12) {
                            durationDist = constant.getDurationTimeDist().get("10-11").get("Thursday");
                        } else if(ldt.getHour() < 13) {
                            durationDist = constant.getDurationTimeDist().get("12").get("Thursday");
                        } else if(ldt.getHour() < 17) {
                            durationDist = constant.getDurationTimeDist().get("13-16").get("Thursday");
                        } else if(ldt.getHour() < 19) {
                            durationDist = constant.getDurationTimeDist().get("17-18").get("Thursday");
                        } else {
                            durationDist = constant.getDurationTimeDist().get("19-20").get("Thursday");
                        }
                    case FRIDAY:
                        ldt = RandomGenerator.generateEntryData(i, constant.getEntryTimeDist().get("Friday"));
                        if(ldt.getHour() < 10) {
                            durationDist = constant.getDurationTimeDist().get("6-9").get("Friday");
                        } else if(ldt.getHour() < 12) {
                            durationDist = constant.getDurationTimeDist().get("10-11").get("Friday");
                        } else if(ldt.getHour() < 13) {
                            durationDist = constant.getDurationTimeDist().get("12").get("Friday");
                        } else if(ldt.getHour() < 17) {
                            durationDist = constant.getDurationTimeDist().get("13-16").get("Friday");
                        } else if(ldt.getHour() < 19) {
                            durationDist = constant.getDurationTimeDist().get("17-18").get("Friday");
                        } else {
                            durationDist = constant.getDurationTimeDist().get("19-20").get("Friday");
                        }
                    case SATURDAY:
                        ldt = RandomGenerator.generateEntryData(i, constant.getEntryTimeDist().get("Saturday"));
                        if(ldt.getHour() < 10) {
                            durationDist = constant.getDurationTimeDist().get("6-9").get("Saturday");
                        } else if(ldt.getHour() < 12) {
                            durationDist = constant.getDurationTimeDist().get("10-11").get("Saturday");
                        } else if(ldt.getHour() < 13) {
                            durationDist = constant.getDurationTimeDist().get("12").get("Saturday");
                        } else if(ldt.getHour() < 17) {
                            durationDist = constant.getDurationTimeDist().get("13-16").get("Saturday");
                        } else if(ldt.getHour() < 19) {
                            durationDist = constant.getDurationTimeDist().get("17-18").get("Saturday");
                        } else {
                            durationDist = constant.getDurationTimeDist().get("19-20").get("Saturday");
                        }
                        if(util.findWeather(ldt).getWasNiceWeather()) {
                            ldt = RandomGenerator.generateEntryData(i, constant.getEntryTimeDist().get("NiceSaturday"));
                            if(ldt.getHour() < 10) {
                                durationDist = constant.getDurationTimeDist().get("6-9").get("NiceSaturday");
                            } else if(ldt.getHour() < 12) {
                                durationDist = constant.getDurationTimeDist().get("10-11").get("NiceSaturday");
                            } else if(ldt.getHour() < 13) {
                                durationDist = constant.getDurationTimeDist().get("12").get("NiceSaturday");
                            } else if(ldt.getHour() < 17) {
                                durationDist = constant.getDurationTimeDist().get("13-16").get("NiceSaturday");
                            } else if(ldt.getHour() < 19) {
                                durationDist = constant.getDurationTimeDist().get("17-18").get("NiceSaturday");
                            } else {
                                durationDist = constant.getDurationTimeDist().get("19-20").get("NiceSaturday");
                            }
                        }
                    case SUNDAY:
                        ldt = RandomGenerator.generateEntryData(i, constant.getEntryTimeDist().get("Sunday"));
                        if(ldt.getHour() < 10) {
                            durationDist = constant.getDurationTimeDist().get("6-9").get("Sunday");
                        } else if(ldt.getHour() < 12) {
                            durationDist = constant.getDurationTimeDist().get("10-11").get("Sunday");
                        } else if(ldt.getHour() < 13) {
                            durationDist = constant.getDurationTimeDist().get("12").get("Sunday");
                        } else if(ldt.getHour() < 17) {
                            durationDist = constant.getDurationTimeDist().get("13-16").get("Sunday");
                        } else if(ldt.getHour() < 19) {
                            durationDist = constant.getDurationTimeDist().get("17-18").get("Sunday");
                        } else {
                            durationDist = constant.getDurationTimeDist().get("19-20").get("Sunday");
                        }
                        if(util.findWeather(ldt).getWasNiceWeather()) {
                            ldt = RandomGenerator.generateEntryData(i, constant.getEntryTimeDist().get("NiceSunday"));
                            if(ldt.getHour() < 10) {
                                durationDist = constant.getDurationTimeDist().get("6-9").get("NiceSunday");
                            } else if(ldt.getHour() < 12) {
                                durationDist = constant.getDurationTimeDist().get("10-11").get("NiceSunday");
                            } else if(ldt.getHour() < 13) {
                                durationDist = constant.getDurationTimeDist().get("12").get("NiceSunday");
                            } else if(ldt.getHour() < 17) {
                                durationDist = constant.getDurationTimeDist().get("13-16").get("NiceSunday");
                            } else if(ldt.getHour() < 19) {
                                durationDist = constant.getDurationTimeDist().get("17-18").get("NiceSunday");
                            } else {
                                durationDist = constant.getDurationTimeDist().get("19-20").get("NiceSunday");
                            }
                        }
                }

                Weather weather = util.findWeather(ldt);

                //incorporate weather into datetime class
                DateTime dateTime = new DateTime(ldt, weather, HolidayDeterminer.getHolidayInfo(ldt.toLocalDate()));
                Visit visit = new Visit();
                visit.setVisitID(String.valueOf((i-1)*DAILY_VOLUME + j));
                visit.setEntryTime(dateTime);

                // generate the corresponding duration time(Minutes).
                int totalMinutes = RandomGenerator.generateDuration(durationDist);
                visit.setTotalTime(totalMinutes);

                // generate the corresponding leave time.
                LocalDateTime leaveTime = ldt.plusMinutes(totalMinutes);
                DateTime leaveDateTime = new DateTime(leaveTime, util.findWeather(leaveTime),
                    HolidayDeterminer.getHolidayInfo(ldt.toLocalDate()));
                visit.setLeaveTime(leaveDateTime);

                newDay.addVisit(visit);
            }
            days.add(newDay);
        }

        // sort Visit based on entryTime

        for (Day day : days) {
            Collections.sort(day.getVisits(), new Comparator<Visit>() {
                @Override
                public int compare(Visit v1, Visit v2) {
                    return v1.getEntryTime().getLocalDateTime().compareTo(v2.getEntryTime().getLocalDateTime());
                }
            });
        }

        // generate the csv file
        csvGenerator.writeToCSV(days);

        // simple test
        /*for(Visit v: visits) {
            System.out.println(v.getEntryTime().getLocalDateTime().toString());
            System.out.println(v.getEntryTime().getWeather().getAverageTemperature());
        }*/
    }
}
