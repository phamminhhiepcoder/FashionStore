package WebProject.WebProject.controller;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import WebProject.WebProject.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import WebProject.WebProject.repository.CartRepository;
import WebProject.WebProject.repository.OrderItemRepository;
import WebProject.WebProject.repository.ProductImageRepository;
import WebProject.WebProject.repository.ProductRepository;
import WebProject.WebProject.repository.ProductVariantRepository;
import WebProject.WebProject.service.CartService;
import WebProject.WebProject.service.CategoryService;
import WebProject.WebProject.service.CookieService;
import WebProject.WebProject.service.ProductService;
import WebProject.WebProject.service.UserService;

@Controller
public class ProductController {

	@Autowired
	ProductService productService;

	@Autowired
	UserService userService;

	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductRepository productRepository;

	@Autowired
	CartService cartService;

	@Autowired
	HttpSession session;

	@Autowired
	private ProductVariantRepository productVariantRepository;

	@Autowired
	private ProductImageRepository productImageRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	CookieService cookie;

	@GetMapping(value = { "", "/home" })
	public String list(Model model) throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "application/json");
		Cookie remember = cookie.read("remember");
		String error_bank = (String) session.getAttribute("error_bank");
		String NoSignIn = (String) session.getAttribute("NoSignIn");
		session.setAttribute("NoSignIn", null);
		session.setAttribute("error_bank", null);
		User acc = (User) session.getAttribute("acc");

		if (acc != null) {
			List<Cart> listCart = cartService.GetAllCartByUser_id(acc.getId());
			session.setAttribute("countCart", listCart.size());
		}
		if (session.getAttribute("acc") == null)
			session.setAttribute("countCart", "0");
		model.addAttribute("error_bank", error_bank);
		model.addAttribute("NoSignIn", NoSignIn);

		List<Product> Top12ProductBestSellers = productService.findTopProductBestSellers();
		List<Product> Top12ProductNewArrivals = productService.findTopProductNewArrivals();
		model.addAttribute("TopProductBestSellers", Top12ProductBestSellers);
		model.addAttribute("TopProductNewArrivals", Top12ProductNewArrivals);

		return "index";
	}

	@GetMapping("/shop")
	public String shop(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) Long category,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) BigDecimal minPrice,
			@RequestParam(required = false) BigDecimal maxPrice,
			@RequestParam(required = false) String size,
			@RequestParam(required = false) String color,
			Model model) {

		Pageable pageable = PageRequest.of(page, 12);

		if (minPrice == null) {
			minPrice = BigDecimal.ZERO;
		}

		if (maxPrice == null) {
			maxPrice = new BigDecimal("999999999999");
		}

		Page<Product> products = productRepository.filterProducts(
				category, keyword, minPrice, maxPrice, size, color, pageable);

		model.addAttribute("listProduct", products);
		model.addAttribute("TotalPro", products.getTotalElements());
		model.addAttribute("listCategory", categoryService.findAll());
		model.addAttribute("listColors", productRepository.findAllDistinctColors());
		model.addAttribute("listSizes", productRepository.findAllDistinctSizes());

		model.addAttribute("category", category);
		model.addAttribute("keyword", keyword);
		model.addAttribute("minPrice", minPrice);
		model.addAttribute("maxPrice", maxPrice);
		model.addAttribute("size", size);
		model.addAttribute("color", color);

		return "shop";
	}

	@GetMapping("/productDetail/{id}")
	public String ProductDetailId(@PathVariable Long id, Model model, HttpServletRequest request) {
		Product product = productService.getProductById(id);

		if (product != null) {
			List<Product> relatedProduct = productService.findTop4ProductByCategory_id(product.getCategory().getId());
			model.addAttribute("relatedProduct", relatedProduct);

			Set<String> colors = product.getVariants().stream()
					.map(ProductVariant::getColor)
					.filter(color -> color != null && !color.trim().isEmpty())
					.collect(Collectors.toSet());

			Set<String> sizes = product.getVariants().stream()
					.map(ProductVariant::getSize)
					.filter(size -> size != null && !size.trim().isEmpty())
					.collect(Collectors.toSet());

			model.addAttribute("colors", colors);
			model.addAttribute("sizes", sizes);
			model.addAttribute("product", product);

			HttpSession session = request.getSession();
			String addToCartErr = (String) session.getAttribute("AddToCartErr");
			if (addToCartErr != null) {
				model.addAttribute("error", addToCartErr);
				session.removeAttribute("AddToCartErr");
			}

			return "shop-details";
		} else {
			return "redirect:/home";
		}
	}

}
