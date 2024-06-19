package com.onlinebookstore.mapper;

import com.onlinebookstore.config.MapperConfig;
import com.onlinebookstore.dto.category.CategoryDto;
import com.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.onlinebookstore.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    Category toCategoryEntity(CreateCategoryRequestDto requestDto);

    void toCategoryEntity(CreateCategoryRequestDto requestDto, @MappingTarget Category category);
}
