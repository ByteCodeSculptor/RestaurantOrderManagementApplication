package com.restaurants.demo.controller;

import com.restaurants.demo.dto.request.AvailabilityRequest;
import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.security.services.MenuService;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<MenuResponse> createMenuItem(@Valid @RequestBody MenuRequest request) {
        return ResponseEntity.ok(menuService.createMenuItem(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MenuResponse> updateMenuItem(@PathVariable Long id, @Valid @RequestBody MenuRequest request){
        return ResponseEntity.ok(menuService.updateMenuItem(id,request));
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
