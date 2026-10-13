package WebProject.WebProject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import WebProject.WebProject.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

	List<Cart> findAllByUser_id(String user_id);

	@Transactional
	@Modifying
	@Query("DELETE FROM Cart c WHERE c.variant.id = :variantId")
	void deleteByVariantId(@Param("variantId") Long variantId);

}
