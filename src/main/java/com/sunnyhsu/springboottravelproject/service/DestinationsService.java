package com.sunnyhsu.springboottravelproject.service;

import com.sunnyhsu.springboottravelproject.dto.DestinationsQueryParams;
import com.sunnyhsu.springboottravelproject.model.Destination;

import java.util.List;

public interface DestinationsService {

    List<Destination> fetchDestinations(int page, int size);

    Integer countTotalDestinations();
}
