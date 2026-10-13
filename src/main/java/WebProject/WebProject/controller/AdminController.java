package WebProject.WebProject.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import WebProject.WebProject.entity.*;
import WebProject.WebProject.repository.CartRepository;
import WebProject.WebProject.repository.OrderRepository;
import WebProject.WebProject.repository.ProductRepository;
import WebProject.WebProject.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import WebProject.WebProject.service.CategoryService;
import WebProject.WebProject.service.CloudinaryService;
import WebProject.WebProject.service.MailService;
import WebProject.WebProject.service.OrderService;
import WebProject.WebProject.service.OrderItemService;
import WebProject.WebProject.service.ProductService;
import WebProject.WebProject.service.UserService;

@Controller
public class AdminController {
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
    ProductRepository productRepository;

    @Autowired
    MailService mailService;

    @Autowired
    HttpSession session;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("signin-admin")
    public String SignInAdminView(Model model) {
        String err_sign_admin = (String) session.getAttribute("err_sign_admin");
        model.addAttribute("err_sign_admin", err_sign_admin);
        session.setAttribute("err_sign_admin", null);
        return "signin-admin";
    }

    @PostMapping("signin-admin")
    public String SignInAdminHandel(@ModelAttribute("login-name") String login_name,
            @ModelAttribute("pass") String pass, Model model) throws Exception {
        User admin = userService.findByIdAndRole(login_name, "admin");
        if (admin == null) {
            session.setAttribute("err_sign_admin", "UserName or Password is not correct!");
            return "redirect:/signin-admin";
        } else {
            String decodedValue = new String(Base64.getDecoder().decode(admin.getPassword()));
            if (!decodedValue.equals(pass)) {
                session.setAttribute("err_sign_admin", "UserName or Password is not correct!");
                return "redirect:/signin-admin";
            } else {
                session.setAttribute("admin", admin);
                return "redirect:/dashboard";
            }
        }

    }

    @GetMapping("logout-admin")
    public String LogOutAdmin(Model model) {
        session.setAttribute("admin", null);
        return "redirect:/signin-admin";
    }

    @GetMapping("dashboard")
    public String DashboardView(
            @RequestParam(name = "timeRange", defaultValue = "month") String timeRange,
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model) {

        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (timeRange) {
            case "today":
                startDate = LocalDate.now();
                break;
            case "week":
                startDate = LocalDate.now().minusDays(7);
                break;
            case "year":
                startDate = LocalDate.now().withDayOfYear(1);
                break;
            case "month":
            default:
                startDate = LocalDate.now().withDayOfMonth(1);
                break;
        }

        List<Order> listOrder = orderService.findAll();

        List<Product> listProduct;
        if (keyword != null && !keyword.trim().isEmpty()) {
            listProduct = productRepository.findByProductNameContainingIgnoreCase(keyword.trim());
        } else {
            listProduct = productService.getAllProduct();
        }

        List<User> listUser = userService.findAll();
        List<Category> listCategory = categoryService.findAll();
        List<Order> recentOrders = orderService.findTop5RecentOrder();

        List<Object[]> topSellersObj = productRepository.findTopSellersByDateRange(startDate, endDate,
                PageRequest.of(0, 5));
        List<Map<String, Object>> topBestSellers = new ArrayList<>();
        for (Object[] obj : topSellersObj) {
            Product p = (Product) obj[0];
            Long totalSold = (Long) obj[1];
            Map<String, Object> map = new HashMap<>();
            map.put("productName", p.getProductName());
            map.put("sold", totalSold);
            topBestSellers.add(map);
        }

        List<Object[]> periodSalesObj = productRepository.findProductSalesInPeriod(startDate, endDate);
        Map<Long, Long> periodSalesMap = new HashMap<>();
        for (Object[] obj : periodSalesObj) {
            periodSalesMap.put((Long) obj[0], (Long) obj[1]);
        }

        LocalDate cutoffSlow = LocalDate.now().minusDays(30);
        List<Long> activeProductIds = productRepository.findActiveProductIdsSince(cutoffSlow);

        List<Map<String, Object>> inventoryStats = new ArrayList<>();
        for (Product p : listProduct) {
            Map<String, Object> item = new HashMap<>();
            item.put("product", p);

            int totalStock = 0;
            if (p.getVariants() != null) {
                totalStock = p.getVariants().stream().mapToInt(ProductVariant::getQuantity).sum();
            }
            item.put("totalStock", totalStock);

            long soldInPeriod = periodSalesMap.getOrDefault(p.getId(), 0L);
            item.put("soldInPeriod", soldInPeriod);

            String status;
            String statusClass;
            if (totalStock <= 20) {
                status = "Sắp hết hàng (Cần nhập kho)";
                statusClass = "badge-danger";
            } else if (!activeProductIds.contains(p.getId()) && totalStock > 0) {
                status = "Tồn kho lâu (Cần xả kho)";
                statusClass = "badge-warning";
            } else {
                status = "Ổn định";
                statusClass = "badge-success";
            }

            item.put("status", status);
            item.put("statusClass", statusClass);
            inventoryStats.add(item);
        }

        model.addAttribute("selectedTime", timeRange);
        model.addAttribute("keyword", keyword); 
        model.addAttribute("topBestSellers", topBestSellers);
        model.addAttribute("recentOrders", recentOrders);
        model.addAttribute("inventoryStats", inventoryStats);

        model.addAttribute("Total_Order", listOrder.size());
        model.addAttribute("Total_Product", productService.getAllProduct().size());
        model.addAttribute("Total_User", listUser.size());
        model.addAttribute("Total_Category", listCategory.size());

        return "dashboard";
    }

    @GetMapping("/dashboard-invoice/{id}")
    public String InvoiceView(@PathVariable Long id, Model model, HttpServletRequest request) {
        Order order = orderService.findById(id);
        List<OrderItem> listOrder_Item = orderItemService.getAllByOrder_Id(order.getId());
        model.addAttribute("listOrder_Item", listOrder_Item);
        model.addAttribute("order", order);
        return "dashboard-invoice";
    }

    @GetMapping("dashboard-wallet")
    public String DashboardWalletView(
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            Model model) {

        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        }

        LocalDate endDate = (endDateStr != null && !endDateStr.isEmpty()) ? LocalDate.parse(endDateStr)
                : LocalDate.now();
        LocalDate startDate = (startDateStr != null && !startDateStr.isEmpty()) ? LocalDate.parse(startDateStr)
                : LocalDate.now().minusDays(30);

        List<Order> allOrders = orderService.findAll();

        List<Order> listOrder = allOrders.stream().filter(o -> {
            if (o.getBooking_Date() == null || o.getStatus() == null)
                return false;

            if (!"Completed".equals(o.getStatus()))
                return false;

            LocalDate orderDate = o.getBooking_Date();

            return !orderDate.isBefore(startDate) && !orderDate.isAfter(endDate);
        }).collect(Collectors.toList());

        List<Order> listPaymentWithBank = listOrder.stream()
                .filter(o -> "Payment with bank".equals(o.getPayment_Method())).collect(Collectors.toList());
        List<Order> listPaymentOnDelivery = listOrder.stream()
                .filter(o -> "Payment on delivery".equals(o.getPayment_Method())).collect(Collectors.toList());

        BigDecimal totalBank = BigDecimal.ZERO;
        BigDecimal totalDelivery = BigDecimal.ZERO;

        BigDecimal totalProfit = BigDecimal.ZERO;

        for (Order y : listPaymentWithBank) {
            if (y.getTotal() != null)
                totalBank = totalBank.add(y.getTotal());
        }
        for (Order y : listPaymentOnDelivery) {
            if (y.getTotal() != null)
                totalDelivery = totalDelivery.add(y.getTotal());
        }

        for (Order o : listOrder) {
            if (o.getOrder_Item() != null) {
                for (OrderItem detail : o.getOrder_Item()) {
                    if (detail.getVariant() != null) {
                        ProductVariant variant = detail.getVariant();

                        BigDecimal importPrice = variant.getImportPrice() != null ? variant.getImportPrice()
                                : BigDecimal.ZERO;

                        BigDecimal salePrice = detail.getPrice() != null ? detail.getPrice()
                                : (variant.getPrice() != null ? variant.getPrice() : BigDecimal.ZERO);

                        BigDecimal quantity = new BigDecimal(detail.getCount());

                        BigDecimal itemProfit = salePrice.subtract(importPrice).multiply(quantity);

                        totalProfit = totalProfit.add(itemProfit);
                    }
                }
            }
        }

        Map<String, BigDecimal> revenueMapByDate = new TreeMap<>();
        Map<String, Long> orderCountMapByDate = new TreeMap<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM"));
            revenueMapByDate.put(formattedDate, BigDecimal.ZERO);
            orderCountMapByDate.put(formattedDate, 0L);
        }

        for (Order o : listOrder) {
            LocalDate orderDate = o.getBooking_Date();

            String formattedDate = orderDate.format(DateTimeFormatter.ofPattern("dd/MM"));
            if (revenueMapByDate.containsKey(formattedDate) && o.getTotal() != null) {
                revenueMapByDate.put(formattedDate, revenueMapByDate.get(formattedDate).add(o.getTotal()));
                orderCountMapByDate.put(formattedDate, orderCountMapByDate.get(formattedDate) + 1);
            }
        }

        List<Order> listRecentBank = listPaymentWithBank.stream().limit(5).collect(Collectors.toList());
        List<Order> listRecentDelivery = listPaymentOnDelivery.stream().limit(5).collect(Collectors.toList());

        model.addAttribute("startDate", startDate.toString());
        model.addAttribute("endDate", endDate.toString());

        model.addAttribute("TotalBank", totalBank);
        model.addAttribute("TotalDelivery", totalDelivery);
        model.addAttribute("TotalOrder", listOrder.size());
        model.addAttribute("TotalProfit", totalProfit);

        model.addAttribute("chartLabels", revenueMapByDate.keySet());
        model.addAttribute("chartRevenue", revenueMapByDate.values());

        model.addAttribute("listRecentDelivery", listRecentDelivery);
        model.addAttribute("listRecentBank", listRecentBank);

        return "dashboard-wallet";
    }

    @GetMapping("dashboard-myprofile")
    public String DashboardMyProfile(Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            String error_change_pass = (String) session.getAttribute("error_change_pass");
            String ChangePassSuccess = (String) session.getAttribute("ChangePassSuccess");
            String messageChangeProfile = (String) session.getAttribute("messageChangeProfile");
            model.addAttribute("messageChangeProfile", messageChangeProfile);
            model.addAttribute("error_change_pass", error_change_pass);
            model.addAttribute("ChangePassSuccess", ChangePassSuccess);
            session.setAttribute("error_change_pass", null);
            session.setAttribute("ChangePassSuccess", null);
            session.setAttribute("messageChangeProfile", null);
            model.addAttribute("admin", admin);
            return "dashboard-my-profile";
        }
    }

    @PostMapping("/dashboard-myprofile/changepassword")
    public String DashboardChangePassword(Model model, @ModelAttribute("current_password") String current_password,
            @ModelAttribute("new_password") String new_password,
            @ModelAttribute("confirm_password") String confirm_password, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            String decodedValue = new String(Base64.getDecoder().decode(admin.getPassword()));
            if (!decodedValue.equals(current_password)) {
                session.setAttribute("error_change_pass", "Current Password not correct!");
                return "redirect:/dashboard-myprofile";
            } else {

                if (!new_password.equals(confirm_password)) {
                    session.setAttribute("error_change_pass", "Confirm New Password not valid!");
                    return "redirect:/dashboard-myprofile";
                } else {
                    String encodedValue = Base64.getEncoder().encodeToString(new_password.getBytes());
                    admin.setPassword(encodedValue);
                    userService.saveUser(admin);
                    session.setAttribute("admin", admin);
                }
            }
            session.setAttribute("ChangePassSuccess", "ChangePassSuccess");
            return "redirect:" + referer;
        }
    }

    @PostMapping("/dashboard-myprofile/changeProfile")
    public String ChangeProfile(Model model, @ModelAttribute("avatar") MultipartFile avatar,
            @ModelAttribute("fullname") String fullname, @ModelAttribute("phone") String phone,
            @ModelAttribute("email") String email) throws IOException {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            if (!avatar.isEmpty()) {
                String url = cloudinaryService.uploadFile(avatar);
                admin.setAvatar(url);
            }
            admin.setUsername(fullname);
            admin.setEmail(email);
            admin.setPhone_Number(phone);
            userService.saveUser(admin);
            session.setAttribute("admin", admin);
            session.setAttribute("messageChangeProfile", "Change Success.");
            return "redirect:/dashboard-myprofile";
        }
    }

    @GetMapping("dashboard-users")
    public String DashboardUsersView(Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            Pageable pageable = PageRequest.of(0, 3);
            Page<User> page = userService.findAll(pageable);
            model.addAttribute("pageUser", page);
            return "dashboard-users";
        }
    }

    @GetMapping("dashboard-users/{page}")
    public String DashboardUserPageView(@PathVariable int page, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            Pageable pageable = PageRequest.of(page, 3);
            Page<User> pageUser = userService.findAll(pageable);
            model.addAttribute("pageUser", pageUser);
            return "dashboard-users";
        }
    }

    @PostMapping("/dashboard-users/search")
    public String DashboardMyproductSearch(@ModelAttribute("search-input") String search_input,
            Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            Page<User> pageUser = null;
            Pageable pageable = PageRequest.of(0, 3);

            pageUser = userService.findByUsernameContaining(search_input, pageable);
            model.addAttribute("pageUser", pageUser);
            model.addAttribute("search_dashboard", "search_dashboard");
            model.addAttribute("search_input", search_input);
            session.setAttribute("search_input_dashboard", search_input);
            return "dashboard-users";
        }
    }

    @GetMapping("/dashboard-adduser")
    public String dashboardAddUserView(Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        }
        return "dashboard-adduser";
    }

    @PostMapping("/dashboard-adduser")
    public String dashboardAddUserHandle(
            @RequestParam("id") String id,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("email") String email,
            @RequestParam("phone_Number") String phoneNumber,
            @RequestParam("role") String role,
            RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        }

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        String encodedValue = Base64.getEncoder().encodeToString(password.getBytes());
        user.setPassword(encodedValue);
        user.setEmail(email);
        user.setPhone_Number(phoneNumber);
        user.setRole(role);

        user.setLogin_Type("default");
        user.setAvatar("default.png");

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message", "Thêm người dùng thành công!");
        redirectAttributes.addFlashAttribute("messageType", "success");

        return "redirect:/dashboard-users";
    }

    @GetMapping("/dashboard-users/edit/{id}")
    public String dashboardUserEditView(@PathVariable String id, Model model) {

        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        }

        User user = userRepository.findById(id);
        model.addAttribute("user", user);

        return "dashboard-users-edit";
    }

    @PostMapping("/dashboard-users/edit")
    public String dashboardUserEditHandle(
            @RequestParam("id") String id,
            @RequestParam("username") String username,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam("email") String email,
            @RequestParam("phone_Number") String phoneNumber,
            @RequestParam("role") String role,
            RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        }

        User user = userRepository.findById(id);

        if (user != null) {
            user.setUsername(username);

            if (password != null && !password.trim().isEmpty()) {
                String encodedValue = Base64.getEncoder().encodeToString(password.getBytes());
                user.setPassword(encodedValue);
            }

            user.setEmail(email);
            user.setPhone_Number(phoneNumber);
            user.setRole(role);

            userRepository.save(user);

            redirectAttributes.addFlashAttribute("message", "Cập nhật người dùng thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Không tìm thấy người dùng!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/dashboard-users";
    }

    @GetMapping("/dashboard-users/delete/{id}")
    public String deleteUser(@PathVariable String id, RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        }

        User user = userRepository.findById(id);

        if (user != null) {
            cartRepository.deleteAll(user.getCart());
            orderRepository.deleteAll(user.getOrder());
            userRepository.delete(user);

            redirectAttributes.addFlashAttribute("message", "Xóa người dùng thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Người dùng không tồn tại!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/dashboard-users";
    }
}
