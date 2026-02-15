package com.restaurants.demo.service.impl;

import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.exception.CustomException;
import com.restaurants.demo.repository.MenuItemRepository;
import com.restaurants.demo.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository menuItemRepository;

    @Override
    public MenuResponse createMenuItem(MenuRequest request) {
        try {
            MenuItem menuItem = MenuItem.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .available(request.getAvailable())
                    .build();

            MenuItem saved = menuItemRepository.save(menuItem);

            return buildMenuResponse(saved);
        } catch (Exception e) {
            throw new CustomException("Failed to create menu item", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public MenuResponse updateMenuItem(Long id, MenuRequest request) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Menu item not found with id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setAvailable(request.getAvailable());

        MenuItem updated = menuItemRepository.save(menuItem);
        return buildMenuResponse(updated);
    }

    @Override
    public void deleteMenuItem(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Menu item not found with id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        menuItemRepository.delete(menuItem);
    }

    @Override
    public MenuResponse updateAvailability(Long id, Boolean available) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new CustomException(
                        "Menu item not found with id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        menuItem.setAvailable(available);
        MenuItem updated = menuItemRepository.save(menuItem);

        return buildMenuResponse(updated);
    }

    @Override
    public Page<MenuResponse> getMenuItems(Boolean available, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MenuItem> menuPage;

        if (available != null) {
            menuPage = menuItemRepository.findByAvailable(available, pageable);
        } else {
            menuPage = menuItemRepository.findAll(pageable);
        }

        return menuPage.map(this::buildMenuResponse);
    }

    // Reusable helper method to build MenuResponse
    private MenuResponse buildMenuResponse(MenuItem menuItem) {
        return MenuResponse.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .available(menuItem.getAvailable())
                .build();
    }
}
