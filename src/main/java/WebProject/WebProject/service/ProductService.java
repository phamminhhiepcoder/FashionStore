package WebProject.WebProject.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import WebProject.WebProject.entity.Product;
import WebProject.WebProject.entity.ProductVariant;

public interface ProductService {
	List<Product> getAllProduct();

	Product saveProduct(Product product);

	ProductVariant saveVariant(ProductVariant variant);

	Product getProductById(Long id);

	Product updateProduct(Product product);

	void deleteProductById(Long id);

	List<Product> findByProduct_NameContaining(String name);

	List<Product> findTopProductBestSellers();

	List<Product> findTopProductNewArrivals();

	Page<Product> findAll(Pageable pageable);

	Page<Product> findByProduct_NameContaining(String name, Pageable pageable);

	Page<Product> findByProduct_NameAndCategory_idContaining(String name, Long category_id, Pageable pageable);

	List<Product> findTop4ProductByCategory_id(Long id);
}
