package WebProject.WebProject.controller;

import WebProject.WebProject.entity.Category;
import WebProject.WebProject.entity.User;
import WebProject.WebProject.repository.CategoryRepository;
import WebProject.WebProject.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class AdminCategoryController {

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private HttpSession session;


	@GetMapping("dashboard-categories")
	public String dashboardCategoriesView(Model model) {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			Pageable pageable = PageRequest.of(0, 3);
			Page<Category> pageCategory = categoryRepository.findAll(pageable);

			model.addAttribute("pageCategory", pageCategory);
			return "dashboard-categories";
		}
	}

	@GetMapping("dashboard-categories/{page}")
	public String dashboardCategoriesPageView(@PathVariable int page, Model model) {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			Pageable pageable = PageRequest.of(page, 3);
			Page<Category> pageCategory = categoryRepository.findAll(pageable);

			model.addAttribute("pageCategory", pageCategory);
			return "dashboard-categories";
		}
	}

	@GetMapping("dashboard-addcategory")
	public String dashboardAddCategoryView(Model model) {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			return "dashboard-addcategory";
		}
	}

	@PostMapping("dashboard-addcategory")
	public String dashboardAddCategoryHandle(@ModelAttribute("category_name") String categoryName,
			RedirectAttributes redirectAttributes) {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			Category newCate = new Category();
			newCate.setCategory_Name(categoryName);
			categoryRepository.save(newCate);

			redirectAttributes.addFlashAttribute("message", "Thêm danh mục thành công!");
			redirectAttributes.addFlashAttribute("messageType", "success");
			return "redirect:/dashboard-categories";
		}
	}

	@GetMapping("/dashboard-categories/edit/{id}")
	public String dashboardCategoryEditView(@PathVariable Long id, Model model) {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			Category category = categoryRepository.findById(id).orElse(null);
			model.addAttribute("category", category);
			return "dashboard-categories-edit";
		}
	}

	@PostMapping("/dashboard-categories/edit")
	public String dashboardCategoryEditHandle(@ModelAttribute("category_id") Long categoryId,
			@ModelAttribute("category_name") String categoryName,
			RedirectAttributes redirectAttributes) {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			Category category = categoryRepository.findById(categoryId).orElse(null);
			if (category != null) {
				category.setCategory_Name(categoryName);
				categoryRepository.save(category);
				redirectAttributes.addFlashAttribute("message", "Cập nhật danh mục thành công!");
				redirectAttributes.addFlashAttribute("messageType", "success");
			} else {
				redirectAttributes.addFlashAttribute("message", "Không tìm thấy danh mục!");
				redirectAttributes.addFlashAttribute("messageType", "danger");
			}

			return "redirect:/dashboard-categories";
		}
	}

	@GetMapping("/dashboard-categories/delete/{id}")
	public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		}

		if (id == 1) {
			redirectAttributes.addFlashAttribute("message", "Không thể xóa danh mục mặc định!");
			redirectAttributes.addFlashAttribute("messageType", "danger");
			return "redirect:/dashboard-categories";
		}

		Category category = categoryRepository.findById(id).orElse(null);
		if (category != null) {
			productRepository.updateCategory(id, 1L);
			categoryRepository.delete(category);
			redirectAttributes.addFlashAttribute("message", "Xóa danh mục thành công!");
			redirectAttributes.addFlashAttribute("messageType", "success");
		} else {
			redirectAttributes.addFlashAttribute("message", "Danh mục không tồn tại!");
			redirectAttributes.addFlashAttribute("messageType", "danger");
		}

		return "redirect:/dashboard-categories";
	}

	@PostMapping("/dashboard-categories/search")
	public String dashboardCategoriesSearch(@ModelAttribute("search-input") String searchInput, Model model) {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			Pageable pageable = PageRequest.of(0, 3);
			Page<Category> pageCategory = categoryRepository.findByCategoryNameContaining(searchInput, pageable);

			model.addAttribute("pageCategory", pageCategory);
			model.addAttribute("search_dashboard", "search_dashboard");
			model.addAttribute("search_input", searchInput);

			session.setAttribute("search_input_category_dashboard", searchInput);
			return "dashboard-categories";
		}
	}

	@GetMapping("/dashboard-categories/search/{page}")
	public String dashboardCategoriesSearchPage(@PathVariable int page, Model model) {
		User admin = (User) session.getAttribute("admin");
		if (admin == null) {
			return "redirect:/signin-admin";
		} else {
			String searchInput = (String) session.getAttribute("search_input_category_dashboard");
			Pageable pageable = PageRequest.of(page, 3);

			Page<Category> pageCategory = categoryRepository.findByCategoryNameContaining(searchInput, pageable);

			model.addAttribute("pageCategory", pageCategory);
			model.addAttribute("search_dashboard", "search_dashboard");
			model.addAttribute("search_input", searchInput);
			return "dashboard-categories";
		}
	}
}