package com.restaurants.demo.security.services;


import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository menuItemRepository;

    @Override
    public MenuResponse createMenuItem(MenuRequest request) {

        MenuItem menuItem = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .available(request.isAvailable())
                .build();

        MenuItem saved = menuItemRepository.save(menuItem);

        return MenuResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .price(saved.getPrice())
                .available(saved.isAvailable())
                .build();
    }
}
