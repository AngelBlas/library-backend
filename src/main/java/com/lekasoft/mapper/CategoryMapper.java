package com.lekasoft.mapper;

import com.lekasoft.dto.CategoryDTO;
import com.lekasoft.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(CategoryDTO categoryDTO);

    CategoryDTO toCategoryDto(Category category);
}
