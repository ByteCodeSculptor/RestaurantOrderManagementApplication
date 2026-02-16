package com.restaurants.demo.service.impl;

import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.entity.MenuItem;
import com.restaurants.demo.exception.ApiResponse;
import com.restaurants.demo.repository.MenuItemRepository;
import com.restaurants.demo.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository menuItemRepository;

    @Override
    public ResponseEntity<ApiResponse<MenuResponse>> createMenuItem(MenuRequest request) {
        try {
            MenuItem menuItem = MenuItem.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .price(request.getPrice())
                    .available(request.getAvailable())
                    .build();

            MenuItem saved = menuItemRepository.save(menuItem);
            MenuResponse savedResponse = buildMenuResponse(saved);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Menu Item Created Successfully", savedResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to created Menu Item :" + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenuItem(Long id, MenuRequest request) {
        try{
            Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(id);
            if(optionalMenuItem.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false,"MenuItem Not Found",null));
            }

            optionalMenuItem.get().setName(request.getName());
            optionalMenuItem.get().setDescription(request.getDescription());
            optionalMenuItem.get().setPrice(request.getPrice());
            optionalMenuItem.get().setAvailable(request.getAvailable());

            MenuItem updated = menuItemRepository.save(optionalMenuItem.get());
            MenuResponse updatedResponse = buildMenuResponse(updated);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"Menu Item Updated Successfully",updatedResponse));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to created Menu Item :" + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<MenuResponse>> deleteMenuItem(Long id) {
        try{
            Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(id);

            if(optionalMenuItem.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false,"MenuItem Not Found",null));
            }
            menuItemRepository.delete(optionalMenuItem.get());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true,"MenuItem Deleted Successfully",null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to Delete Menu Item :" + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<MenuResponse>> updateAvailability(Long id, Boolean available) {
        try{
            Optional<MenuItem> optionalMenuItem = menuItemRepository.findById(id);

            if(optionalMenuItem.isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false,"MenuItem Not Found",null));
            }

            optionalMenuItem.get().setAvailable(available);
            MenuItem updated = menuItemRepository.save(optionalMenuItem.get());

            MenuResponse updatedResponse =  buildMenuResponse(updated);

            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, "Menu Item Updated Successfully", updatedResponse));
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to Updated Menu Item :" + e.getMessage(), null));
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Page<MenuResponse>>> getMenuItems(Boolean available, int page, int size) {
        try{
            Pageable pageable = PageRequest.of(page, size);
            Page<MenuItem> menuPage;

            if (available != null) {
                menuPage = menuItemRepository.findByAvailable(available, pageable);
            } else {
                menuPage = menuItemRepository.findAll(pageable);
            }
            Page<MenuResponse> responsePage = menuPage.map(this::buildMenuResponse);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to Updated Menu Item", responsePage));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to Updated Menu Item :" + e.getMessage(), null));
        }
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
