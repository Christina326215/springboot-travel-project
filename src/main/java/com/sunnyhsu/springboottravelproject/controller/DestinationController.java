package com.sunnyhsu.springboottravelproject.controller;

import com.sunnyhsu.springboottravelproject.dto.DestinationsQueryParams;
import com.sunnyhsu.springboottravelproject.model.Destination;
import com.sunnyhsu.springboottravelproject.service.DestinationsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.sunnyhsu.springboottravelproject.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DestinationController {

    @Autowired
    private DestinationsService destinationsService;

    @GetMapping("/destinations")
    public String getDestinations(HttpSession session,
                                  Model model,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }

        // 取得 Destination List
        // 預設每頁顯示 6 筆資料，顯示第一頁
        List<Destination> destinationsList = destinationsService.fetchDestinations(page, size);

        model.addAttribute("destinations", destinationsList);
        return "destinations";
    }

    @GetMapping("/more-destinations")
    public String getMoreDestinations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            Model model) {

        // 取得 Destination List
        List<Destination> destinationsList = destinationsService.fetchDestinations(page, size);

        // 計算總數與總頁數
        // 取得 Destination 總數
        Integer total = destinationsService.countTotalDestinations();
        int totalPages = (int) Math.ceil((double) total / size);

        model.addAttribute("destinations", destinationsList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);

        return "more-destinations";
    }

}
