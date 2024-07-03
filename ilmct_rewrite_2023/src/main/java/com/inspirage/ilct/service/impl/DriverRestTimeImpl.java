package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.documents.CountryDoc;
import com.inspirage.ilct.documents.DriverRestTimeDoc;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.request.DriverRestTimeBean;
import com.inspirage.ilct.exceptions.DuplicateRequestException;
import com.inspirage.ilct.exceptions.NullPointerException;
import com.inspirage.ilct.repo.CountryRepository;
import com.inspirage.ilct.repo.DriverRestTimeRepository;
import com.inspirage.ilct.service.DriverRestTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author chandrika.g
 * user
 * @ProjectName ilmct-backend
 * @since 11-11-2023
 */
@Service
public class DriverRestTimeImpl implements DriverRestTimeService {
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    DriverRestTimeRepository driverRestTimeRepository;

    public ApiResponse updateDriveAndRestTimings(DriverRestTimeBean timing) {
        DriverRestTimeDoc driverRestTimeDoc;
        Optional<DriverRestTimeDoc> optional = driverRestTimeRepository.findById(timing.getId());
        if (optional.isEmpty()) throw new NullPointerException("not found data given this id");
        driverRestTimeDoc = optional.get();
        driverRestTimeDoc.setCountry(optional.get().getCountry());
        if (timing.getDriveHrs() != null) {
            driverRestTimeDoc.setDriveHrs(timing.getDriveHrs());
        }
        if (timing.getDriveMins() != null) {
            driverRestTimeDoc.setDriveMins(timing.getDriveMins());
        }
        if (timing.getRestHrs() != null) {
            driverRestTimeDoc.setRestHrs(timing.getRestHrs());
        }
        if (timing.getRestMins() != null) {
            driverRestTimeDoc.setRestMins(timing.getRestMins());
        }
        driverRestTimeRepository.save(driverRestTimeDoc);
        return new ApiResponse(HttpStatus.OK.value(), "driver rest timings updated successfully", driverRestTimeDoc);
    }

    @Override
    public ApiResponse saveDriveAndRestTimings(DriverRestTimeBean timing) {
        DriverRestTimeDoc driverRestTimeDoc;
            driverRestTimeDoc = new DriverRestTimeDoc();
            Optional<DriverRestTimeDoc> country = driverRestTimeRepository.findByCountryId(timing.getCountry());
            if(country.isPresent()) {
                driverRestTimeDoc = country.get();
                driverRestTimeDoc.setCountry(country.get().getCountry());
                driverRestTimeDoc.setDriveHrs(timing.getDriveHrs());
                driverRestTimeDoc.setDriveMins(timing.getDriveMins());
                driverRestTimeDoc.setRestHrs(timing.getRestHrs());
                driverRestTimeDoc.setRestMins(timing.getRestMins());
                driverRestTimeDoc.setStatus(Boolean.TRUE);
            }else{
                driverRestTimeDoc.setCountry(countryRepository.findById(timing.getCountry()).orElseThrow(() -> new NullPointerException("country not found")));
                driverRestTimeDoc.setDriveHrs(timing.getDriveHrs());
                driverRestTimeDoc.setDriveMins(timing.getDriveMins());
                driverRestTimeDoc.setRestHrs(timing.getRestHrs());
                driverRestTimeDoc.setRestMins(timing.getRestMins());
                driverRestTimeDoc.setStatus(Boolean.TRUE);
            }
            driverRestTimeRepository.save(driverRestTimeDoc);
            return new ApiResponse(HttpStatus.OK.value(), "driver rest timings saved successfully", driverRestTimeDoc);
        }



    @Override
    public ApiResponse findAllDriverAndRestTimings() {
        List<DriverRestTimeDoc> l = driverRestTimeRepository.findAllByStatus(true);
        if (l == null || l.size() == 0) {
            return new ApiResponse(HttpStatus.NO_CONTENT, "No Data Found");
        }
        return new ApiResponse(HttpStatus.OK, "Success", l);
    }

    @Override
    public ApiResponse deleteAllDriveAndRestTimings() {
        List<DriverRestTimeDoc> l = driverRestTimeRepository.findAll();
        for (DriverRestTimeDoc doc : l) {
            doc.setStatus(Boolean.FALSE);
            driverRestTimeRepository.save(doc);
        }
        return new ApiResponse(HttpStatus.NO_CONTENT, "Success");
    }
    @Override
    public ApiResponse deleteDriveAndRestTimingById(String id) {
        DriverRestTimeDoc timing = driverRestTimeRepository.findByIdAndStatus(id, true);
        if (timing == null) return new ApiResponse(HttpStatus.NO_CONTENT, "No Data Found");
        timing.setStatus(Boolean.FALSE);
        driverRestTimeRepository.save(timing);
        return new ApiResponse(HttpStatus.NO_CONTENT, "Success");
    }

    @Override
    public ApiResponse findDriverCountries() {
        List<CountryDoc> countries = countryRepository.findAll();
        return new ApiResponse(HttpStatus.OK, "Success", countries);
    }
}
