package com.inspirage.ilct.documents;

import com.inspirage.ilct.util.Utility;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class Units {

    public enum TemperatureUnit {

        CELSIUS("Celsius (°C)"),
        FAHRENHEIT("Fahrenheit (°F)");

        private final String name;

        TemperatureUnit(String s) {
            name = s;
        }

        public String getTemperatureDisplayUnit(String temperatureInCelsius) {
            try {
                if (!Utility.isEmpty(temperatureInCelsius)) {

                    if (this == TemperatureUnit.FAHRENHEIT) {
                        if (temperatureInCelsius.contains("C"))
                            return Utility.convertToFahrenheit(temperatureInCelsius.replaceAll("C", "").trim()) + " °F";
                        else
                            return Utility.convertToFahrenheit(temperatureInCelsius) + " °F";
                    }
                    if (temperatureInCelsius.contains("C"))
                        return temperatureInCelsius.replaceAll("C", "").trim() + " °C";
                    else
                        return temperatureInCelsius + " °C";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public String getName() {
            return this.name;
        }


    }

    public enum DistanceUnit {

        KILOMETERS("Kilometers (KM)"),
        MILES("MILES (M)"),
        METERS("Meters");

        private final String name;

        DistanceUnit(String s) {
            name = s;
        }

        public String getSpeedDisplayUnit(String speed) {
            try {
                if (!Utility.isEmpty(speed)) {
                    switch (this) {
                        case MILES:
                            if (speed.contains("KPH"))
                                return Utility.KPH_TO_MPH(Double.parseDouble(speed.replaceAll("KPH", "").trim())) + " MPH";
                            return speed;
                        case KILOMETERS:
                            if (speed.contains("MPH"))
                                return Utility.MPH_TO_KPH(Double.parseDouble(speed.replaceAll("MPH", "").trim())) + " KPH";
                            return speed;
                        case METERS:
                            if (speed.contains("KPH"))
                                return Utility.KPH_TO_MeterPerSec(Double.parseDouble(speed.replaceAll("KPH", "").trim())) + " MeeterPerSecond";
                            if (speed.contains("MPH"))
                                return Utility.MPH_TO_MeterPerSec(Double.parseDouble(speed.replaceAll("MPH", "").trim())) + " MeeterPerSecond";
                            return speed;
                        default:
                            return speed;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        public String getName() {
            return this.name;
        }

    }
}
