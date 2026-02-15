package com.restaurants.demo.controller;

import com.restaurants.demo.dto.request.AvailabilityRequest;
import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.exception.ApiResponse;
import com.restaurants.demo.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<MenuResponse>> createMenuItem(@Valid @RequestBody MenuRequest request) {
        MenuResponse response = menuService.createMenuItem(request);
        ApiResponse<MenuResponse> apiResponse = ApiResponse.<MenuResponse>builder()
                .status(HttpStatus.CREATED.value())
                .message("Menu item created successfully")
                .data(response)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuRequest request){

        MenuResponse response = menuService.updateMenuItem(id,request);
        ApiResponse<MenuResponse> apiResponse = ApiResponse.<MenuResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Menu item updated successfully")
                .data(response)
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id){
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/availability")
    public ResponseEntity<MenuResponse> updateAvailability(@PathVariable Long id, @Valid @RequestBody AvailabilityRequest request){
        return ResponseEntity.ok(menuService.updateAvailability(id,request.getAvailable()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping
    public ResponseEntity<Page<MenuResponse>> getMenuItems(
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
            ){
        return ResponseEntity.ok(menuService.getMenuItems(available,page,size));
    }
}
