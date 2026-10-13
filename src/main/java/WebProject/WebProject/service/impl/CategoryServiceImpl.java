package WebProject.WebProject.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import WebProject.WebProject.entity.Category;
import WebProject.WebProject.entity.dtos.CategoryDTO;
import WebProject.WebProject.repository.CategoryRepository;
import WebProject.WebProject.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {
	@Autowired
	CategoryRepository categoryRepository;

	@Override
	public Category saveCategory(Category category) {
		return null;
	}

	@Override
	public Category getCategoryById(Long id) {
		return categoryRepository.getById(id);
	}

	@Override
	public Category updateCategory(Category category) {
		return null;
	}

	@Override
	public void deleteCategoryId(Long id) {
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public List<CategoryDTO> findAllDtos() {
		List<Category> categories = categoryRepository.findAll();

		return categories.stream().map(category -> {
			CategoryDTO dto = new CategoryDTO();
			dto.setId(category.getId());
			dto.setCategory_Name(category.getCategory_Name());
			dto.setIcon(category.getIcon());
			return dto;
		}).collect(Collectors.toList());
	}
}
