package com.inspirage.ilct.documents;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "OrderV2")
public class OrderV2Doc {
    @Indexed
    private String orderId;
    private String globalId;
    private String bn;
    private String shipFromLocationId;
    private String shipToLocationId;
    private String ttUrl;
    private List<String> shipmentId = new ArrayList<>();
}
