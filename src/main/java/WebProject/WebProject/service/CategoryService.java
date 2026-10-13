package WebProject.WebProject.service;

import java.util.List;

import WebProject.WebProject.entity.Category;
import WebProject.WebProject.entity.dtos.CategoryDTO;

public interface CategoryService {

	Category saveCategory(Category category);

	Category getCategoryById(Long id);

	Category updateCategory(Category category);

	List<Category> findAll();

	List<CategoryDTO> findAllDtos();

	void deleteCategoryId(Long id);
}
