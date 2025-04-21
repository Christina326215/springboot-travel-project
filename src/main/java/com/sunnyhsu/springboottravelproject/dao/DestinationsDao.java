package com.sunnyhsu.springboottravelproject.dao;

import com.sunnyhsu.springboottravelproject.dto.DestinationsQueryParams;
import com.sunnyhsu.springboottravelproject.model.Destination;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface DestinationsDao {

    List<Destination> fetchDestinations(int page, int size);

    Integer countTotalDestinations();
}
