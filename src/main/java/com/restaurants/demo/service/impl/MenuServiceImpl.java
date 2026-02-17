package com.restaurants.demo.service.impl;

import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.AvailabilityResponse;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.exception.DuplicateResourceException;
import com.restaurants.demo.exception.ResourceNotFoundException;
import com.restaurants.demo.mapper.MenuMapper;
import com.restaurants.demo.repository.MenuItemRepository;
import com.restaurants.demo.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository menuItemRepository;
    private final MenuMapper menuMapper;

    @Override
    public MenuResponse createMenuItem(MenuRequest request) {
        ensureMenuItemIsUnique(request.getName());

        MenuItem menuEntity = menuMapper.toEntity(request);

        MenuItem savedMenuItem = menuItemRepository.save(menuEntity);

        return menuMapper.toResponse(savedMenuItem);
    }

    @Override
    public MenuResponse updateMenuItem(Long id,MenuRequest request){
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Menu Item Not Found with Id: " + id));

        MenuItem menuEntity = menuMapper.toEntity(request);

        MenuItem updatedMenuItem = menuItemRepository.save(menuEntity);

        return menuMapper.toResponse(updatedMenuItem);
    }

    @Override
    public void deleteMenuItem(Long id){
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Menu Item Not Found with Id: " + id));

        menuItemRepository.delete(menuItem);
    }

    @Override
    public AvailabilityResponse updateAvailability(Long id, Boolean available){
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Menu Item Not Found with Id: " + id));

        menuItem.setAvailable(available);

        menuItemRepository.save(menuItem);

        return menuMapper.toAvailabilityResponse(menuItem);
    }

    @Override
    public Page<MenuResponse> getMenuItems(Pageable pageable, Boolean available){
        Page<MenuItem> menuItemPage;

        if(available != null){
            menuItemPage = menuItemRepository.findByAvailable(available,pageable);
        }else{
            menuItemPage = menuItemRepository.findAll(pageable);
        }

        return menuItemPage.map(menuMapper::toResponse);
    }

    private void ensureMenuItemIsUnique(String name) {

        String normalizedName = name.trim().toLowerCase();

        if (menuItemRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new DuplicateResourceException(
                    "Menu item already exists with name: " + name
            );
        }
    }

}
