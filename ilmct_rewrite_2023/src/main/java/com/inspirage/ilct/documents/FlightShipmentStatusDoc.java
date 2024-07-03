package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.flight.Airline;
import com.inspirage.ilct.dto.flight.Airport;
import com.inspirage.ilct.dto.flight.DepartureDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @Author : Deepak Sagar Behera
 * @Created On :  17-May-2018 16:37
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "flight_shipment_status")
public class FlightShipmentStatusDoc extends BaseDoc {

    private String loadId;
    private String containerId;

    private Integer flightId;
    private List<Airline> airlines = null;
    private List<Airport> airports = null;

    private String carrierFsCode;
    private String flightNumber;
    private String departureAirportFsCode;
    private String arrivalAirportFsCode;
    private DepartureDate departureDate;
    private String equipment;
    private Double bearing;
    private Double heading;

    private LatLng location;
    private Integer speedMph;
    private Integer altitudeFt;
    private String source;
    private String date;

    private String statusCodeId;
    private String eventDescription;
}
