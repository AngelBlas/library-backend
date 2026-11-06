package com.lekasoft.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;
    @Column(nullable = false, length = 200)
    private String title;
    @Column(nullable = false, length = 150)
    private String author;
    @Column(length = 150)
    private String isbn;
    @Column(nullable = false)
    private String available;
    @ManyToOne
    @JoinColumn(name = "id_category", nullable = false, foreignKey = @ForeignKey(name = "FK_BOOK_CATEGORY"))
    private Category category;
}
