package com.inspirage.ilct.documents;

import com.inspirage.ilct.dto.bean.TimeEstimationBean;
import com.inspirage.ilct.dto.otm.*;
import com.inspirage.ilct.enums.ShipmentClassification;
import com.inspirage.ilct.enums.StatusEnum;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@SuppressWarnings("unused")
@Document(collection = "shipments")
@EqualsAndHashCode(callSuper = false)
public class Shipment extends BaseDoc {
//	private OtmShipmentHeader otmShipmentHeader;
	private String loadId;
	private String simpleLoadId;
	private String loadName;
	private String mode;
	private Object loadedDistanceValue;
	private Object loadedDistanceUomCode;
	private String originId;
	private String destinationId;
	private Date startTime;
	private Date endTime;
	private String carrierId;
	private String containerNumber;

	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String startDateTimeZone;
	private String endDateTimeZone;

	private Double totalWeightValue;
	private String totalWeightUomCode;
	private Double totalVolumeValue;
	private String totalVolumeUomCode;
	private Integer cartonCount;
	private Integer itemCount;
	private Integer totalOrders;
	private Object ordersId;
	private Integer totalStops;
	private String truckType;
	private double utilizationWt;
	private double utilizationVol;
	private double utilizationEru;
	private boolean hazardousFlag = false;

	private String indicator;
	private String cnNumber;
	private String blNumber;
	private String driverId;
	private String truckId;
	private String trailerId;
	private String equipmentGroup;

	private List<OtmShipmentRefnum> shipmentRefNumList;
	public List<InvolvedParty> involvedParty;
	private Object involvedPartyQualifierId;
	private Object involvedPartyContactId;
	private Object communicationMethod;

	private List<OtmShipmentStop> shipmentStopList;
	private Date sightingDate;

	private Object specialServiceId;
	private Object completionState;
	private Object actualOccurenceTime;
	private Object plannedDuration;
	private Object actualDuration;
	private Object actualDistance;
	private Object actualWeight;
	private String actualVolume;
	private String contractNumber;
	private String utl;
	private String vesselId;
	private String voyageId;

	private Map<String, String> statuses = new HashMap<>();
	private Date insertDate;
	private Date updateDate;
	private LatLng current;
	private String deliveryLocation, pickupLocation;
	private TimeEstimationBean eta;
	private Alerts weatherAlert;
	private String hazardousValue;
//	private List<OtmRelease> otmRelease;
	private StatusEnum status;
	private int eventCount;
	private String lastEventId;
	private String currentSpeed;
	private Integer currentFuelInLtr;
	private String currentFuel;
	private String currentTemp;
	private int avgSpeed;
	private String speedUnit;
	private double SToDDirectionAngle;

//	private List<OtmShipUnit> otmShipUnit;
//	private List<OtmPackagedItem> packagedItem;
	public String globalId;
	public String deleveryOrderNo;
	public String bookingRefNo;
	private Date latestGvitDate;
	public Date closedOn;
	public String closedBy;
	public ShipmentClassification shipmentClassification;
	private String hos;
	private String wagonNo;
	private OtmSEquipment otmSEquipment;
	private List<OtmSEquipment> otmSEquipments;
	private String languageCode;
	private Boolean isChinaShipment = Boolean.FALSE;
	private String pickupCountryCode;
}
