package com.suffolk.library_management.service;

import com.suffolk.library_management.entity.Book;
import com.suffolk.library_management.entity.Category;
import com.suffolk.library_management.model.BookResponse;
import com.suffolk.library_management.model.CategoryResponse;
import com.suffolk.library_management.repository.BookRepository;
import com.suffolk.library_management.repository.CategoryRepository;
import com.suffolk.library_management.response.ApiResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Optional;

import static com.suffolk.library_management.utils.Constant.*;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private int status;
    private String message;

    @Transactional
    public ApiResponse<Book> postBook(Book request, MultipartFile coverImg, MultipartFile pdfFile) {
        try {
            if (!request.getTitle().isEmpty() && !request.getAuthor().isEmpty() && request.getDescription() != null && request.getCategory() != null && coverImg != null && pdfFile != null) {
                Book bookData;
                String coverFileName;
                String coverFilePath;
                String bookFileName;
                String bookFilePath;
                coverFileName = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + "_" + coverImg.getOriginalFilename();
                coverFilePath = FOLDER_PATH_BOOK_COVER + "/" + coverFileName;
                bookFileName = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) + "_" + pdfFile.getOriginalFilename();
                bookFilePath = FOLDER_PATH_BOOK_FILE + "/" + coverFileName;
                String coverExtension = StringUtils.getFilenameExtension(coverImg.getOriginalFilename());
                String fileExtension = StringUtils.getFilenameExtension(pdfFile.getOriginalFilename());
                assert coverExtension != null;
                assert fileExtension != null;
                if ((coverExtension.equalsIgnoreCase("PNG") || (coverExtension.equalsIgnoreCase("JPG")) && (fileExtension.equalsIgnoreCase("PDF")))) {
                    bookData = Book.builder()
                            .title(request.getTitle())
                            .author(request.getAuthor())
                            .description(request.getDescription())
                            .category(request.getCategory())
                            .coverImageName(coverFileName)
                            .coverImagePath(coverFilePath)
                            .fileName(bookFileName)
                            .filePath(bookFilePath)
                            .createdDate(LocalDateTime.now().toString())
                            .modifiedDate(LocalDateTime.now().toString())
                            .status(true).build();
                    coverImg.transferTo(new File(coverFilePath));
                    pdfFile.transferTo(new File(bookFilePath));
                    bookRepository.save(bookData);
                    status = STATUS_CODE_ONE;
                    message = RECORD_INSERT_SUCCESS;
                } else {
                    status = STATUS_CODE_ZERO;
                    message = "un-supported file formats";
                }

            } else {
                message = NULL_REQUEST;
            }
        } catch (Exception e) {
            status = STATUS_CODE_ZERO;
            message = e.getMessage();
        }

        return ApiResponse.<Book>builder().status(status).message(message).data(null).build();
    }

    public ApiResponse<CategoryResponse> category() {
        CategoryResponse response = new CategoryResponse();
        ArrayList<Category> category = new ArrayList<>();
        status = STATUS_CODE_ZERO;
        message = "";
        try {
            category = categoryRepository.getCategories();
            if (category == null) {
                message = "No category found";
            } else {
                response.setCategory(category);
                status = 1;
                message = SUCCESS;
            }
        } catch (Exception e) {
            message = "Exception : " + e.getMessage();
        }
        return ApiResponse.<CategoryResponse>builder()
                .status(status)
                .message(message)
                .data(response)
                .build();
    }

    public ApiResponse<BookResponse> getBookDetails(String genre) {
        BookResponse response = new BookResponse();
        try {
            ArrayList<Book> bookList = new ArrayList<>();
            status = STATUS_CODE_ZERO;
            message = "";
            if (genre.isEmpty() || genre.isBlank()) {
                bookList = bookRepository.findByGenre(genre);
                status = STATUS_CODE_ONE;
                message = SUCCESS;
            } else {
                bookList = bookRepository.findBooks();
                message = SUCCESS;
            }
            response.setBooks(bookList);
        } catch (Exception e) {
            message = "Exception : " + e.getMessage();
        }

        return ApiResponse.<BookResponse>builder()
                .status(status)
                .message(message)
                .data(response)
                .build();
    }

    public byte[] downloadCoverImageFromFileSystem(String fileName) throws IOException {
        System.out.println("File Name : " + fileName);
        Optional<Book> fileData = bookRepository.findByCoverImageName(fileName);
        System.out.println("File Data : " + fileData);
        if (fileData.isPresent()) {
            String filePath = fileData.get().getFilePath();
            System.out.println("File Path : " + fileData.get().getFilePath());
            return Files.readAllBytes(new File(filePath).toPath());
        } else {
            return null;
        }
    }

    public byte[] downloadBookFromFileSystem(String fileName) throws IOException {
        System.out.println("File Name : " + fileName);
        Optional<Book> fileData = bookRepository.findByBookName(fileName);
        if (fileData.isPresent()) {
            String filePath = fileData.get().getFilePath();
            System.out.println("File Path : " + fileData.get().getFilePath());
            return Files.readAllBytes(new File(filePath).toPath());
        } else {
            return null;
        }
    }


}
