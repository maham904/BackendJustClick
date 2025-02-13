package com.suffolk.library_management.repository;

import com.suffolk.library_management.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.suffolk.library_management.entity.Book;

import java.util.ArrayList;
import java.util.Optional;


@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("SELECT pb FROM Book pb where pb.coverImageName = ?1 ORDER BY pb.createdDate desc limit 1")
    Optional<Book> findByCoverImageName(String fileName);

    @Query("SELECT pb FROM Book pb where pb.fileName = ?1 ORDER BY pb.createdDate desc limit 1")
    Optional<Book> findByBookName(String fileName);

    @Query("SELECT pb FROM Book pb where pb.category =?1 ORDER BY pb.createdDate")
    ArrayList<Book> findByGenre(String genre);

    @Query("SELECT pb FROM Book pb ORDER BY pb.createdDate")
    ArrayList<Book> findBooks();
}
