package com.assessment.ecommerce.controller;

import com.assessment.ecommerce.model.InsightMetrics;
import com.assessment.ecommerce.service.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/insights")
@RequiredArgsConstructor
public class InsightsController {
    private final InsightService insightService;

    @GetMapping
    public InsightMetrics getInsights(Authentication auth) {
        return insightService.calculateMetrics(auth.getName());
    }
}
