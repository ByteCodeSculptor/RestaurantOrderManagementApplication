package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.util.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

/*
    MenuService defines the business logic for managing menu items, including creating, updating, deleting, and fetching menu items.
    It also includes functionality for updating the availability of menu items, which is crucial for order management.
 */
public interface MenuService {

    MenuResponse createMenuItem(MenuRequest request);

    MenuResponse updateMenuItem(Long id,MenuRequest request);

    void deleteMenuItem(Long id);

    MenuResponse updateAvailability(Long id, Boolean available);

    Page<MenuResponse> getMenuItems(Pageable pageable, Boolean available);
}
