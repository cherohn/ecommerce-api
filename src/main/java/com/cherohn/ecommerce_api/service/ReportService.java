package com.cherohn.ecommerce_api.service;

import com.cherohn.ecommerce_api.dto.response.RevenueAggregate;
import com.cherohn.ecommerce_api.dto.response.RevenueReportResponse;
import com.cherohn.ecommerce_api.dto.response.TopProductResponse;
import com.cherohn.ecommerce_api.model.OrderStatus;
import com.cherohn.ecommerce_api.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;

    @Cacheable(value = "topProducts", key = "#limit")
    public List<TopProductResponse> getTopSellingProducts(int limit){
        Pageable pageable = PageRequest.of(0, limit);
        return orderRepository.findTopSellingProducts(pageable);
    }

    @Cacheable(value = "revenueReport", key = "#startDate + '_' +  #endDate")
    public RevenueReportResponse getRevenueReport(LocalDate startDate, LocalDate endDate){
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        RevenueAggregate aggregate = orderRepository.getRevenueAggregate(OrderStatus.DELIVERED, startDateTime, endDateTime);

        Long totalOrders = aggregate.getTotalOrders();
        BigDecimal totalRevenue = aggregate.getTotalRevenue();

        BigDecimal averageOrderValue = totalOrders > 0 ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;

        return RevenueReportResponse.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalOrders(totalOrders)
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .build();
    }
}
