package com.suffolk.library_management.model;

import com.suffolk.library_management.entity.Book;
import com.suffolk.library_management.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private ArrayList<Book> books;
}
