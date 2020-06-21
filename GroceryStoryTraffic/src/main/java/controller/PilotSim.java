package controller;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.*;
import model.data.Constant;
import model.utility.Modifier;
import model.utility.*;
import view.CsvGenerator;
import model.Visit;

/**
 * Controller class that contains a main method that instantiates all of the required objects.
 * Does not require any command line arguments and generates a csv document displaying all of the
 * highlighted visit information per row.
 */
public class PilotSim {
    static final int MONTH = 5;
    static final int DAYS_IN_MONTH = 31;
    static final int WEATHER_SAMPLE_TIME = 12;
    private static CsvGenerator csvGenerator = new CsvGenerator();
    private static Util util = new Util();

    /**
     * main method that does not require any command line arguments.
     * @param args - command line arguments; not required for this method.
     */
    public static void main(String[] args) {
        List<Day> days = new ArrayList<>();
        Constant constant = new Constant();
        int dailyVolume;
        int additionalVolume;

        for(int i = 1; i <= DAYS_IN_MONTH; i++) {

            Day newDay = new Day();
            // Initialize a new date in the target range and determine customer volume that day.
            LocalDate date = LocalDate.of(2020, MONTH, i);
            HolidayType holiday = HolidayDeterminer.getHolidayInfo(date);
            WeatherType weatherType = util.findWeather(date.atTime(WEATHER_SAMPLE_TIME, 0))
                .getWeatherType();  // Arbitrarily chosen at noontime.
            DayOfWeek dayOfWeek = date.getDayOfWeek();


            // Get baseline volume (normal days)
            dailyVolume = DistributionDeterminer.getDailyVolume(date, constant);

            // Apply holiday volume modifications.
            dailyVolume = Modifier.applyHolidayVolume(holiday, dailyVolume);
            // Apply poor weather volume modifications.
            dailyVolume = Modifier.applyPoorWeatherVolume(weatherType, dailyVolume, dayOfWeek);

            // Get baseline data (normal days) OR holiday data OR poor weather data
            for (int j = 0; j < dailyVolume; j++) {
                // Get entry information.
                LocalDateTime ldt = DistributionDeterminer.getEntryTime(i, date, constant);

                // Get visit duration distribution for the specified date/time.
                double[] durationDist = DistributionDeterminer.getDurationDistribution(
                        ldt, constant, holiday);
                Weather weather = util.findWeather(ldt);

                // add pre-fix "N" representing normal daily volume ID
                String id = String.valueOf( "N" + (i-1)*dailyVolume + j);
                // Get entry information including weather and holiday information.
                DateTime entryTime = new DateTime(ldt, weather, holiday);

                // get the visit duration in minutes.
                int totalMinutes = RandomGenerator.generateDuration(durationDist);

                // Get visit leave information.
                LocalDateTime leaveTime = ldt.plusMinutes(totalMinutes);
                DateTime leaveDateTime = new DateTime(leaveTime, util.findWeather(leaveTime),
                        HolidayDeterminer.getHolidayInfo(date));

                // Immutable creation of visit.
                Visit visit = new Visit(id, entryTime, leaveDateTime, totalMinutes);
                newDay.addVisit(visit);
            }

            // add event data (except Holiday and bad weather)
            // Todo: Apply day/week before holiday effect.
            if (holiday == HolidayType.DAY_BEFORE_HOLIDAY || holiday == HolidayType.WEEK_TO_HOLIDAY ) {
                Modifier.ResultType beforeHolidayInfo = Modifier.applyBeforeHoliday(holiday, dailyVolume);
                additionalVolume = beforeHolidayInfo.additionalVolume;
                double[] entryDist = beforeHolidayInfo.entryDist;
                double[] durationDist = beforeHolidayInfo.durationDist;
                String prefixID = "H";
                Day newVisits = AdditionalDataGenerator(i, additionalVolume, entryDist, durationDist, prefixID, holiday, date);
                // Rong: not quite sure about data type. Hope the additionalDataGenerator return a list of new visits
                // I found day class is a list of visits... please help to fix.
                newDay.mergeVisits(newVisits);
            }

            // Todo: Apply the nice weather effect.
            if (weatherType == WeatherType.IS_NICE) {
                if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                    Modifier.ResultType niceWeatherInfo = Modifier.applyNiceWeather(dailyVolume);
                    additionalVolume = niceWeatherInfo.additionalVolume;
                    double[] entryDist = niceWeatherInfo.entryDist;
                    double[] durationDist = niceWeatherInfo.durationDist;
                    String prefixID = "W";
                    Day newVisits = AdditionalDataGenerator(i, additionalVolume, entryDist, durationDist, prefixID, holiday, date);
                    // Rong: not quite sure about data type. Hope the additionalDataGenerator return a list of new visits
                    // I found day class is a list of visits... please help to fix.
                    newDay.mergeVisits(newVisits);
                }
            }


            // Todo: Apply the meal hour effect.
            // Todo: Apply the senior discount effect.


            days.add(newDay);
        }


        // Sorts all visits by entry date/time information.
        for (Day day : days) {
            day.getVisits().sort(new Comparator<Visit>() {
                @Override
                public int compare(Visit v1, Visit v2) {
                    return v1.getEntryTime().getLocalDateTime()
                        .compareTo(v2.getEntryTime().getLocalDateTime());
                }
            });
        }
        csvGenerator.writeToCSV(days);
        // Commenting out for now to test main for other functionality.
        //DayDao.cleanAllVisits();
        //DayDao.addAllVisits(days);
        //DayDao.closeClient();
    }

    /**
     * generate additional data for events
     * @param day
     * @param volume
     * @param entryDist
     * @param durationDist
     * @param prefix
     * @param holiday
     * @param date
     * @return should return a list of additional visits data, which can be directly merged to the normal data
     */
    public static Day AdditionalDataGenerator(int day, int volume, double[] entryDist, double[] durationDist,
                                          String prefix, HolidayType holiday, LocalDate date) {
        Day newVisits = new Day();
        for (int i = 0; i < volume; i++) {
            // Get entry information.
            LocalDateTime ldt = RandomGenerator.generateEntryData(day, entryDist);;

            // Get visit duration distribution for the specified date/time.
            Weather weather = util.findWeather(ldt);

            // id add pre-code representing normal data, day/week before holiday, niceWeather, mealPeak, seniorDiscount
            String id = String.valueOf(prefix + (day-1) * volume + i);
            // Get entry information including weather and holiday information.
            DateTime entryTime = new DateTime(ldt, weather, holiday);

            // get the visit duration in minutes.
            int totalMinutes = RandomGenerator.generateDuration(durationDist);

            // Get visit leave information.
            LocalDateTime leaveTime = ldt.plusMinutes(totalMinutes);
            DateTime leaveDateTime = new DateTime(leaveTime, util.findWeather(leaveTime),
                    HolidayDeterminer.getHolidayInfo(date));

            // Immutable creation of visit.
            Visit visit = new Visit(id, entryTime, leaveDateTime, totalMinutes);
            newVisits.addVisit(visit);
        }
        return newVisits;
    }


}
