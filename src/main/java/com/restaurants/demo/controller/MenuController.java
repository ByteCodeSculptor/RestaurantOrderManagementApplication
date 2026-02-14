package com.restaurants.demo.controller;

import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.security.services.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MenuResponse> createMenuItem(
            @Valid @RequestBody MenuRequest request) {

        return ResponseEntity.ok(menuService.createMenuItem(request));
    }
}
