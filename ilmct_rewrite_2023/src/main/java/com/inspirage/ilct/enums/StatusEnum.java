package com.inspirage.ilct.enums;

import java.util.Arrays;
import java.util.List;

public enum StatusEnum {
    NOT_STARTED, STARTED, DELAYED, DELIVERED, COMPLETED, CLOSED, ONTIME, ARRIVING_EARLY, INTRANSIT;

    public static List<String> getInTransitStatuses() {
        return Arrays.asList(StatusEnum.STARTED.name(), StatusEnum.DELAYED.name(), StatusEnum.ONTIME.name(),
                StatusEnum.ARRIVING_EARLY.name());
    }

    public static boolean isInstrasiteStatus(String shipmentStatus) {
        return getInTransitStatuses().stream().anyMatch(s -> s.equals(shipmentStatus));
    }

    public static List<StatusEnum> getCompletedStatuses() {
        return Arrays.asList(StatusEnum.CLOSED, StatusEnum.COMPLETED);
    }

    public static List<String> getCompletedStatusesAsString() {
        return Arrays.asList(StatusEnum.CLOSED.name(), StatusEnum.COMPLETED.name());
    }

    public static List<String> getInCompletedStatuses() {
        return Arrays.asList(StatusEnum.CLOSED.name(), StatusEnum.COMPLETED.name(), StatusEnum.NOT_STARTED.name());
    }

    public static List<String> getInOTMUpdateStatuses() {
        return Arrays.asList(StatusEnum.DELAYED.name(), StatusEnum.ONTIME.name(),
                StatusEnum.ARRIVING_EARLY.name());
    }

    public static List<String> getLastMileDeliveryStatus() {
        return Arrays.asList(StatusEnum.NOT_STARTED.name(), StatusEnum.DELAYED.name(), StatusEnum.ONTIME.name(),
                StatusEnum.ARRIVING_EARLY.name() , StatusEnum.COMPLETED.name() , StatusEnum.CLOSED.name() );
    }

    public static List<StatusEnum> getLastMileDeliveryStatusEnum() {
        return Arrays.asList(StatusEnum.NOT_STARTED, StatusEnum.DELAYED, StatusEnum.ONTIME,
                StatusEnum.ARRIVING_EARLY , StatusEnum.COMPLETED , StatusEnum.CLOSED );
    }


    public static List<String> getExpressStatus() {
        return Arrays.asList(StatusEnum.NOT_STARTED.name(), StatusEnum.DELAYED.name(), StatusEnum.ONTIME.name(),
                StatusEnum.ARRIVING_EARLY.name(), StatusEnum.STARTED.name());
    }
}
