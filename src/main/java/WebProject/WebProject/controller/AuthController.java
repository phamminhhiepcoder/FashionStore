package WebProject.WebProject.controller;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import WebProject.WebProject.service.CloudinaryService;
import WebProject.WebProject.entity.Cart;
import WebProject.WebProject.entity.User;
import WebProject.WebProject.model.Mail;
import WebProject.WebProject.service.CartService;
import WebProject.WebProject.service.CookieService;
import WebProject.WebProject.service.MailService;
import WebProject.WebProject.service.UserService;

@Controller
public class AuthController {

	@Autowired
	UserService userService;

	@Autowired
	CartService cartService;

	@Autowired
	MailService mailService;

	@Autowired
	CloudinaryService cloudinaryService;

	@Autowired
	HttpSession session;

	@Autowired
	CookieService cookie;

	@GetMapping("/signin")
	public String SigInView(Model model) throws Exception {
		Cookie login_name = cookie.read("login_name");
		Cookie pass = cookie.read("pass");
		if (login_name != null)
			model.addAttribute("login_name", login_name.getValue());
		if (pass != null) {
			String decodedValue = new String(Base64.getDecoder().decode(pass.getValue()));
			model.addAttribute("pass", decodedValue);
		}

		return "signin";
	}

	@GetMapping("/signup")
	public String SignUpView(Model model) {
		return "signup";
	}

	@PostMapping("/signin")
	public String SignIn(@ModelAttribute("login-name") String loginname,
			@ModelAttribute("password") String password,
			@RequestParam(value = "remember", defaultValue = "false") boolean remember,
			Model model) throws Exception {
		User user = userService.findByIdAndRole(loginname, "user");
		if (user != null) {
			String decodedValue = new String(Base64.getDecoder().decode(user.getPassword()));
			if (decodedValue.equals(password)) {
				if (remember) {
					cookie.create("username", user.getId(), 3);
					cookie.create("login_name", user.getId(), 3);
					cookie.create("pass", user.getPassword(), 3);
					cookie.create("remember", "remember", 3);
				} else {
					cookie.create("login_name", user.getId(), 3);
					cookie.delete("pass");
				}
				session.setAttribute("acc", user);
				List<Cart> listCart = cartService.GetAllCartByUser_id(user.getId());
				session.setAttribute("countCart", listCart.size());
				return "redirect:/home";
			}
		}

		model.addAttribute("message", "Tên đăng nhập hoặc mật khẩu không chính xác!");
		model.addAttribute("messageType", "danger");
		return "signin";
	}

	@PostMapping("/signup")
	public String SignUp(@ModelAttribute("username") String id,
			@ModelAttribute("your_email") String email,
			@ModelAttribute("fullname") String fullname,
			@ModelAttribute("password") String password,
			@ModelAttribute("comfirm_password") String comfirm_password,
			Model model) throws Exception {

		if (!password.equals(comfirm_password)) {
			model.addAttribute("message", "Mật khẩu xác nhận không trùng khớp!");
			model.addAttribute("messageType", "danger");
			return "signup";
		}

		User user = userService.findByIdAndRole(id, "user");
		if (user != null) {
			model.addAttribute("message", "Tài khoản đăng nhập đã tồn tại!");
			model.addAttribute("messageType", "danger");
			return "signup";
		}

		User user2 = userService.GetUserByEmail(email);
		if (user2 != null) {
			model.addAttribute("message", "Email này đã tồn tại!");
			model.addAttribute("messageType", "danger");
			return "signup";
		}

		String encodedValue = Base64.getEncoder().encodeToString(password.getBytes());
		String avatar = "https://haycafe.vn/wp-content/uploads/2022/02/Avatar-trang-den.png";

		User newUser = new User();
		newUser.setId(id);
		newUser.setUsername(id); 
		newUser.setRole("user");
		newUser.setPassword(encodedValue);
		newUser.setAvatar(avatar);
		newUser.setEmail(email);
		newUser.setCreatedAt(LocalDateTime.now());
		newUser.setUpdatedAt(LocalDateTime.now());

		session.setAttribute("userRegister", newUser);
		session.removeAttribute("noSendRegisterEmail");

		return "redirect:/signup-verify";
	}

	@GetMapping("/signup-verify")
	public String signupVerifyView(Model model) throws Exception {
		User userRegister = (User) session.getAttribute("userRegister");
		if (userRegister == null)
			return "redirect:/signup";

		if (session.getAttribute("noSendRegisterEmail") == null) {
			int code = (int) Math.floor(((Math.random() * 899999) + 100000));
			Mail mail = new Mail();

			mail.setMailFrom("FashionStore@gmail.com");
			mail.setMailTo(userRegister.getEmail());
			mail.setMailSubject("Mã xác thực Đăng ký - FashionStore Shop");
			mail.setMailContent("Mã OTP của bạn là: " + code);
			mailService.sendEmail(mail);

			session.setAttribute("signupCode", code);
			session.setAttribute("noSendRegisterEmail", "true");
		}

		String errorCode = (String) session.getAttribute("signup_error_code");
		if (errorCode != null) {
			model.addAttribute("message", errorCode);
			model.addAttribute("messageType", "danger");
			session.removeAttribute("signup_error_code");
		}

		model.addAttribute("forgot", "Xác Thực Tài Khoản Đăng Ký");
		model.addAttribute("sendcode", "sendcode");
		return "otp";
	}

	@PostMapping("/signup-verify")
	public String signupVerifyHandle(@RequestParam("code_input") int code_input,
			RedirectAttributes redirectAttributes) throws Exception {
		Integer code = (Integer) session.getAttribute("signupCode");
		User userRegister = (User) session.getAttribute("userRegister");

		if (userRegister != null && code != null && code == code_input) {
			userService.saveUser(userRegister);

			session.removeAttribute("userRegister");
			session.removeAttribute("signupCode");
			session.removeAttribute("noSendRegisterEmail");

			redirectAttributes.addFlashAttribute("message", "Đăng ký tài khoản thành công! Vui lòng đăng nhập.");
			redirectAttributes.addFlashAttribute("messageType", "success");

			return "redirect:/signin";
		} else {
			session.setAttribute("signup_error_code", "Mã OTP đăng ký không chính xác!");
			return "redirect:/signup-verify";
		}
	}

	@GetMapping("/signout")
	public String SignOut(RedirectAttributes redirectAttributes) {
		session.setAttribute("acc", null);
		cookie.delete("remember");

		redirectAttributes.addFlashAttribute("message", "Đã đăng xuất thành công!");
		redirectAttributes.addFlashAttribute("messageType", "success");
		return "redirect:/signin";
	}

	@GetMapping("/forgot")
	public String forGotView(Model model) {
		String error_forgot = (String) session.getAttribute("error_forgot");
		if (error_forgot != null) {
			model.addAttribute("message", error_forgot);
			model.addAttribute("messageType", "danger");
			session.removeAttribute("error_forgot");
		}
		model.addAttribute("forgot", "Quên mật khẩu");
		return "signin";
	}

	@PostMapping("/forgot")
	public String forGotHandel(@ModelAttribute("login-name") String login_name) throws Exception {
		User user = userService.findByIdAndRole(login_name, "user");
		if (user == null) {
			session.setAttribute("error_forgot", "Tên đăng nhập không tồn tại!");
			return "redirect:/forgot";
		} else {
			session.setAttribute("userForgot", user);
			return "redirect:/code";
		}
	}

	@GetMapping("/code")
	public String codeView(Model model) throws Exception {
		User userForgot = (User) session.getAttribute("userForgot");
		if (userForgot == null) {
			return "redirect:/forgot";
		}

		String noSendEmail = (String) session.getAttribute("noSendEmail");
		if (noSendEmail == null) {
			int code = (int) Math.floor(((Math.random() * 899999) + 100000));
			Mail mail = new Mail();
			mail.setMailFrom("FashionStore@gmail.com");
			mail.setMailTo(userForgot.getEmail());
			mail.setMailSubject("Quên mật khẩu");
			mail.setMailContent("Mã OTP của bạn là: " + code);
			mailService.sendEmail(mail);

			session.setAttribute("code", code);
		}
		session.removeAttribute("noSendEmail");

		String errorCode = (String) session.getAttribute("error_code");
		if (errorCode != null) {
			model.addAttribute("message", errorCode);
			model.addAttribute("messageType", "danger");
			session.removeAttribute("error_code");
		}

		model.addAttribute("forgot", "Quên mật khẩu");
		model.addAttribute("sendcode", "sendcode");
		model.addAttribute("username", userForgot.getId());
		return "signin";
	}

	@PostMapping("/code")
	public String codeHandel(@RequestParam("code_input") int code_input) throws Exception {
		Integer code = (Integer) session.getAttribute("code");
		if (code != null && code == code_input) {
			session.removeAttribute("code");
			return "redirect:/newpass";
		} else {
			session.setAttribute("noSendEmail", "noSendEmail");
			session.setAttribute("error_code", "Mã OTP không chính xác!");
			return "redirect:/code";
		}
	}

	@GetMapping("/newpass")
	public String newPassView(Model model) {
		String error_newpass = (String) session.getAttribute("error_newpass");
		if (error_newpass != null) {
			model.addAttribute("message", error_newpass);
			model.addAttribute("messageType", "danger");
			session.removeAttribute("error_newpass");
		}

		model.addAttribute("forgot", "Quên mật khẩu");
		model.addAttribute("sendcode", "sendcode");
		model.addAttribute("changepass", "changepass");
		return "signin";
	}

	@PostMapping("/newpass")
	public String newPassHandel(@RequestParam("new_pass") String new_pass,
			@RequestParam("confirm_new_pass") String confirm_new_pass,
			RedirectAttributes redirectAttributes) throws Exception {
		if (new_pass.equals(confirm_new_pass)) {
			String encodedValue = Base64.getEncoder().encodeToString(new_pass.getBytes());
			User userForgot = (User) session.getAttribute("userForgot");

			if (userForgot != null) {
				userForgot.setPassword(encodedValue);
				userService.saveUser(userForgot);
				session.removeAttribute("userForgot");
			}

			redirectAttributes.addFlashAttribute("message", "Đổi mật khẩu thành công! Vui lòng đăng nhập lại.");
			redirectAttributes.addFlashAttribute("messageType", "success");
			return "redirect:/signin";
		} else {
			session.setAttribute("error_newpass", "Mật khẩu xác nhận không trùng khớp!");
			return "redirect:/newpass";
		}
	}
}