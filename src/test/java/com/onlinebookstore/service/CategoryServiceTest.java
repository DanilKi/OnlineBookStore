package com.onlinebookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onlinebookstore.dto.category.CategoryDto;
import com.onlinebookstore.dto.category.CreateCategoryRequestDto;
import com.onlinebookstore.mapper.CategoryMapper;
import com.onlinebookstore.model.Category;
import com.onlinebookstore.repository.category.CategoryRepository;
import com.onlinebookstore.service.impl.CategoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    private Category category;
    private CategoryDto categoryDto;
    private CreateCategoryRequestDto requestDto;

    @BeforeEach
    void setUp() {
        category = new Category(1L);
        category.setName("Sample Category 1");
        categoryDto = new CategoryDto(1L, "Sample Category 1", null);
        requestDto = new CreateCategoryRequestDto("Sample Category 1", null);
    }

    @Test
    @DisplayName("""
            Find all categories containing in DB
            """)
    void findAll_CategoriesPresentInDB_ReturnsListOfCategoryDto() {
        when(categoryRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(category)));
        when(categoryMapper.toCategoryDto(category)).thenReturn(categoryDto);
        List<CategoryDto> expected = List.of(categoryDto);

        List<CategoryDto> actual = categoryService.findAll(Pageable.unpaged());

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findAll(Pageable.unpaged());
    }

    @Test
    @DisplayName("""
            Get an existing category by id from DB
            """)
    void getById_CategoryWithIdExists_ReturnsValidCategoryDto() {
        CategoryDto expected = categoryDto;
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(category));
        when(categoryMapper.toCategoryDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.getById(1L);

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("""
            Get non-existing category by id from DB
            """)
    void getById_CategoryWithIdNotExists_ThrowsException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getById(2L));
        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("""
            Create a new category and save it to DB
            """)
    void save_ValidCreateCategoryRequestDto_ReturnsValidCategoryDto() {
        CategoryDto expected = categoryDto;
        when(categoryMapper.toCategoryEntity(requestDto)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toCategoryDto(category)).thenReturn(expected);

        CategoryDto actual = categoryService.save(requestDto);

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    @DisplayName("""
            Update non-existing category by id in DB
            """)
    void update_CategoryWithIdNotExists_ThrowsException() {
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.update(2L, requestDto));
        verify(categoryRepository, times(1)).findById(anyLong());
    }

    @Test
    @DisplayName("""
            Update an existing category by id in DB
            """)
    void update_CategoryWithIdExists_ReturnsUpdatedCategoryDto() {
        Category updatedCategory = category;
        requestDto = new CreateCategoryRequestDto("Sample Category 2", null);
        updatedCategory.setName(requestDto.name());
        CategoryDto expected = new CategoryDto(1L, "Sample Category 2", null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.ofNullable(category));
        doNothing().when(categoryMapper).toCategoryEntity(requestDto, category);
        when(categoryRepository.save(updatedCategory)).thenReturn(updatedCategory);
        when(categoryMapper.toCategoryDto(updatedCategory)).thenReturn(expected);

        CategoryDto actual = categoryService.update(1L, requestDto);

        assertEquals(expected, actual);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(updatedCategory);
    }
}
