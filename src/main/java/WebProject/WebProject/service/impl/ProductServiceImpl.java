package WebProject.WebProject.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import WebProject.WebProject.entity.Product;
import WebProject.WebProject.entity.ProductVariant;
import WebProject.WebProject.repository.ProductRepository;
import WebProject.WebProject.repository.ProductVariantRepository;
import WebProject.WebProject.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	ProductRepository productRepository;

	@Autowired
	ProductVariantRepository variantRepository;

	@Override
	public List<Product> getAllProduct() {
		// TODO Auto-generated method stub
		return productRepository.findAll();
	}

	@Override
	public Product saveProduct(Product product) {
		// TODO Auto-generated method stub
		return productRepository.save(product);
	}

	@Override
	public Product getProductById(Long id) {
		return productRepository.findById(id).orElse(null);
	}

	@Override
	public Product updateProduct(Product product) {
		// TODO Auto-generated method stub
		return productRepository.save(product);
	}

	@Override
	public void deleteProductById(Long id) {
		// TODO Auto-generated method stub
		productRepository.deleteById(id);
	}

	@Override
	public List<Product> findByProduct_NameContaining(String name) {
		// TODO Auto-generated method stub
		return productRepository.findByProduct_NameContaining(name);
	}

	@Override
	public List<Product> findTopProductBestSellers() {
		return productRepository.findTopProductBestSellers();
	}

	@Override
	public List<Product> findTopProductNewArrivals() {
		// TODO Auto-generated method stub
		return productRepository.findTopProductNewArrivals();
	}

	@Override
	public Page<Product> findAll(Pageable pageable) {
		return productRepository.findAll(pageable);
	}

	@Override
	public Page<Product> findByProduct_NameAndCategory_idContaining(String name, Long category_id, Pageable pageable) {
		return productRepository.findByProduct_NameAndCategory_idContaining(name, category_id, pageable);
	}

	@Override
	public Page<Product> findByProduct_NameContaining(String name, Pageable pageable) {
		return productRepository.findByProduct_NameContaining(name, pageable);
	}

	@Override
	public ProductVariant saveVariant(ProductVariant variant) {
		return variantRepository.save(variant);
	}

	@Override
	public List<Product> findTop4ProductByCategory_id(Long id) {
		return productRepository.findTop4ProductByCategory_id(id);
	}

}
