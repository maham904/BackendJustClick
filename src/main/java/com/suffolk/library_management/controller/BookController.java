package com.suffolk.library_management.controller;

import com.suffolk.library_management.entity.Book;
import com.suffolk.library_management.model.BookResponse;
import com.suffolk.library_management.model.CategoryResponse;
import com.suffolk.library_management.response.ApiResponse;
import com.suffolk.library_management.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@CrossOrigin // For Web
@RequiredArgsConstructor
public class BookController extends AbstractRestController {
    private final BookService bookService;

    @PostMapping(path = "/create/book", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})  // SignUp
    public ResponseEntity<ApiResponse<Book>> post(
            @RequestPart("cover") Optional<MultipartFile> coverFile,
            @RequestPart("file") Optional<MultipartFile> pdfFile,
            @ModelAttribute Book request
    ) {
        MultipartFile cover = null, file = null;
        if (coverFile.isPresent()) {
            cover = coverFile.get();
        }
        if (pdfFile.isPresent()) {
            file = pdfFile.get();
        }
        return ResponseEntity.ok(bookService.postBook(request, cover, file));
    }

    @GetMapping("/book/category") // logIn
    public ResponseEntity<ApiResponse<CategoryResponse>> category() {
        return ResponseEntity.ok(bookService.category());
    }

    @GetMapping("/book")  // SignUp
    public ResponseEntity<ApiResponse<BookResponse>> get(
            @RequestParam("genre") Optional<String> genre
    ) {
        String genreValue = "";
        if (genre.isPresent()) {
            genreValue = genre.get();
        }
        return ResponseEntity.ok(bookService.getBookDetails(genreValue));
    }

    @GetMapping("/book/cover/{fileName}")
    public ResponseEntity<?> downloadCoverImageFromFileSystem(@PathVariable String fileName) throws IOException {
        byte[] imageData = bookService.downloadCoverImageFromFileSystem(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);

    }

    @GetMapping("/book/file/{fileName}")
    public ResponseEntity<?> downloadBookFromFileSystem(@PathVariable String fileName) throws IOException {
        byte[] imageData = bookService.downloadBookFromFileSystem(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);

    }




}
