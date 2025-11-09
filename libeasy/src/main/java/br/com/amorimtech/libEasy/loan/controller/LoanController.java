package br.com.amorimtech.libEasy.loan.controller;

import br.com.amorimtech.libEasy.loan.dto.LoanRequest;
import br.com.amorimtech.libEasy.loan.dto.LoanResponse;
import br.com.amorimtech.libEasy.loan.mapper.LoanMapper;
import br.com.amorimtech.libEasy.shared.dto.PageResponse;
import br.com.amorimtech.libEasy.shared.dto.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import br.com.amorimtech.libEasy.shared.dto.ApiResponse;
import br.com.amorimtech.libEasy.loan.model.Loan;
import br.com.amorimtech.libEasy.loan.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loans")
public class LoanController {
    private final LoanService loanService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<LoanResponse>>> findAll(Pageable pageable, Authentication authentication) {
        UserDTO currentUser = (UserDTO) authentication.getPrincipal();
        Page<Loan> loanPage = loanService.findAllForUser(currentUser, pageable);
        PageResponse<LoanResponse> pageResponse = PageResponse.from(loanPage.map(LoanMapper::toResponse));
        return ApiResponse.success(pageResponse, HttpStatus.OK).createResponseEntity();
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LoanResponse>> findById(@PathVariable Long id, Authentication authentication) {
        UserDTO currentUser = (UserDTO) authentication.getPrincipal();
        Loan loan = loanService.findByIdForUser(id, currentUser);
        return ApiResponse.success(LoanMapper.toResponse(loan), HttpStatus.OK).createResponseEntity();
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LoanResponse>> save(@Valid @RequestBody LoanRequest loanCreateRequest, Authentication authentication) {
        UserDTO currentUser = (UserDTO) authentication.getPrincipal();
        Loan loan = loanService.createForUser(LoanMapper.toModel(loanCreateRequest), currentUser);
        return ApiResponse.success(LoanMapper.toResponse(loan), HttpStatus.CREATED).createResponseEntity();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<LoanResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody LoanRequest loanUpdateRequest
    ) {
        Loan loan = loanService.update(id, LoanMapper.toModel(loanUpdateRequest));
        return ApiResponse.success(LoanMapper.toResponse(loan), HttpStatus.OK).createResponseEntity();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        loanService.delete(id);
        return ApiResponse.<Void>success(null, HttpStatus.NO_CONTENT).createResponseEntity();
    }
}
