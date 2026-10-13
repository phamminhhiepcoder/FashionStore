package WebProject.WebProject.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import WebProject.WebProject.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Category getById(int id);

	@Query("SELECT c FROM Category c WHERE c.category_Name LIKE %:name%")
	Page<Category> findByCategoryNameContaining(@Param("name") String name, Pageable pageable);
}
