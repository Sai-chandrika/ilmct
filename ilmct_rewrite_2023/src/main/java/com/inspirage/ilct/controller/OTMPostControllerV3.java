package com.inspirage.ilct.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.inspirage.ilct.documents.Log;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.ShipmentTransmissionDTO;
import com.inspirage.ilct.dto.bean.AddEventBean;
import com.inspirage.ilct.dto.shipmentstatus.ShipmentStatusDto;
import com.inspirage.ilct.enums.ActionEnum;
import com.inspirage.ilct.enums.MessageTypeEnum;
import com.inspirage.ilct.service.CommonService;
import com.inspirage.ilct.service.LoggerService;
import com.inspirage.ilct.service.OtmServiceV3;
import com.inspirage.ilct.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static com.inspirage.ilct.util.Constants.SHIPMENT_CLOSING_ERROR;
import static com.inspirage.ilct.util.Constants.TRANSMISSION_ERROR;

@RestController
@RequestMapping("/v3/otm-post/")
public class OTMPostControllerV3 {
    @Autowired
    OtmServiceV3 otmService;

    @Autowired
    CommonService commonService;
    private static final int Indent_Factor = 4;

    @Autowired
    LoggerService loggerService;

    @PostMapping(value = "/save-otm-shipment-data")
    public ResponseEntity<ApiResponse> saveOTMShipmentData(@RequestBody String transmission, HttpServletRequest request) {
        request.setAttribute("requestData", this.replaceFormat(transmission));
        ObjectMapper objectMapper = new ObjectMapper();
        ShipmentTransmissionDTO transmissionDto = null;
        try {
            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            transmissionDto = objectMapper.readValue(toJson(Utility.stylizer("plannedshipment_to_ilmct_v11.xsl", transmission)), ShipmentTransmissionDTO.class);
        } catch (Exception e) {
            loggerService.saveLog(Log.builder().actionEnum(ActionEnum.SAVING_SHIPMENT).localDateTime(LocalDateTime.now()).message(e.getMessage()).type(MessageTypeEnum.EXCEPTION).build(), request);
            e.printStackTrace();
        }
        if (transmissionDto == null) {
            loggerService.saveLog(Log.builder().actionEnum(ActionEnum.SAVING_SHIPMENT).localDateTime(LocalDateTime.now()).message(TRANSMISSION_ERROR).type(MessageTypeEnum.ERROR).build(), request);
            return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_ACCEPTABLE, "Failed"),
                    HttpStatus.EXPECTATION_FAILED);
        }
        return ResponseEntity.ok(otmService.saveShipmentXml(transmissionDto, request));
    }

    @PostMapping(value="/save-otm-shipment-status")
    public ApiResponse saveShipmentStatus(@RequestBody String transmission,HttpServletRequest request){
        request.setAttribute("requestData",this.replaceFormat(transmission));
        ObjectMapper mapper=new ObjectMapper();
        ShipmentStatusDto shipmentStatusDto=null;
        try {
            mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES,true);
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS);
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,true);
            transmission=Utility.stylizer("StylesheetToRemoveNamespaces.xsl",transmission);
            String json=toJson(transmission);
            shipmentStatusDto=mapper.readValue(json,ShipmentStatusDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (shipmentStatusDto==null){
            return new ApiResponse(HttpStatus.BAD_REQUEST,"Failed");
        }
        return new ApiResponse(HttpStatus.OK,"Sucess",otmService.saveShipmentStatus(shipmentStatusDto,request));
    }

    private String toJson(String xml) throws JSONException {
        String XML_STRING;
        XML_STRING = xml.replaceAll("\\n", "");
        JSONObject xmlJSONObj = XML.toJSONObject(XML_STRING);
        return xmlJSONObj.toString(Indent_Factor);
    }

    private String replaceFormat(String string) {
        return string.replaceAll("\\n", "").replaceAll("\\t", "").replace("\\r", "");
    }
    @PostMapping(value = "/add-documents",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse> addDocumentToShipment(@RequestParam("files") List<MultipartFile> files, @RequestParam String loadId, HttpServletRequest request, @RequestParam(required = false) String comment) throws Exception {
        ApiResponse response = otmService.addDocumentsToShipment(files, loadId, request, comment);
        if(!response.getStatus().equals(HttpStatus.OK)){
            return new ResponseEntity<>(response,response.getStatus());
        }else
            return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all-timezones")
    public ResponseEntity<ApiResponse> getAllTimeZones() {
        return ResponseEntity.ok(commonService.getAllTimeZones());
    }

    @PostMapping("/add-shipment-event")
    public ResponseEntity<ApiResponse> addEventToShipment(@RequestBody AddEventBean addEventBean, HttpServletRequest request){
        ApiResponse response = otmService.addEventToShipment(addEventBean, request);
        if (response.getStatus().equals(HttpStatus.OK)){
            return ResponseEntity.ok(response);
        }else{
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/add-to-watchlist")
    public ResponseEntity<ApiResponse> addShipmentToWatchList(@RequestParam String loadId, HttpServletRequest request) throws Exception {
        return ResponseEntity.ok(otmService.addToWatchList(loadId, request));
    }

    @GetMapping(value = "closeShipment")
    public ResponseEntity<ApiResponse> closeShipment(@RequestParam String loadId, @RequestParam String userId) {
        if (loadId != null && userId != null) {
            return ResponseEntity.ok(otmService.closeShipment(loadId, userId));
        } else {
            loggerService.saveLog(Log.builder().actionEnum(ActionEnum.CLOSING_SHIPMENT).localDateTime(LocalDateTime.now()).message(SHIPMENT_CLOSING_ERROR).type(MessageTypeEnum.ERROR).build(), userId);
            ApiResponse response = new ApiResponse(HttpStatus.BAD_REQUEST, "Invalid Request");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
