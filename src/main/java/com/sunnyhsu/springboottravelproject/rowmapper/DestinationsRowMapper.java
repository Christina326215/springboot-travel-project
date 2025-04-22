package com.sunnyhsu.springboottravelproject.rowmapper;

import com.sunnyhsu.springboottravelproject.model.Destination;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DestinationsRowMapper implements RowMapper<Destination> {

    @Override
    public Destination mapRow(ResultSet resultSet, int i) throws SQLException {
        Destination destination = new Destination(
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getString("image_url"),
                resultSet.getTimestamp("created_date").toLocalDateTime().toLocalDate(),
                resultSet.getTimestamp("last_modified_date").toLocalDateTime().toLocalDate()
        );
//        destination.setName(resultSet.getString("name"));
//        destination.setDescription(resultSet.getString("description"));
//        destination.setImageUrl(resultSet.getString("imageUrl"));

        return destination;
    }
}
