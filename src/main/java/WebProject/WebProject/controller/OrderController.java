package WebProject.WebProject.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import WebProject.WebProject.entity.*;
import WebProject.WebProject.model.Mail;
import WebProject.WebProject.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderController {

	@Autowired
	CartService cartService;

	@Autowired
	ProductService productService;

	@Autowired
	OrderItemService order_ItemService;

	@Autowired
	OrderService orderService;

	@Autowired
	MailService mailService; 

	@Autowired
	HttpSession session;

	@GetMapping("checkout")
	public String CheckOutView(Model model) {
		User user = (User) session.getAttribute("acc");
		if (user == null) {
			session.setAttribute("AddToCartErr", "Vui lòng đăng nhập trước khi thực hiện thao tác!");
			return "redirect:/home";
		} else {
			List<Cart> Cart = cartService.GetAllCartByUser_id(user.getId());
			if (!Cart.isEmpty()) {
				BigDecimal total = (BigDecimal) session.getAttribute("Total");
				model.addAttribute("Total", total);
				model.addAttribute("listCart", (List<Cart>) session.getAttribute("listCart"));
				@SuppressWarnings("unchecked")
				List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
				model.addAttribute("listCart", listCart);
				return "checkout";
			} else {
				session.setAttribute("CartIsEmpty", "CartIsEmpty");
				return "redirect:/cart";
			}
		}
	}

	@PostMapping("checkout")
	public String CheckOut(@ModelAttribute("fullname") String fullname, @ModelAttribute("country") String country,
			@ModelAttribute("address") String address, @ModelAttribute("phone") String phone,
			@ModelAttribute("email") String email, @ModelAttribute("note") String note,
			@RequestParam(value = "payment", defaultValue = "cod") String payment, Model model,
			HttpServletResponse resp) throws Exception {

		@SuppressWarnings("unchecked")
		List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
		User user = (User) session.getAttribute("acc");
		BigDecimal total = (BigDecimal) session.getAttribute("Total");

		String status = "Pending";
		String payment_method = payment.equals("cod") ? "Payment on delivery" : "Payment with bank";

		Order newOrder = new Order();
		newOrder.setTotal(total);
		newOrder.setAddress(address);
		newOrder.setBooking_Date(LocalDate.now());
		newOrder.setCountry(country);
		newOrder.setEmail(email);
		newOrder.setFullname(fullname);
		newOrder.setNote(note);
		newOrder.setPayment_Method(payment_method);
		newOrder.setPhone(phone);
		newOrder.setStatus(status);
		newOrder.setPaymentStatus("Unpaid");
		newOrder.setUser(user);

		orderService.saveOrder(newOrder);
		List<Order> listOrder = orderService.getAllOrderByUser_Id(user.getId());
		newOrder = listOrder.get(listOrder.size() - 1);

		for (Cart y : listCart) {
			ProductVariant variant = y.getVariant();
			variant.setQuantity(variant.getQuantity() - y.getCount());
			productService.saveVariant(variant);

			OrderItem item = new OrderItem();
			item.setOrder(newOrder);
			item.setVariant(variant);
			item.setCount(y.getCount());
			item.setPrice(variant.getPrice());
			item.setTotal(variant.getPrice().multiply(BigDecimal.valueOf(y.getCount())));

			order_ItemService.saveOrder_Item(item);
			cartService.deleteById(y.getId());
		}

		session.setAttribute("order", newOrder);

		if ("Payment on delivery".equals(payment_method)) {
			sendOrderInvoiceEmail(newOrder);
		}

		if ("Payment with bank".equals(payment_method)) {
			return "redirect:/sepay-checkout/" + newOrder.getId();
		} else {
			return "redirect:/invoice";
		}
	}

	private void sendOrderInvoiceEmail(Order order) {
		try {
			Mail mail = new Mail();
			mail.setMailFrom("FashionStore@gmail.com");
			mail.setMailTo(order.getEmail());
			mail.setMailSubject("Xác nhận đơn hàng #" + order.getId() + " - FashionStore Shop");

			StringBuilder content = new StringBuilder();
			content.append("Cảm ơn bạn đã đặt hàng tại FashionStore Shop!\n");
			content.append("Đơn hàng của bạn đã được xác nhận đặt thành công.\n\n");
			content.append("--- THÔNG TIN HÓA ĐƠN ---\n");
			content.append("Mã đơn hàng: DH").append(order.getId()).append("\n");
			content.append("Họ tên: ").append(order.getFullname()).append("\n");
			content.append("Số điện thoại: ").append(order.getPhone()).append("\n");
			content.append("Địa chỉ: ").append(order.getAddress()).append(", ").append(order.getCountry()).append("\n");
			content.append("Phương thức thanh toán: ").append(order.getPayment_Method()).append("\n");
			content.append("Tổng tiền: ").append(order.getTotal()).append(" VNĐ\n");
			content.append("\nChúng tôi sẽ liên hệ và giao hàng sớm nhất cho bạn!");

			mail.setMailContent(content.toString());
			mailService.sendEmail(mail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@GetMapping("/sepay-checkout/{id}")
	public String sepayCheckoutView(@PathVariable Long id, Model model) {
		Order order = orderService.findById(id);
		if (order == null) {
			return "redirect:/home";
		}
		model.addAttribute("order", order);
		model.addAttribute("bankName", "MBBank");
		model.addAttribute("accountNumber", "7712032003");
		model.addAttribute("accountName", "HKD Fashion Store");
		return "sepay_checkout";
	}

	@PostMapping("/api/check-payment-status")
	@ResponseBody
	public java.util.Map<String, String> checkPaymentStatus(
			@RequestParam("order_id") Long orderId,
			javax.servlet.http.HttpSession session) {

		java.util.Map<String, String> response = new java.util.HashMap<>();
		Order order = orderService.findById(orderId);

		if (order != null) {
			if ("Paid".equalsIgnoreCase(order.getPaymentStatus())) {
				session.setAttribute("order", order);
				session.setAttribute("invoiceView", "True");

				response.put("payment_status", "Paid");
				response.put("redirect_url", "/invoice");
			} else {
				response.put("payment_status", "Unpaid");
			}
		} else {
			response.put("payment_status", "order_not_found");
		}
		return response;
	}

	@GetMapping("invoice")
	public String Invoice(Model model) {
		Order order = (Order) session.getAttribute("order");
		String invoiceView = (String) session.getAttribute("invoiceView");
		session.setAttribute("invoiceView", null);
		List<OrderItem> listOrder_Item = order_ItemService.getAllByOrder_Id(order.getId());
		model.addAttribute("invoiceView", invoiceView);
		model.addAttribute("listOrder_Item", listOrder_Item);
		model.addAttribute("order", order);
		return "invoice";
	}

	@GetMapping("/invoice/{id}")
	public String InvoiceView(@PathVariable Long id, Model model, HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		model.addAttribute("referer", referer);
		Order order = orderService.findById(id);
		session.setAttribute("order", order);
		session.setAttribute("invoiceView", "view");
		return "redirect:/invoice";
	}

	@GetMapping("/myhistory")
	public String Myhistory(Model model, HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		User user = (User) session.getAttribute("acc");
		if (user == null) {
			return "redirect:" + referer;
		} else {
			List<Order> listOrder = orderService.getAllOrderByUser_Id(user.getId());
			Collections.reverse(listOrder);
			model.addAttribute("listOrder", listOrder);
		}
		return "myhistory";
	}
}