package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.config.TokenUtilService;
import com.inspirage.ilct.documents.FilterDoc;
import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.dto.bean.FilterBean;
import com.inspirage.ilct.dto.response.PageableResponse;
import com.inspirage.ilct.exceptions.InvalidUserTokenException;
import com.inspirage.ilct.exceptions.UserNotFoundException;
import com.inspirage.ilct.repo.FilterRepository;
import com.inspirage.ilct.repo.UserRepository;
import com.inspirage.ilct.service.FilterService;
import com.inspirage.ilct.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.util.stream.Collectors;

@Service
public class FilterServiceImpl implements FilterService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    FilterRepository filterRepository;
    @Autowired
    TokenUtilService tokenUtilService;
    @Override
    public ApiResponse saveFilter(FilterBean filterBean, HttpServletRequest request) throws UserNotFoundException, InvalidUserTokenException {
        if (ObjectUtils.isEmpty(filterBean)) {
            return new ApiResponse(HttpStatus.BAD_REQUEST, "error.filter.save");
        }
        String userId = tokenUtilService.getUserId(request);
        User user = userRepository.findOneByUserId(userId).orElse(new User());
        FilterDoc filter = new FilterDoc();
        filter.setCriteria(filterBean.getCriteria());
        filter.setName(filterBean.getName());
        filter.setUserId(user.getId());
        filterRepository.save(filter);
        return new ApiResponse(HttpStatus.OK,"filter saved successfully",mapFromFilterDoc(filter));
    }
    public FilterBean mapFromFilterDoc(FilterDoc filterDoc){
        FilterBean filterBean=new FilterBean();
        filterBean.setId(filterDoc.getId());
        filterBean.setCriteria(filterDoc.getCriteria());
        filterBean.setName(filterDoc.getName());
        filterBean.setExistingName(filterDoc.getName());
        return filterBean;
    }

    @Override
    public ApiResponse editFiler(FilterBean filterBean, jakarta.servlet.http.HttpServletRequest request) throws UserNotFoundException, InvalidUserTokenException {
        String userId = tokenUtilService.getUserId(request);
        User user = userRepository.findOneByUserId(userId).orElse(new User());

        if(ObjectUtils.isEmpty(user)) return new ApiResponse(HttpStatus.OK, "user not existed");

        FilterDoc filter = filterRepository.findOneByIdAndUserId(filterBean.getId(), user.getId());
        if (filter != null) {
            filter.setCriteria(filterBean.getCriteria());
            filter.setName(filterBean.getName());
            filterRepository.save(filter);
            return new ApiResponse(HttpStatus.OK,"filter updated successfully", filterBean);
        }
        return new ApiResponse(HttpStatus.NOT_FOUND,"filter not existed");
    }

    @Override
    public ApiResponse getFilters(int pageIndex, int numberOfRecord, jakarta.servlet.http.HttpServletRequest request) throws UserNotFoundException, InvalidUserTokenException {
        String userId = tokenUtilService.getUserId(request);
        User user = userRepository.findOneByUserId(userId).orElse(new User());

        if(ObjectUtils.isEmpty(user)) return new ApiResponse(HttpStatus.OK, "user not existed");

        return new PageableResponse.PageableResponseBuilder(HttpStatus.OK)
                .withData(filterRepository.findByUserIdOrderByCreatedDateDesc(user.getId(), Utility.pageable(pageIndex, numberOfRecord)).stream().map(this::mapFromFilterDoc).collect(Collectors.toList()))
                .withPageInfo(pageIndex, numberOfRecord, filterRepository.countByUserId(user.getId()))
                .build();
    }

    @Override
    public ApiResponse deleteFilter(String filterId, HttpServletRequest request) throws UserNotFoundException, InvalidUserTokenException {
        String userId = tokenUtilService.getUserId(request);
        User user = userRepository.findOneByUserId(userId).orElse(new User());

        if(ObjectUtils.isEmpty(user)) return new ApiResponse(HttpStatus.OK, "user not existed");

        FilterDoc filter = filterRepository.findOneByIdAndUserId(filterId, user.getId());
        if (filter != null) {
            filterRepository.delete(filter);
            return new ApiResponse(HttpStatus.OK,"filter deleted successfully");
        }
        return new ApiResponse(HttpStatus.NOT_FOUND,"filter not existed");
    }
}
