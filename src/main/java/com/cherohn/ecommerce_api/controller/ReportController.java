package com.cherohn.ecommerce_api.controller;

import com.cherohn.ecommerce_api.dto.response.RevenueReportResponse;
import com.cherohn.ecommerce_api.dto.response.TopProductResponse;
import com.cherohn.ecommerce_api.service.ReportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Relatórios")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductResponse>> topProducts(@RequestParam(defaultValue = "10") int limit){
        return ResponseEntity.ok(reportService.getTopSellingProducts(limit));
    }

    @GetMapping("/revenue")
    public ResponseEntity<RevenueReportResponse> revenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate){
        return ResponseEntity.ok(reportService.getRevenueReport(startDate, endDate));
    }
}
