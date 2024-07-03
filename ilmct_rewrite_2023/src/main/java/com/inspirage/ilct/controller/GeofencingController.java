package com.inspirage.ilct.controller;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.GeofenceBean;
import com.inspirage.ilct.service.GeofenceService;
import com.inspirage.ilct.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/v2/geo_fencing/")
public class GeofencingController {
    @Autowired
    GeofenceService geofenceService;
    @PostMapping("save_geofence_data")
    public ResponseEntity<ApiResponse> saveGeofenceData(@Valid @RequestBody GeofenceBean gfBean, HttpServletRequest request) {
        return ResponseEntity.ok(geofenceService.saveGeofenceData(request, gfBean));
    }

    @GetMapping(value = "geofence_locations")
    public ResponseEntity<ApiResponse> getLocationsInfo(
            @RequestParam(value = "searchText", required = false) String searchText,
            @RequestParam(value = "index", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "numberOfRecord", required = false, defaultValue = Constants.PAGE_SIZE
                    + "") int numberOfRecord,
            HttpServletRequest request) {
        ApiResponse response = geofenceService.getLocations(pageIndex, numberOfRecord, searchText, request);
        return ResponseEntity.ok(response);
    }
}