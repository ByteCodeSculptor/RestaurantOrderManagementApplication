package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import org.springframework.data.domain.Page;

public interface MenuService {

    MenuResponse create(MenuRequest request);

    MenuResponse update(Long id, MenuRequest request);

    void delete(Long id);

    Page<MenuResponse> getAll(Boolean available, int page, int size);

    MenuResponse getById(Long id);
}
