package com.lekasoft.repository;

import com.lekasoft.model.Category;

import java.util.Optional;

public interface ICategoryRepository extends IGenericRepository<Category, Long> {

    Optional<Category> findByName(String name);
}
