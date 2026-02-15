package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import org.springframework.data.domain.Page;

public interface MenuService {

    MenuResponse createMenuItem(MenuRequest request);

    MenuResponse updateMenuItem(Long id,MenuRequest request);

    void deleteMenuItem(Long id);

    MenuResponse updateAvailability(Long id, Boolean available);

    Page<MenuResponse> getMenuItems(Boolean available, int page, int size);
}
