package WebProject.WebProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import WebProject.WebProject.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	List<OrderItem> findAllByOrder_id(Long id);

	void deleteById(Long id);

	@Transactional
	@Modifying
	@Query("DELETE FROM OrderItem o WHERE o.variant.id = :variantId")
	void deleteByVariantId(@Param("variantId") Long variantId);
}
