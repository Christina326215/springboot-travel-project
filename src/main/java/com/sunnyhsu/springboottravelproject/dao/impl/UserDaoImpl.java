package com.sunnyhsu.springboottravelproject.dao.impl;

import com.sunnyhsu.springboottravelproject.dao.UserDao;
import com.sunnyhsu.springboottravelproject.dto.UserRegisterRequest;
import com.sunnyhsu.springboottravelproject.model.Role;
import com.sunnyhsu.springboottravelproject.model.User;
import com.sunnyhsu.springboottravelproject.rowmapper.RoleRowMapper;
import com.sunnyhsu.springboottravelproject.rowmapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserDaoImpl implements UserDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public User getUserById(Integer userId) {
        String sql = "SELECT user_id, email, password, avatar, created_date, last_modified_date, name " +
                "FROM `user` WHERE user_id = :userId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if(userList.size() > 0){
            return userList.get(0);
        } else{
            return null;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        String sql = "SELECT user_id, email, password, avatar, created_date, last_modified_date, name " +
                "FROM `user` WHERE email = :email";

        Map<String, Object> map = new HashMap<>();
        map.put("email", email);

        List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

        if(userList.size() > 0){
            return userList.get(0);
        } else{
            return null;
        }
    }

    @Override
    public Integer createUser(UserRegisterRequest userRegisterRequest) {
        String sql = "INSERT INTO `user`(email, password, created_date, last_modified_date) " +
                "VALUES (:email, :password, :createdDate, :lastModifiedDate)";

        Map<String, Object> map = new HashMap<>();
        map.put("email", userRegisterRequest.getEmail());
        map.put("password", userRegisterRequest.getPassword());

        Date now = new Date();
        map.put("createdDate", now);
        map.put("lastModifiedDate", now);

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int userId = keyHolder.getKey().intValue();

        return userId;
    }

    @Override
    public void updateUserAvatar(Integer userId, String avatarPath) {
        String sql = "UPDATE `user` SET avatar = :avatarPath " +
                "WHERE user_id = :userId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("avatarPath", avatarPath);

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public List<Role> getRolesByMemberId(Integer memberId) {
        String sql = """
                SELECT role.role_id, role.role_name FROM role
                    JOIN member_has_role ON role.role_id = member_has_role.role_id
                    WHERE member_has_role.member_id = :memberId
                """;

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);

        List<Role> roleList = namedParameterJdbcTemplate.query(sql, map, new RoleRowMapper());

        return roleList;
    }

    @Override
    public void upsertUserForOAuth(String email, String name, String avatar) {
        User existingUser = this.getUserByEmail(email);
        Date now = new Date();

        if (existingUser == null) {
            String sql = "INSERT INTO `user` (email, password, avatar, name, created_date, last_modified_date) " +
                    "VALUES (:email, :password, :avatar, :name, :createdDate, :lastModifiedDate)";

            Map<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("password", "");
            map.put("avatar", avatar);
            map.put("name", name);
            map.put("createdDate", now);
            map.put("lastModifiedDate", now);

            namedParameterJdbcTemplate.update(sql, map);
        } else {
            String sql = "UPDATE `user` " +
                    "SET avatar = :avatar, name = :name, last_modified_date = :lastModifiedDate " +
                    "WHERE email = :email";

            Map<String, Object> map = new HashMap<>();
            map.put("email", email);
            map.put("avatar", avatar);
            map.put("name", name);
            map.put("lastModifiedDate", now);

            namedParameterJdbcTemplate.update(sql, map);
        }
    }
}
