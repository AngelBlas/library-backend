package com.lekasoft.service.impl;

import com.lekasoft.model.Category;
import com.lekasoft.repository.ICategoryRepository;
import com.lekasoft.repository.IGenericRepository;
import com.lekasoft.service.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends CRUDImpl<Category, Long> implements ICategoryService {

    private final ICategoryRepository repository;

    @Override
    protected IGenericRepository<Category, Long> getRepo() {
        return repository;
    }
}
