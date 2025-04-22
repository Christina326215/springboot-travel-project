package com.sunnyhsu.springboottravelproject.service.impl;

import com.sunnyhsu.springboottravelproject.dao.DestinationsDao;
import com.sunnyhsu.springboottravelproject.dto.DestinationsQueryParams;
import com.sunnyhsu.springboottravelproject.model.Destination;
import com.sunnyhsu.springboottravelproject.service.DestinationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DestinationsServiceImpl implements DestinationsService {

    @Autowired
    private DestinationsDao destinationsDao;

    @Override
    public List<Destination> getDestinations(DestinationsQueryParams destinationsQueryParams) {
        return destinationsDao.getDestinations(destinationsQueryParams);
    }

    @Override
    public Integer countDestinations(DestinationsQueryParams destinationsQueryParams) {
        return destinationsDao.countDestinations(destinationsQueryParams);
    }
}
