package com.suffolk.library_management.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.suffolk.library_management.utils.Constant.TABLE_BOOK;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TABLE_BOOK)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String author;
    private String description;
    private String coverImageName;
    private String coverImagePath;
    private String category;
    private String filePath;
    private String fileName;
    private String createdDate;
    private String modifiedDate;
    private Boolean status;
}
