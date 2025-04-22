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
    public List<Destination> getDestinations(DestinationsQueryParams destinationsQueryParams) {

        String sql = "SELECT id, name, description, image_url, created_date, last_modified_date " +
                "FROM destinations " +
                "WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        if (destinationsQueryParams.getSearch() != null){
            sql = sql + " AND name LIKE :search";
            map.put("search", "%" + destinationsQueryParams.getSearch() + "%");
        }

        // 排序
        sql = sql + " ORDER BY " + destinationsQueryParams.getOrderBy() + " " + destinationsQueryParams.getSort();

        // 分頁
        sql = sql + " LIMIT :limit OFFSET :offset";
        map.put("limit", destinationsQueryParams.getLimit());
        map.put("offset", destinationsQueryParams.getOffset());

        List<Destination> destinationsList = namedParameterJdbcTemplate.query(sql, map, new DestinationsRowMapper());

        return destinationsList;
    }

    @Override
    public Integer countDestinations(DestinationsQueryParams destinationsQueryParams) {
        String sql = "SELECT COUNT(*) FROM destinations WHERE 1=1";

        Map<String, Object> map = new HashMap<>();

        // 查詢條件
        if (destinationsQueryParams.getSearch() != null){
            sql = sql + " AND name LIKE :search";
            map.put("search", "%" + destinationsQueryParams.getSearch() + "%");
        }

        Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

        return total;
    }
}
