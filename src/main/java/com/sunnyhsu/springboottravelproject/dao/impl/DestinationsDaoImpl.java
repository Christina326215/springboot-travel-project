package com.sunnyhsu.springboottravelproject.dao.impl;

import com.sunnyhsu.springboottravelproject.dao.DestinationsDao;
import com.sunnyhsu.springboottravelproject.dto.DestinationsQueryParams;
import com.sunnyhsu.springboottravelproject.model.Destination;
import com.sunnyhsu.springboottravelproject.rowmapper.DestinationsRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DestinationsDaoImpl implements DestinationsDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Destination> fetchDestinations(int page, int size) {

        String sql = "SELECT name, description, image_url " +
                "FROM destinations " +
                "LIMIT 6";

        Map<String, Object> map = new HashMap<>();

        List<Destination> destinationsList = namedParameterJdbcTemplate.query(sql, map, new DestinationsRowMapper());

        System.out.println(destinationsList);

        return destinationsList;
    }

    @Override
    public Integer countTotalDestinations() {
        String sql = "SELECT COUNT(*) FROM destinations";

        Map<String, Object> map = new HashMap<>();

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;
    }
}
