package com.onlinebookstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.onlinebookstore.dto.book.BookDto;
import com.onlinebookstore.dto.book.BookDtoWithoutCategoryIds;
import com.onlinebookstore.dto.book.BookSearchParametersDto;
import com.onlinebookstore.dto.book.CreateBookRequestDto;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.model.Category;
import com.onlinebookstore.repository.book.BookRepository;
import com.onlinebookstore.repository.book.BookSpecificationBuilder;
import com.onlinebookstore.service.impl.BookServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    @Mock
    private BookRepository bookRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder specificationBuilder;
    @InjectMocks
    private BookServiceImpl bookService;
    private Book book;
    private BookDto bookDto;
    private CreateBookRequestDto requestDto;

    @BeforeEach
    void setUp() {
        book = new Book(1L);
        book.setTitle("Sample Book 1");
        book.setAuthor("Author A");
        book.setIsbn("9781234567897");
        book.setPrice(BigDecimal.valueOf(19.99));
        book.setCategories(Set.of(new Category(1L)));

        bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle("Sample Book 1");
        bookDto.setAuthor("Author A");
        bookDto.setIsbn("9781234567897");
        bookDto.setPrice(BigDecimal.valueOf(19.99));
        bookDto.setCategoryIds(List.of(1L));

        requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Sample Book 1");
        requestDto.setAuthor("Author A");
        requestDto.setIsbn("9781234567897");
        requestDto.setPrice(BigDecimal.valueOf(19.99));
        requestDto.setCategoryIds(List.of(1L));
    }

    @Test
    @DisplayName("""
            Create a new book and save it to DB
            """)
    void save_ValidCreateBookRequestDto_ReturnsValidBookDto() {
        BookDto expected = bookDto;
        when(bookMapper.toBookEntity(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toBookDto(book)).thenReturn(expected);

        BookDto actual = bookService.save(requestDto);

        assertEquals(expected, actual);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    @DisplayName("""
            Update an existing book by id in DB
            """)
    void update_BookWithIdExists_ReturnsUpdatedBookDto() {
        Book updatedBook = book;
        requestDto.setTitle("Updated title");
        requestDto.setPrice(BigDecimal.valueOf(9.99));
        updatedBook.setTitle(requestDto.getTitle());
        updatedBook.setPrice(requestDto.getPrice());
        BookDto expected = bookDto;
        expected.setTitle(updatedBook.getTitle());
        expected.setPrice(updatedBook.getPrice());
        when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(book));
        doNothing().when(bookMapper).toBookEntity(requestDto, book);
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        when(bookMapper.toBookDto(updatedBook)).thenReturn(expected);

        BookDto actual = bookService.update(requestDto, 1L);

        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).save(updatedBook);
    }

    @Test
    @DisplayName("""
            Find all books containing in DB
            """)
    void findAll_BooksPresentInDB_ReturnsListOfBookDto() {
        Page<Book> booksPage = new PageImpl<>(List.of(book));
        List<BookDto> expected = List.of(bookDto);
        when(bookRepository.findAll(any(Pageable.class))).thenReturn(booksPage);
        when(bookMapper.toBookDto(book)).thenReturn(bookDto);

        List<BookDto> actual = bookService.findAll(mock(Pageable.class));
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("""
            Find an existing book by id in DB
            """)
    void findById_BookWithIdExists_ReturnsOneBookDto() {
        BookDto expected = bookDto;
        when(bookRepository.findById(1L)).thenReturn(Optional.ofNullable(book));
        when(bookMapper.toBookDto(book)).thenReturn(expected);

        BookDto actual = bookService.findById(1L);

        assertEquals(expected, actual);
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("""
            Find non-existing book by id in DB
            """)
    void findById_BookWithIdNotExists_ThrowsException() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> bookService.findById(1L));
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("""
            Search for books according to certain parameters
            """)
    void search_ParametersMatchExistingBook_ReturnsListOfBookDto() {
        Specification<Book> bookSpecification = (root, query, criteriaBuilder)
                -> root.get("title").in("Sample Book 1");
        BookSearchParametersDto searchParametersDto = new BookSearchParametersDto(
                new String[]{"Sample Book 1"}, null, null, null, null, null);
        when(specificationBuilder.build(searchParametersDto)).thenReturn(bookSpecification);
        when(bookRepository.findAll(bookSpecification)).thenReturn(List.of(book));
        when(bookMapper.toBookDto(book)).thenReturn(bookDto);
        List<BookDto> expected = List.of(bookDto);

        List<BookDto> actual = bookService.search(searchParametersDto);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Get all books by categoryId from DB
            """)
    void getAllByCategoriesId_CategoryWithIdExists_ReturnsListOfBookDtoWithoutCategories() {
        BookDtoWithoutCategoryIds expected = new BookDtoWithoutCategoryIds();
        expected.setId(bookDto.getId());
        expected.setTitle(bookDto.getTitle());
        expected.setAuthor(bookDto.getAuthor());
        expected.setIsbn(bookDto.getIsbn());
        expected.setPrice(bookDto.getPrice());
        when(bookRepository.findAllByCategoriesId(any(Pageable.class), eq(1L)))
                .thenReturn(new PageImpl<>(List.of(book)));
        when(bookMapper.toDtoWithoutCategories(book)).thenReturn(expected);

        List<BookDtoWithoutCategoryIds> actual =
                bookService.getAllByCategoriesId(mock(Pageable.class), 1L);

        assertEquals(expected, actual.get(0));
        verify(bookRepository, times(1)).findAllByCategoriesId(any(Pageable.class), eq(1L));
    }
}
