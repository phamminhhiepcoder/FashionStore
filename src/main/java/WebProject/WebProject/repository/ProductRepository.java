package WebProject.WebProject.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import WebProject.WebProject.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query(value = "SELECT * FROM product p WHERE p.product_name LIKE %?1%", nativeQuery = true)
	List<Product> findByProduct_NameContaining(String name);

	@Query(value = "SELECT p.* FROM product p " +
			"ORDER BY (p.sold >= 100) DESC, p.sold DESC " +
			"LIMIT 24", nativeQuery = true)
	List<Product> findTopProductBestSellers();

	@Query(value = "SELECT DISTINCT p.* FROM product p " +
			"WHERE p.created_at >= DATE_SUB(NOW(), INTERVAL 1 MONTH) " +
			"ORDER BY p.created_at DESC LIMIT 24", nativeQuery = true)
	List<Product> findTopProductNewArrivals();

	@Modifying
	@Transactional
	@Query(value = "UPDATE product SET category_id = :newCategoryId WHERE category_id = :oldCategoryId", nativeQuery = true)
	void updateCategory(@Param("oldCategoryId") Long oldCategoryId,
			@Param("newCategoryId") Long newCategoryId);

	Page<Product> findAllByCategory_id(Long id, Pageable pageable);

	@Query(value = "SELECT * FROM product p WHERE p.product_name LIKE CONCAT('%', ?1, '%') AND p.category_id = ?2", countQuery = "SELECT COUNT(*) FROM product p WHERE p.product_name LIKE CONCAT('%', ?1, '%') AND p.category_id = ?2", nativeQuery = true)
	Page<Product> findByProduct_NameAndCategory_idContaining(String name,
			Long categoryId,
			Pageable pageable);

	@Query(value = "SELECT * FROM product p WHERE p.product_name LIKE %?1%", nativeQuery = true)
	Page<Product> findByProduct_NameContaining(String name, Pageable pageable);

	@Query(value = " SELECT DISTINCT p.* FROM product p INNER JOIN product_variant pv ON pv.product_id = p.id WHERE p.category_id = :id AND pv.price > 0 AND p.is_active = 1 AND p.is_selling = 1 ORDER BY p.sold DESC LIMIT 4", nativeQuery = true)
	List<Product> findTop4ProductByCategory_id(@Param("id") Long id);

	List<Product> findTop5ByOrderBySoldDesc();

	@Query("SELECT p FROM Product p JOIN p.variants v GROUP BY p.id HAVING SUM(v.quantity) < 20 ORDER BY SUM(v.quantity) ASC")
	List<Product> findTop5LowStockProducts(Pageable pageable);

	Page<Product> findByCategoryId(Long id, Pageable pageable);

	@Query("SELECT DISTINCT p FROM Product p JOIN p.variants v WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) AND v.price > 0")
	Page<Product> findByProductNameContainingIgnoreCase(@Param("keyword") String keyword, Pageable pageable);

	@Query("SELECT DISTINCT p FROM Product p JOIN p.variants v WHERE p.category.id = :category AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) AND v.price > 0")
	Page<Product> findByCategoryAndKeyword(@Param("category") Long category, @Param("keyword") String keyword,
			Pageable pageable);

	@Query("SELECT DISTINCT p FROM Product p JOIN p.variants v WHERE p.category.id = :category AND v.price > 0")
	Page<Product> findByCategoryIdAndPriceGreaterThanZero(@Param("category") Long category, Pageable pageable);

	@Query("SELECT DISTINCT p FROM Product p JOIN p.variants v WHERE v.price > 0")
	Page<Product> findAllSelling(Pageable pageable);

	@Query("SELECT p FROM Product p JOIN p.variants v GROUP BY p HAVING MIN(v.price) BETWEEN :minPrice AND :maxPrice")
	Page<Product> findAllByPrice(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice,
			Pageable pageable);

	@Query("SELECT p FROM Product p JOIN p.variants v WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%',:keyword,'%')) GROUP BY p HAVING MIN(v.price) BETWEEN :minPrice AND :maxPrice")
	Page<Product> findByKeywordAndPrice(@Param("keyword") String keyword, @Param("minPrice") BigDecimal minPrice,
			@Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

	@Query("SELECT p FROM Product p JOIN p.variants v WHERE p.category.id=:categoryId GROUP BY p HAVING MIN(v.price) BETWEEN :minPrice AND :maxPrice")
	Page<Product> findByCategoryAndPrice(@Param("categoryId") Long categoryId, @Param("minPrice") BigDecimal minPrice,
			@Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

	@Query("SELECT p FROM Product p JOIN p.variants v WHERE p.category.id=:categoryId AND LOWER(p.productName) LIKE LOWER(CONCAT('%',:keyword,'%')) GROUP BY p HAVING MIN(v.price) BETWEEN :minPrice AND :maxPrice")
	Page<Product> findByCategoryKeywordAndPrice(@Param("categoryId") Long categoryId, @Param("keyword") String keyword,
			@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

	@Query("SELECT p, COALESCE(SUM(oi.count), 0) as totalSold " +
			"FROM OrderItem oi " +
			"JOIN oi.variant pv " +
			"JOIN pv.product p " +
			"JOIN oi.order o " +
			"WHERE o.booking_Date BETWEEN :startDate AND :endDate " +
			"GROUP BY p " +
			"ORDER BY totalSold DESC")
	List<Object[]> findTopSellersByDateRange(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate,
			Pageable pageable);

	@Query("SELECT p.id, COALESCE(SUM(oi.count), 0) " +
			"FROM OrderItem oi " +
			"JOIN oi.variant pv " +
			"JOIN pv.product p " +
			"JOIN oi.order o " +
			"WHERE o.booking_Date BETWEEN :startDate AND :endDate " +
			"GROUP BY p.id")
	List<Object[]> findProductSalesInPeriod(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Query("SELECT DISTINCT p.id " +
			"FROM OrderItem oi " +
			"JOIN oi.variant pv " +
			"JOIN pv.product p " +
			"JOIN oi.order o " +
			"WHERE o.booking_Date >= :cutoffDate")
	List<Long> findActiveProductIdsSince(@Param("cutoffDate") LocalDate cutoffDate);

	@Query("SELECT DISTINCT v.color FROM ProductVariant v WHERE v.color IS NOT NULL AND TRIM(v.color) != ''")
	List<String> findAllDistinctColors();

	@Query("SELECT DISTINCT v.size FROM ProductVariant v WHERE v.size IS NOT NULL AND TRIM(v.size) != ''")
	List<String> findAllDistinctSizes();

	@Query("SELECT DISTINCT p FROM Product p JOIN p.variants v WHERE " +
			"(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
			"(:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
			"(v.price BETWEEN :minPrice AND :maxPrice) AND " +
			"(:size IS NULL OR :size = '' OR v.size = :size) AND " +
			"(:color IS NULL OR :color = '' OR v.color = :color)")
	Page<Product> filterProducts(
			@Param("categoryId") Long categoryId,
			@Param("keyword") String keyword,
			@Param("minPrice") BigDecimal minPrice,
			@Param("maxPrice") BigDecimal maxPrice,
			@Param("size") String size,
			@Param("color") String color,
			Pageable pageable);

	List<Product> findByProductNameContainingIgnoreCase(String keyword);
}