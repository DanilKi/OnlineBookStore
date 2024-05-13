package com.onlinebookstore.service.impl;

import com.onlinebookstore.dto.BookDto;
import com.onlinebookstore.dto.CreateBookRequestDto;
import com.onlinebookstore.mapper.BookMapper;
import com.onlinebookstore.model.Book;
import com.onlinebookstore.repository.BookRepository;
import com.onlinebookstore.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toBookEntity(requestDto);
        return bookMapper.toBookDto(bookRepository.save(book));
    }

    @Override
    public BookDto update(CreateBookRequestDto requestDto, Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't update a book by id: " + id)
        );
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());
        return bookMapper.toBookDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toBookDto)
                .toList();
    }

    @Override
    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find a book by id: " + id)
        );
        return bookMapper.toBookDto(book);
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }
}
