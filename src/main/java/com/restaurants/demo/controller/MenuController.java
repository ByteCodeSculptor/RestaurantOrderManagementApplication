package com.restaurants.demo.controller;

import com.restaurants.demo.dto.request.AvailabilityRequest;
import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.exception.ApiResponse;
import com.restaurants.demo.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /*
        Admin-only endpoints for create items.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping 
    public ResponseEntity<ApiResponse<MenuResponse>> createMenuItem(@Valid @RequestBody MenuRequest request) {
        return menuService.createMenuItem(request);
    }

    /*
        Admin-only endpoints for update and delete items.
    */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuRequest request){
        return menuService.updateMenuItem(id, request);
    }

    /*
        Admin-only endpoint for deleting menu items.
    */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuResponse>> deleteMenuItem(@PathVariable Long id){
        return menuService.deleteMenuItem(id);
    }


    /*
        Admin-only endpoint for updating availability of menu items.
    */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/availability")
    public ResponseEntity<ApiResponse<MenuResponse>> updateAvailability(@PathVariable Long id, @Valid @RequestBody AvailabilityRequest request){
        return menuService.updateAvailability(id, request.getAvailable());
    }

    /*
        Endpoint for fetching menu items with optional filtering by availability. Accessible to both admin and staff.
    */
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<MenuResponse>>> getMenuItems(
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return  menuService.getMenuItems(available, page, size);
    }
}
