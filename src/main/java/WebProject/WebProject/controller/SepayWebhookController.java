package WebProject.WebProject.controller;

import WebProject.WebProject.model.Mail;
import WebProject.WebProject.entity.Order;
import WebProject.WebProject.service.MailService;
import WebProject.WebProject.service.OrderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/sepay_webhook")
public class SepayWebhookController {

	@Autowired
	private OrderService orderService;

	@Autowired
	private MailService mailService; 

	@PostMapping
	public ResponseEntity<Map<String, Object>> receiveWebhook(@RequestBody String requestBody,
			HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode data = mapper.readTree(requestBody);

			String transactionContent = data.path("content").asText();

			Pattern pattern = Pattern.compile("DH(\\d+)");
			Matcher matcher = pattern.matcher(transactionContent);

			if (!matcher.find()) {
				response.put("success", false);
				response.put("message", "Nội dung chuyển khoản không chứa mã đơn hàng hợp lệ (DHxxx)");
				return ResponseEntity.ok(response);
			}

			Long payOrderId = Long.parseLong(matcher.group(1));

			Order order = orderService.findById(payOrderId);
			if (order == null) {
				response.put("success", false);
				response.put("message", "Không tìm thấy đơn hàng ID: " + payOrderId);
				return ResponseEntity.ok(response);
			}

			order.setStatus("Confirmed");
			order.setPaymentStatus("Paid");
			orderService.saveOrder(order);

			sendOrderInvoiceEmail(order);

			response.put("success", true);
			response.put("message", "Cập nhật trạng thái đơn hàng thành công!");
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			e.printStackTrace();
			response.put("success", false);
			response.put("message", "Lỗi xử lý hệ thống: " + e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void sendOrderInvoiceEmail(Order order) {
		try {
			WebProject.WebProject.model.Mail mail = new Mail();
			mail.setMailFrom("FashionStore@gmail.com");
			mail.setMailTo(order.getEmail());
			mail.setMailSubject("Xác nhận thanh toán & Đơn hàng #" + order.getId() + " - FashionStore Shop");

			StringBuilder content = new StringBuilder();
			content.append("Cảm ơn bạn đã thanh toán và đặt hàng tại FashionStore Shop!\n");
			content.append("Đơn hàng của bạn đã được xác nhận thanh toán thành công.\n\n");
			content.append("--- THÔNG TIN HÓA ĐƠN ---\n");
			content.append("Mã đơn hàng: DH").append(order.getId()).append("\n");
			content.append("Họ tên: ").append(order.getFullname()).append("\n");
			content.append("Số điện thoại: ").append(order.getPhone()).append("\n");
			content.append("Địa chỉ giao hàng: ").append(order.getAddress()).append(", ").append(order.getCountry())
					.append("\n");
			content.append("Phương thức thanh toán: ").append(order.getPayment_Method()).append("\n");
			content.append("Trạng thái thanh toán: Đã thanh toán (Paid)\n");
			content.append("Tổng tiền: ").append(order.getTotal()).append(" VNĐ\n");
			content.append("\nChúng tôi sẽ chuẩn bị hàng và giao cho bạn trong thời gian sớm nhất!");

			mail.setMailContent(content.toString());
			mailService.sendEmail(mail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}