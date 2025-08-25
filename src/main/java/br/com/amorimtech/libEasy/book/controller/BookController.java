package br.com.amorimtech.libEasy.book.controller;


import br.com.amorimtech.libEasy.book.dto.BookCreateRequest;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BookResponse>>> findAll(Pageable pageable) {
        Page<Book> bookPage = bookService.findAll(pageable);
        PageResponse<BookResponse> pageResponse = PageResponse.from(bookPage.map(BookMapper::toResponse));
        return ApiResponse.success(pageResponse, HttpStatus.OK).createResponseEntity();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> findById(@PathVariable Long id) {
        Book book = bookService.findById(id);
        return ApiResponse.success(BookMapper.toResponse(book), HttpStatus.OK).createResponseEntity();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookResponse>> save(@Valid @RequestBody BookCreateRequest bookCreateRequest) {
        Book book = bookService.create(BookMapper.toModel(bookCreateRequest));
        return ApiResponse.success(BookMapper.toResponse(book), HttpStatus.CREATED).createResponseEntity();
    }
}
