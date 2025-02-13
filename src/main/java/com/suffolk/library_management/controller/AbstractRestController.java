package com.suffolk.library_management.controller;

import com.suffolk.library_management.entity.Book;
import com.suffolk.library_management.response.ApiResponse;
import com.suffolk.library_management.service.AuthService;
import com.suffolk.library_management.service.BookService;
import jakarta.transaction.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/v1")
public abstract class AbstractRestController {
}
