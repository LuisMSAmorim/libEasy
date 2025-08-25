package br.com.amorimtech.libEasy.book.controller;


import br.com.amorimtech.libEasy.book.dto.BookResponse;
import br.com.amorimtech.libEasy.book.mapper.BookMapper;
import br.com.amorimtech.libEasy.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import br.com.amorimtech.libEasy.shared.dto.ApiResponse;
import br.com.amorimtech.libEasy.book.model.Book;
import br.com.amorimtech.libEasy.book.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
