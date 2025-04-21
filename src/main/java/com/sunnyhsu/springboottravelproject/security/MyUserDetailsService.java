package com.sunnyhsu.springboottravelproject.security;

import com.sunnyhsu.springboottravelproject.dao.UserDao;
import com.sunnyhsu.springboottravelproject.model.Role;
import com.sunnyhsu.springboottravelproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 從資料庫中查詢 Member 數據
        User user = userDao.getUserByEmail(username);

        if (user == null){
            throw new UsernameNotFoundException("Member not found for: " + username);
        } else {
            String memberEmail = user.getEmail();
            String password = user.getPassword();

            // 權限
            List<Role> roleList = userDao.getRolesByMemberId(user.getUserId());
            List<GrantedAuthority> authorities = convertToAuthorities(roleList);

            // 轉換成 Spring Security 指定的 User 格式
            return new org.springframework.security.core.userdetails.User(memberEmail,password,authorities);
        }
    }

    private List<GrantedAuthority> convertToAuthorities(List<Role> roleList){
        List<GrantedAuthority> authorities = new ArrayList<>();

        for (Role role : roleList){
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        return authorities;
    }
}
