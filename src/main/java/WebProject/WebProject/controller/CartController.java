package WebProject.WebProject.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import WebProject.WebProject.entity.*;
import WebProject.WebProject.repository.ProductVariantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import WebProject.WebProject.service.CartService;
import WebProject.WebProject.service.ProductService;

@Controller
public class CartController {

	@Autowired
	CartService cartService;

	@Autowired
	ProductService productService;

	@Autowired
	private ProductVariantRepository productVariantRepository;

	@Autowired
	HttpSession session;

	@GetMapping("/cart")
	public String CartView(Model model) throws Exception {
		User user = (User) session.getAttribute("acc");
		if (user == null) {
			session.setAttribute("NoSignIn", "Vui lòng đăng nhập trước khi thực hiện thao tác");
			return "redirect:/home";
		} else {
			List<Cart> listCart = cartService.GetAllCartByUser_id(user.getId());

			BigDecimal total = BigDecimal.ZERO;

			if (listCart != null) {
				for (Cart y : listCart) {
					if (y.getVariant() != null && y.getVariant().getPrice() != null) {
						BigDecimal itemTotal = BigDecimal.valueOf(y.getCount()).multiply(y.getVariant().getPrice());
						total = total.add(itemTotal);
					}
				}
				model.addAttribute("Total", total);
				model.addAttribute("listCart", listCart);

				session.setAttribute("listCart", listCart);
				session.setAttribute("Total", total);
				session.setAttribute("countCart", listCart.size());
			}
			return "shopping-cart";
		}
	}

	@GetMapping("/deleteCart/{id}")
	public String GetDeleteCart(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
		String referer = request.getHeader("Referer");
		User user = (User) session.getAttribute("acc");
		if (user == null) {
			return "redirect:" + (referer != null ? referer : "/home");
		} else {
			cartService.deleteById(id);
			session.setAttribute("CartDelete", id);

			syncCartSession(user);

			return "redirect:/cart";
		}
	}

	@PostMapping("/updateCart")
	public String UpdateCart(HttpServletRequest request) {
		User user = (User) session.getAttribute("acc");
		if (user == null)
			return "redirect:/home";

		@SuppressWarnings("unchecked")
		List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");

		if (listCart != null) {
			int i = 0;
			for (Cart o : listCart) {
				String countParam = request.getParameter("count" + i);
				if (countParam != null) {
					int count = Integer.parseInt(countParam);
					ProductVariant variant = o.getVariant();

					if (count > variant.getQuantity()) {

						session.setAttribute("UpdateCartErr",
								variant.getProduct().getProductName()
										+ " chỉ còn "
										+ variant.getQuantity()
										+ " sản phẩm.");

						return "redirect:/cart";
					}

					o.setCount(count);
					cartService.saveCart(o);
				}
				i++;
			}
		}

		syncCartSession(user);

		return "redirect:/cart";
	}

	@PostMapping("/addToCart")
	public String AddToCart(
			@RequestParam("product_id") Long productId,
			@RequestParam("color") String color,
			@RequestParam("size") String size,
			@RequestParam("quantity") int quantity,
			HttpServletRequest request) throws Exception {

		String referer = request.getHeader("Referer");
		User user = (User) session.getAttribute("acc");

		if (user == null) {
			session.setAttribute("AddToCartErr", "Vui lòng đăng nhập");
			return "redirect:" + (referer != null ? referer : "/home");
		}

		ProductVariant variant = productVariantRepository.findByProductIdAndColorAndSize(productId, color, size)
				.orElse(null);

		if (variant == null) {
			session.setAttribute("AddToCartErr", "Sản phẩm phiên bản này hiện không tồn tại hoặc hết hàng");
			return "redirect:" + (referer != null ? referer : "/home");
		}

		List<Cart> listCart = cartService.GetAllCartByUser_id(user.getId());
		Cart existedCart = null;

		if (listCart != null) {
			for (Cart c : listCart) {
				if (c.getVariant() != null && c.getVariant().getId().equals(variant.getId())) {
					existedCart = c;
					break;
				}
			}
		}

		if (existedCart != null) {

			int newQuantity = existedCart.getCount() + quantity;

			if (newQuantity > variant.getQuantity()) {
				session.setAttribute("AddToCartErr",
						"Chỉ còn " + variant.getQuantity() + " sản phẩm trong kho.");
				return "redirect:" + (referer != null ? referer : "/home");
			}

			existedCart.setCount(newQuantity);
			cartService.saveCart(existedCart);

		} else {

			if (quantity > variant.getQuantity()) {
				session.setAttribute("AddToCartErr",
						"Chỉ còn " + variant.getQuantity() + " sản phẩm trong kho.");
				return "redirect:" + (referer != null ? referer : "/home");
			}

			Cart newCart = new Cart();
			newCart.setVariant(variant);
			newCart.setUser(user);
			newCart.setCount(quantity);
			cartService.saveCart(newCart);
		}

		syncCartSession(user);

		return "redirect:" + (referer != null ? referer : "/cart");
	}

	private void syncCartSession(User user) {
		List<Cart> listCart = cartService.GetAllCartByUser_id(user.getId());

		BigDecimal total = BigDecimal.ZERO;

		if (listCart != null) {
			for (Cart c : listCart) {
				if (c.getVariant() != null && c.getVariant().getPrice() != null) {
					BigDecimal itemTotal = BigDecimal.valueOf(c.getCount()).multiply(c.getVariant().getPrice());
					total = total.add(itemTotal);
				}
			}
			session.setAttribute("listCart", listCart);
			session.setAttribute("countCart", listCart.size());
			session.setAttribute("Total", total);
		} else {
			session.setAttribute("listCart", null);
			session.setAttribute("countCart", 0);
			session.setAttribute("Total", BigDecimal.ZERO);
		}
	}
}