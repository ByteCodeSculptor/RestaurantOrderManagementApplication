package com.restaurants.demo.security.services;

import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;

public interface MenuService {

    MenuResponse createMenuItem(MenuRequest request);
}
