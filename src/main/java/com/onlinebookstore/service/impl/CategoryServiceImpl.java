package com.onlinebookstore.service.impl;

import com.onlinebookstore.dto.category.CategoryDto;
import com.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.onlinebookstore.mapper.CategoryMapper;
import com.onlinebookstore.model.Category;
import com.onlinebookstore.repository.category.CategoryRepository;
import com.onlinebookstore.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a category by id: " + id)
        );
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto save(CreateCategoryRequestDto requestDto) {
        Category category = categoryMapper.toCategoryEntity(requestDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto requestDto) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't update a category by id: " + id)
        );
        categoryMapper.toCategoryEntity(requestDto, category);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
}
