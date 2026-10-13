package WebProject.WebProject.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import WebProject.WebProject.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import WebProject.WebProject.model.Mail;
import WebProject.WebProject.repository.OrderRepository;
import WebProject.WebProject.repository.ProductRepository;
import WebProject.WebProject.service.CategoryService;
import WebProject.WebProject.service.CloudinaryService;
import WebProject.WebProject.service.MailService;
import WebProject.WebProject.service.OrderService;
import WebProject.WebProject.service.OrderItemService;
import WebProject.WebProject.service.ProductService;
import WebProject.WebProject.service.UserService;

@Controller
public class AdminOrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    UserService userService;

    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    MailService mailService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    HttpSession session;

    @GetMapping("/dashboard-orders")
    public String DashboardOrderView(Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            List<Order> listOrder = orderService.findAll();
            model.addAttribute("listOrder", listOrder);
            return "dashboard-orders";
        }
    }

    @PostMapping("/send-message")
    public String SendMessage(Model model, @ModelAttribute("message") String message,
            @ModelAttribute("email") String email, HttpServletRequest request) throws Exception {
        String referer = request.getHeader("Referer");
        Mail mail = new Mail();
        mail.setMailFrom("FashionStore@gmail.com");
        mail.setMailTo(email);
        mail.setMailSubject("This is message from FashionStore Shop.");
        mail.setMailContent(message);
        mailService.sendEmail(mail);
        return "redirect:" + referer;
    }

    @GetMapping("/delete-order/{id}")
    public String DeleteOrder(@PathVariable Long id, Model model, HttpServletRequest request) throws Exception {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            String referer = request.getHeader("Referer");
            Order order = orderService.findById(id);
            if (order != null) {
                for (OrderItem y : order.getOrder_Item()) {
                    orderItemService.deleteById(y.getId());
                }
                orderService.deleteById(id);
            }
            return "redirect:" + referer;
        }
    }

    @GetMapping("/update-order-status/{id}")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam("status") String status,
            HttpServletRequest request) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        }

        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            boolean isAlreadyCompleted = "Completed".equalsIgnoreCase(order.getStatus());

            order.setStatus(status);

            if ("Completed".equalsIgnoreCase(status)) {
                order.setPaymentStatus("Paid");

                if (!isAlreadyCompleted && order.getOrder_Item() != null) {
                    for (OrderItem item : order.getOrder_Item()) {
                        if (item.getVariant() != null && item.getVariant().getProduct() != null) {
                            Product product = item.getVariant().getProduct();
                            product.setSold(product.getSold() + item.getCount());
                            productRepository.save(product);
                        }
                    }

                    sendOrderCompletedEmail(order);
                }
            }

            orderRepository.save(order);
        }
        return "redirect:/dashboard-orders";
    }

    private void sendOrderCompletedEmail(Order order) {
        try {
            if (order.getEmail() == null || order.getEmail().isEmpty()) {
                return;
            }

            Mail mail = new Mail();
            mail.setMailFrom("FashionStore@gmail.com");
            mail.setMailTo(order.getEmail());
            mail.setMailSubject("Đơn hàng #" + order.getId() + " đã giao thành công - FashionStore Shop");

            StringBuilder content = new StringBuilder();
            content.append("Cảm ơn bạn đã mua sắm tại FashionStore Shop!\n");
            content.append("Đơn hàng của bạn đã được giao thành công và hoàn tất.\n\n");
            content.append("--- HÓA ĐƠN ĐƠN HÀNG HOÀN TẤT ---\n");
            content.append("Mã đơn hàng: DH").append(order.getId()).append("\n");
            content.append("Họ tên khách hàng: ").append(order.getFullname()).append("\n");
            content.append("Số điện thoại: ").append(order.getPhone()).append("\n");
            content.append("Địa chỉ nhận hàng: ").append(order.getAddress()).append(", ").append(order.getCountry())
                    .append("\n");
            content.append("Phương thức thanh toán: ").append(order.getPayment_Method()).append("\n");
            content.append("Trạng thái thanh toán: Đã thanh toán (Paid)\n");
            content.append("Trạng thái đơn hàng: Hoàn thành (Completed)\n");
            content.append("Tổng thanh toán: ").append(order.getTotal()).append(" VNĐ\n\n");
            content.append(
                    "Nếu có bất kỳ thắc mắc nào về sản phẩm, xin vui lòng liên hệ với bộ phận CSKH của FashionStore Shop.");

            mail.setMailContent(content.toString());
            mailService.sendEmail(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
