package com.sunnyhsu.springboottravelproject.controller;

import com.sunnyhsu.springboottravelproject.dto.DestinationsQueryParams;
import com.sunnyhsu.springboottravelproject.model.Destination;
import com.sunnyhsu.springboottravelproject.service.DestinationsService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.sunnyhsu.springboottravelproject.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Validated
@Controller
public class DestinationController {

    @Autowired
    private DestinationsService destinationsService;

    @GetMapping("/destinations")
    public String getDestinations(HttpSession session,
                                  Model model,
                                  // 查詢條件 Filtering
                                  @RequestParam(required = false) String search,

                                  // 排序 Sorting
                                  @RequestParam(defaultValue = "created_date") String orderBy,
                                  @RequestParam(defaultValue = "desc") String sort,

                                  // 分頁 Pagination
                                  @RequestParam(defaultValue = "6") @Max(1000) @Min(0) Integer limit,
                                  @RequestParam(defaultValue = "0") @Min(0) Integer offset) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }

        DestinationsQueryParams destinationsQueryParams = new DestinationsQueryParams();
        destinationsQueryParams.setSearch(search);
        destinationsQueryParams.setOrderBy(orderBy);
        destinationsQueryParams.setSort(sort);
        destinationsQueryParams.setLimit(limit);
        destinationsQueryParams.setOffset(offset);

        // 取得 Destination List
        // 預設每頁顯示 6 筆資料，顯示第一頁
        List<Destination> destinationsList = destinationsService.getDestinations(destinationsQueryParams);

        model.addAttribute("destinations", destinationsList);
        return "destinations";
    }

    @GetMapping("/more-destinations")
    public String getMoreDestinations(
            // 查詢條件 Filtering
            @RequestParam(required = false) String search,

            // 排序 Sorting
            @RequestParam(defaultValue = "created_date") String orderBy,
            @RequestParam(defaultValue = "desc") String sort,

            // 分頁 Pagination
            @RequestParam(defaultValue = "8") @Max(1000) @Min(0) Integer limit,
            @RequestParam(defaultValue = "0") @Min(0) Integer offset,

            Model model) {

        DestinationsQueryParams destinationsQueryParams = new DestinationsQueryParams();
        destinationsQueryParams.setSearch(search);
        destinationsQueryParams.setOrderBy(orderBy);
        destinationsQueryParams.setSort(sort);
        destinationsQueryParams.setLimit(limit);
        destinationsQueryParams.setOffset(offset);

        // 取得 Destination List
        List<Destination> destinationsList = destinationsService.getDestinations(destinationsQueryParams);

        // 取得 Destination 總數
        Integer total = destinationsService.countDestinations(destinationsQueryParams);
        // 計算總頁數
        int totalPages = (int) Math.ceil((double) total / limit);

        // 防呆處理：offset 不得超出資料範圍
        if (offset >= total && totalPages > 0) {
            offset = 0; // 回到第一頁
        }

        int currentPage = offset / limit;

        model.addAttribute("destinations", destinationsList);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", limit);

        return "more-destinations";
    }

}
