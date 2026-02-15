package com.restaurants.demo.controller;

import com.restaurants.demo.dto.request.AvailabilityRequest;
import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.service.MenuService;
import com.restaurants.demo.util.ResponseHelper;
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
    public ResponseEntity<?> createMenuItem(@Valid @RequestBody MenuRequest request) {
        MenuResponse response = menuService.createMenuItem(request);
        return ResponseHelper.success(response, "Menu item created successfully");
    }

    /*
        Admin-only endpoints for update and delete items.
    */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuRequest request){
        MenuResponse response = menuService.updateMenuItem(id, request);
        return ResponseHelper.success(response, "Menu item updated successfully");
    }

    /*
        Admin-only endpoint for deleting menu items.
    */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuItem(@PathVariable Long id){
        menuService.deleteMenuItem(id);
        return ResponseHelper.success("Menu item deleted successfully");
    }


    /*
        Admin-only endpoint for updating availability of menu items.
    */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/availability")
    public ResponseEntity<?> updateAvailability(@PathVariable Long id, @Valid @RequestBody AvailabilityRequest request){
        MenuResponse response = menuService.updateAvailability(id, request.getAvailable());
        return ResponseHelper.success(response, "Availability updated successfully");
    }

    /*
        Endpoint for fetching menu items with optional filtering by availability. Accessible to both admin and staff.
    */
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping
    public ResponseEntity<?> getMenuItems(
            @RequestParam(required = false) Boolean available,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<MenuResponse> response = menuService.getMenuItems(available, page, size);
        return ResponseHelper.success(response, "Menu items fetched successfully");
    }
}
