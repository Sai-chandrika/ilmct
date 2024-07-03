package com.inspirage.ilct.controller;

import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.FilterBean;
import com.inspirage.ilct.exceptions.InvalidUserTokenException;
import com.inspirage.ilct.exceptions.UserNotFoundException;
import com.inspirage.ilct.service.FilterService;
import com.inspirage.ilct.util.Constants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
public class filterController {
    @Autowired
    FilterService filterService;
    @PostMapping("user/saveFilter")
    public ResponseEntity<ApiResponse> saveFilter(@Valid @RequestBody FilterBean filter, HttpServletRequest request) throws UserNotFoundException, ParseException, InvalidUserTokenException {
        return ResponseEntity.ok(filterService.saveFilter(filter, request));
    }

    @PutMapping("user/updateFilter")
    public ResponseEntity<ApiResponse> updateFilter(@Valid @RequestBody FilterBean filter, HttpServletRequest request) throws UserNotFoundException, ParseException, InvalidUserTokenException {
        return ResponseEntity.ok(filterService.editFiler(filter, request));
    }

    @GetMapping("user/getFilters")
    public ResponseEntity<ApiResponse> saveFilter(
            @RequestParam(value = "index", required = false, defaultValue = "0") int pageIndex,
            @RequestParam(value = "numberOfRecord", required = false, defaultValue = Constants.PAGE_SIZE + "") int numberOfRecord,
            HttpServletRequest request) throws UserNotFoundException, InvalidUserTokenException {
        return ResponseEntity.ok(filterService.getFilters(pageIndex, numberOfRecord, request));
    }

    @DeleteMapping("user/deleteFilter")
    public ResponseEntity<ApiResponse> deleteFilter(@RequestParam(value = "filterId") String filterId, HttpServletRequest request) throws UserNotFoundException, ParseException, InvalidUserTokenException {
        return ResponseEntity.ok(filterService.deleteFilter(filterId, request));
    }

}
