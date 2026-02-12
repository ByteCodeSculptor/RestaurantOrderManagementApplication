package com.restaurants.demo.service;

import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository repository;

    @Override
    public MenuResponse create(MenuRequest request) {

        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .available(request.getAvailable() != null ? request.getAvailable() : true)
                .build();

        return mapToResponse(repository.save(item));
    }

    @Override
    public MenuResponse update(Long id, MenuRequest request) {

        MenuItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        item.setName(request.getName());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setAvailable(request.getAvailable());

        return mapToResponse(repository.save(item));
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Page<MenuResponse> getAll(Boolean available, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        Page<MenuItem> items;

        if (available != null) {
            items = repository.findByAvailable(available, pageable);
        } else {
            items = repository.findAll(pageable);
        }

        return items.map(this::mapToResponse);
    }

    @Override
    public MenuResponse getById(Long id) {

        MenuItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        return mapToResponse(item);
    }

    private MenuResponse mapToResponse(MenuItem item) {
        return MenuResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .available(item.getAvailable())
                .build();
    }
}
