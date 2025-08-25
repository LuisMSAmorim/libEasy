package br.com.amorimtech.libEasy.book.controller;

import br.com.amorimtech.libEasy.shared.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import br.com.amorimtech.libEasy.shared.dto.ApiResponse;
import br.com.amorimtech.libEasy.book.model.Book;
import br.com.amorimtech.libEasy.book.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Book>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Page<Book> bookPage = bookService.findAll(page, size);
        PageResponse<Book> pageResponse = PageResponse.from(bookPage);
        return ApiResponse.success(pageResponse, HttpStatus.OK).createResponseEntity();
    }
}
