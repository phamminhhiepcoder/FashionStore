package WebProject.WebProject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import WebProject.WebProject.entity.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
	List<ProductVariant> findByProductId(Long id);
	Optional<ProductVariant> findByProductIdAndColorAndSize(Long productId, String color, String size);
}
