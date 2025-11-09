package br.com.amorimtech.libEasy.book.controller;


import br.com.amorimtech.libEasy.book.dto.BookRequest;
import br.com.amorimtech.libEasy.book.dto.BookResponse;
import br.com.amorimtech.libEasy.book.mapper.BookMapper;
import br.com.amorimtech.libEasy.shared.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import br.com.amorimtech.libEasy.shared.dto.ApiResponse;
import br.com.amorimtech.libEasy.book.model.Book;
import br.com.amorimtech.libEasy.book.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> findAll(Pageable pageable) {
        Page<Book> bookPage = bookService.findAll(pageable);
        PageResponse<BookResponse> pageResponse = PageResponse.from(bookPage.map(BookMapper::toResponse));
        return ApiResponse.success(pageResponse, HttpStatus.OK).createResponseEntity();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<BookResponse>> findById(@PathVariable UUID id) {
        Book book = bookService.findById(id);
        return ApiResponse.success(BookMapper.toResponse(book), HttpStatus.OK).createResponseEntity();
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> searchBooks(
            @RequestParam(value = "q", required = false) String query,
            Pageable pageable
    ) {
        Page<Book> searchResults = bookService.searchBooks(query, pageable);
        
        Page<BookResponse> responseResults = searchResults.map(BookMapper::toResponse);
        
        PageResponse<BookResponse> pageResponse = PageResponse.from(responseResults);
        return ApiResponse.success(pageResponse, HttpStatus.OK).createResponseEntity();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> save(@Valid @RequestBody BookRequest bookCreateRequest) {
        Book book = bookService.create(BookMapper.toModel(bookCreateRequest));
        return ApiResponse.success(BookMapper.toResponse(book), HttpStatus.CREATED).createResponseEntity();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BookResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody BookRequest bookUpdateRequest
    ) {
        Book book = bookService.update(id, BookMapper.toModel(bookUpdateRequest));
        return ApiResponse.success(BookMapper.toResponse(book), HttpStatus.OK).createResponseEntity();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        bookService.delete(id);
        return ApiResponse.<Void>success(null, HttpStatus.NO_CONTENT).createResponseEntity();
    }
}
