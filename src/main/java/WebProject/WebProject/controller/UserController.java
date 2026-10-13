package WebProject.WebProject.controller;

import java.io.IOException;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import WebProject.WebProject.service.CloudinaryService;

import WebProject.WebProject.entity.User;
import WebProject.WebProject.service.CartService;
import WebProject.WebProject.service.CookieService;
import WebProject.WebProject.service.MailService;
import WebProject.WebProject.service.UserService;

@Controller
public class UserController {
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

	@GetMapping("/contact")
	public String ContactView(Model model) {
		return "contact";
	}

	@GetMapping("about")
	public String AboutView(Model model) {
		return "about";
	}


	@GetMapping("/myprofile")
	public String Myprofile(Model model, HttpServletRequest request) {
		User user = (User) session.getAttribute("acc");
		String referer = request.getHeader("Referer");
		String messageChangeProfile = (String) session.getAttribute("messageChangeProfile");
		model.addAttribute("messageChangeProfile", messageChangeProfile);
		session.setAttribute("messageChangeProfile", null);
		if (user == null) {
			return "redirect:" + referer;
		} else {
			String error_change_pass = (String) session.getAttribute("error_change_pass");
			String ChangePassSuccess = (String) session.getAttribute("ChangePassSuccess");
			model.addAttribute("error_change_pass", error_change_pass);
			model.addAttribute("ChangePassSuccess", ChangePassSuccess);
			session.setAttribute("error_change_pass", null);
			session.setAttribute("ChangePassSuccess", null);
			model.addAttribute("user", user);
			return "myprofile";
		}

	}

	@PostMapping("/changepassword")
	public String ChangePassword(Model model, @ModelAttribute("current_password") String current_password,
			@ModelAttribute("new_password") String new_password,
			@ModelAttribute("confirm_password") String confirm_password, HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		User user = (User) session.getAttribute("acc");
		String decodedValue = new String(Base64.getDecoder().decode(user.getPassword()));
		if (!decodedValue.equals(current_password)) {
			session.setAttribute("error_change_pass", "Current Password not correct!");
			return "redirect:/myprofile";
		} else {
			if (!new_password.equals(confirm_password)) {
				session.setAttribute("error_change_pass", "Confirm New Password not valid!");
				return "redirect:/myprofile";
			} else {
				String encodedValue = Base64.getEncoder().encodeToString(new_password.getBytes());
				user.setPassword(encodedValue);
				userService.saveUser(user);
				session.setAttribute("acc", user);
			}
		}
		session.setAttribute("ChangePassSuccess", "ChangePassSuccess");
		return "redirect:" + referer;
	}

	@PostMapping("/changeProfile")
	public String ChangeProfile(Model model, @ModelAttribute("avatar") MultipartFile avatar,
			@ModelAttribute("fullname") String fullname, @ModelAttribute("phone") String phone,
			@ModelAttribute("email") String email) throws IOException {
		User user = (User) session.getAttribute("acc");
		if (user != null) {
			if (!avatar.isEmpty()) {
				String url = cloudinaryService.uploadFile(avatar);
				user.setAvatar(url);
			}
			user.setUsername(fullname);
			user.setEmail(email);
			user.setPhone_Number(phone);
			userService.saveUser(user);
			session.setAttribute("acc", user);
			session.setAttribute("messageChangeProfile", "Change Success.");
			return "redirect:/myprofile";
		} else {
			return "redirect:/home";
		}
	}
}
