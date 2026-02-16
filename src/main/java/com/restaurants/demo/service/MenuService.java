package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.exception.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

/*
    MenuService defines the business logic for managing menu items, including creating, updating, deleting, and fetching menu items.
    It also includes functionality for updating the availability of menu items, which is crucial for order management.
 */
public interface MenuService {

    ResponseEntity<ApiResponse<MenuResponse>> createMenuItem(MenuRequest request);

    ResponseEntity<ApiResponse<MenuResponse>> updateMenuItem(Long id,MenuRequest request);

    ResponseEntity<ApiResponse<MenuResponse>> deleteMenuItem(Long id);

    ResponseEntity<ApiResponse<MenuResponse>> updateAvailability(Long id, Boolean available);

    ResponseEntity<ApiResponse<Page<MenuResponse>>> getMenuItems(Boolean available, int page, int size);
}
