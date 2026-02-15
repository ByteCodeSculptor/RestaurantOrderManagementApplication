package com.restaurants.demo.security.services;


import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                .available(request.getAvailable())
                .build();

        MenuItem saved = menuItemRepository.save(menuItem);

        return MenuResponse.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .price(saved.getPrice())
                .available(saved.getAvailable())
                .build();
    }

    @Override
    public MenuResponse updateMenuItem(Long id,MenuRequest request) {

        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("Menu Item Not found"));

        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setAvailable(request.getAvailable());


        MenuItem updated = menuItemRepository.save(menuItem);

        return MenuResponse.builder()
                .id(updated.getId())
                .name(updated.getName())
                .description(updated.getDescription())
                .price(updated.getPrice())
                .available(updated.getAvailable())
                .build();

    }

    @Override
    public void deleteMenuItem(Long id){
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("Menu Item Not found"));
        menuItemRepository.delete(menuItem);
    }

    @Override
    public MenuResponse updateAvailability(Long id, Boolean available){
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> new RuntimeException("Menu Item Not found"));

        menuItem.setAvailable(available);

        MenuItem updated = menuItemRepository.save(menuItem);

        return MenuResponse.builder()
                .id(updated.getId())
                .name(updated.getName())
                .description(updated.getDescription())
                .price(updated.getPrice())
                .available(updated.getAvailable())
                .build();
    }

    @Override
    public Page<MenuResponse> getMenuItems(Boolean available, int page, int size){
        Pageable pageable = PageRequest.of(page,size);

        Page<MenuItem> menuPage;

        if(available != null){
            menuPage = menuItemRepository.findByAvailable(available,pageable);
        }else{
            menuPage = menuItemRepository.findAll(pageable);
        }

        return menuPage.map(menuItem -> MenuResponse.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .build());
    }
}
