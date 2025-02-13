//package com.suffolk.library_management.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Builder
//@Entity
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "loans")
//public class Loan {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "book_id")
//    private Book book;
//
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    private String loanDate;
//    private String returnDate;
//}
