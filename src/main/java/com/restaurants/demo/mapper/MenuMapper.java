package com.restaurants.demo.mapper;

import com.restaurants.demo.dto.request.MenuRequest;
import com.restaurants.demo.dto.response.MenuResponse;
import com.restaurants.demo.entity.MenuItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    MenuItem toEntity(MenuRequest request);

    MenuResponse toResponse(MenuItem entity);
}
