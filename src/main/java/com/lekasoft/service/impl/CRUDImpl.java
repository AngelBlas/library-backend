package com.lekasoft.service.impl;

import com.lekasoft.exception.NotFoundException;
import com.lekasoft.repository.IGenericRepository;
import com.lekasoft.service.ICRUD;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class CRUDImpl<T, ID> implements ICRUD<T, ID> {

    protected abstract IGenericRepository<T, ID> getRepo();

    @Override
    public T save(T entity) {
        return getRepo().save(entity);
    }

    @Override
    public T update(ID id, T entity) {
        getRepo().findById(id).orElseThrow(() -> new NotFoundException("Registro no encontrado: " + id));
        return getRepo().save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public T findById(ID id) {
        return getRepo().findById(id).orElseThrow(() -> new NotFoundException("Registro no encontrado: " + id));
    }

    @Override
    public void delete(ID id) {
        getRepo().deleteById(id);
    }

}
