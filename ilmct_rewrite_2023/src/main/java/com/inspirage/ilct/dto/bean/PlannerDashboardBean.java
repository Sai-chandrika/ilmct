package com.inspirage.ilct.dto.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.inspirage.ilct.documents.*;
import com.inspirage.ilct.repo.MasterTruckTypesRepository;
import com.inspirage.ilct.repo.RuleRepository;
import com.inspirage.ilct.repo.ShipmentStatusRepository;
import com.inspirage.ilct.util.Utility;
import lombok.Data;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlannerDashboardBean {
	private Long shipmentInTransitCount;
	private Long shipmentDelayedCount;
	private Long shipmentToBeCompletedCount;
	private String shipmentToBeCompletedHr;
	private Long shipmentWithLessSpeedCount;
	private String shipmentSpeedUnit;
	private Long shipmentWithLessFuelCount;
	private String shipmentFuelUnit;

	private List<VehicleUtilizationBean> vehicleUtilization;

	private PlannerDeliveryStatus onTimeDeliveryStatusByPlanner;

	public void setVehicleUtilization(List<Shipment> shipments, MasterTruckTypesRepository masterTruckTypesRepository) {
		Map<String, VehicleUtilizationBean> utilizationBeanMap = new HashMap<>();
		VehicleUtilizationBean bean = null;
		for (Shipment shipment : shipments) {
			MasterTruckType truckType;
			try {
				truckType = masterTruckTypesRepository.findByGroup(shipment.getTruckType());
			} catch (IncorrectResultSizeDataAccessException e) {
				truckType = masterTruckTypesRepository.findByIsDefault(true).get();
			}
			if (truckType == null)
				truckType = masterTruckTypesRepository.findByIsDefault(true).get();

			bean = utilizationBeanMap.containsKey(truckType.getType()) ? utilizationBeanMap.get(truckType.getType())
					: new VehicleUtilizationBean(Utility.getRandomColorCode());
			bean.setVehicleType(truckType.getType());

			bean.setUtilization((bean.getUtilization() + shipment.getUtilizationWt()) / 2);
			bean.setUtilizationPercent(Utility.roundUpVal(bean.getUtilization() * 100));
			bean.setUtilizationString(
					"<h5><b>" + bean.getVehicleType() + "</b> : " + bean.getUtilizationPercent() + "%</h5>");

			utilizationBeanMap.put(truckType.getType(), bean);
		}
		this.vehicleUtilization = new ArrayList<>(utilizationBeanMap.values());
	}

	public void setPlannerDeliveryStatus(LocalDateTime fromDate, LocalDateTime toDate, List<Shipment> shipments,
										 User user, RuleRepository ruleRepository, ShipmentStatusRepository shipmentStatusRepository) {
		Map<String, CarrierDetails> carrierDetailsMap = new HashMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

		String date, carrier, key;
		List<ShipmentStatusDoc> shipmentStatusDoc;
		Set<String> dateSet = new HashSet<>();
		Set<String> carrierSet = new HashSet<>();
		CarrierDetails carrierDetails;
		for (Shipment shipment : shipments) {
			date = Utility.convertDate(shipment.getStartTime()).format(formatter);
			carrier = shipment.getCarrierId();
			key = date + "~" + carrier;
			shipmentStatusDoc = shipmentStatusRepository.findOneByLoadId(shipment.getLoadId(),
					  PageRequest.of(0, 1, Sort.Direction.DESC, "lastUpdated"));

			carrierDetails = carrierDetailsMap.containsKey(key) ? carrierDetailsMap.get(key)
					: new CarrierDetails(date, carrier);
			carrierDetails.setTotalShipment(carrierDetails.getTotalShipment() + 1);
			if (this.isShipmentOnTime(shipment, user.getUserId(), ruleRepository,
					shipmentStatusDoc.isEmpty() ? null : shipmentStatusDoc.get(0))) {
				carrierDetails.setOnTimeDelivery(carrierDetails.getOnTimeDelivery() + 1);
			}
			dateSet.add(date);
			if (carrier != null) {
				carrierSet.add(carrier);
				carrierDetailsMap.put(key, carrierDetails);
			}
		}

		LinkedList<String> dateList = new LinkedList<>(dateSet);
		LinkedList<String> carrierList = new LinkedList<>(carrierSet);

		Collections.sort(dateList);
		Collections.sort(carrierList);

		PlannerDeliveryStatus plannerDeliveryStatus = new PlannerDeliveryStatus();
		plannerDeliveryStatus.setCarriers(carrierList);
		plannerDeliveryStatus.setDates(dateList);

		LinkedList<Double> onTimePercentageList;
		for (final String carrierId : carrierList) {
			onTimePercentageList = new LinkedList<>();
			for (final String dateStr : dateList) {

				carrierDetails = carrierDetailsMap.values().stream()
						.filter(cd -> cd.getCarrier().equals(carrierId) && cd.getDate().equals(dateStr)).findFirst()
						.orElse(null);
				if (carrierDetails != null) {
					onTimePercentageList
							.add((carrierDetails.getOnTimeDelivery() / carrierDetails.getTotalShipment()) * 100.0);
				} else {
					onTimePercentageList.add(0.0);
				}
			}
			plannerDeliveryStatus.getData().add(onTimePercentageList);
		}
		this.setOnTimeDeliveryStatusByPlanner(plannerDeliveryStatus);
	}

	private boolean isShipmentOnTime(Shipment shipment, String userId, RuleRepository ruleRepository,
			ShipmentStatusDoc shipmentStatusDoc) {
		if (shipmentStatusDoc != null && shipment.getEndTime() != null) {
			try {
				long minutes = ChronoUnit.MINUTES.between(Utility.convertDate(shipment.getEndTime()),
						Utility.convertDate(shipmentStatusDoc.getCreatedDate()));
				if (minutes > ruleRepository.findOneByUserId(userId).orElse(new RuleDoc()).getDelayMinutes()) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}

@Data
class VehicleUtilizationBean {
	private String vehicleType;
	@JsonIgnore
	private Double utilization = 0.0;
	private Double utilizationPercent = 0.0;
	private String color;
	private String utilizationString;

	public VehicleUtilizationBean(String color) {
		super();
		this.vehicleType = vehicleType;
		this.color = color;
	}
}

@Data
class CarrierDetails {
	private String date;
	private String carrier;
	private Integer totalShipment = 0;
	private Integer onTimeDelivery = 0;

	public CarrierDetails(String date, String carrier) {
		this.date = date;
		this.carrier = carrier;
	}
}

@Data
class PlannerDeliveryStatus {

	@JsonProperty("date_labels")
	private LinkedList<String> dates = new LinkedList<>();

	@JsonProperty("carriers_carriers")
	private LinkedList<String> carriers = new LinkedList<>();

	@JsonProperty("onTimeDelivery_data")
	private LinkedList<LinkedList<Double>> data = new LinkedList<>();

}
