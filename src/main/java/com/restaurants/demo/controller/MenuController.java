package com.restaurants.demo.controller;

import com.restaurants.demo.dto.request.AvailabilityRequest;
import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.AvailabilityResponse;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.util.ApiResponse;
import com.restaurants.demo.service.MenuService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
@Validated
public class MenuController {

    private final MenuService menuService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<MenuResponse>> createMenuItem(@Valid @RequestBody MenuRequest request) {
        MenuResponse response = menuService.createMenuItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response,"Menu Created Successfully"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenuItem(@PathVariable @Positive Long id, @Valid @RequestBody MenuRequest request){
        MenuResponse response = menuService.updateMenuItem(id,request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Menu Updated Successfully"));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(@PathVariable @Positive Long id){
        menuService.deleteMenuItem(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(null,"Menu Deleted Successfully"));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<AvailabilityResponse>> updateAvailability(@PathVariable @Positive Long id, @Valid @RequestBody AvailabilityRequest request){
        AvailabilityResponse response = menuService.updateAvailability(id, request.getAvailable());
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Availability Updated Successfully"));
    }


    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MenuResponse>>> getMenuItems(
            @RequestParam(required = false) Boolean available,
            Pageable pageable
    ){
        Page<MenuResponse> response =  menuService.getMenuItems(pageable, available);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Menu Items Fetched Successfully"));
    }
}
