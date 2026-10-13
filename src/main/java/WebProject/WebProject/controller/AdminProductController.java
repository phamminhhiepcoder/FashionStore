package WebProject.WebProject.controller;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import WebProject.WebProject.entity.*;
import WebProject.WebProject.repository.CartRepository;
import WebProject.WebProject.repository.OrderItemRepository;
import WebProject.WebProject.repository.ProductImageRepository;
import WebProject.WebProject.repository.ProductRepository;
import WebProject.WebProject.repository.ProductVariantRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
import WebProject.WebProject.service.ProductImageService;
import WebProject.WebProject.service.ProductService;

@Controller
public class AdminProductController {
    @Autowired
    ProductService productService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    HttpSession session;

    @Autowired
    ProductImageService productImageService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @GetMapping("dashboard-myproducts")
    public String DashboardMyProductView(Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            List<Category> listCategories = categoryService.findAll();
            Pageable pageable = PageRequest.of(0, 12);
            Page<Product> pageProduct = productService.findAll(pageable);
            model.addAttribute("pageProduct", pageProduct);
            model.addAttribute("listCategories", listCategories);
            return "dashboard-myproducts";
        }
    }

    @GetMapping("dashboard-myproducts/{page}")
    public String DashboardMyProductPageView(@PathVariable int page, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            List<Category> listCategories = categoryService.findAll();
            Pageable pageable = PageRequest.of(page, 12);
            Page<Product> pageProduct = productService.findAll(pageable);
            model.addAttribute("pageProduct", pageProduct);
            model.addAttribute("listCategories", listCategories);
            return "dashboard-myproducts";
        }
    }

    @GetMapping("dashboard-addproduct")
    public String DashboardAddProductView(Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            List<Category> listCategories = categoryService.findAll();
            model.addAttribute("listCategories", listCategories);
            return "dashboard-addproduct";
        }
    }

    @PostMapping("dashboard-addproduct")
    public String DashboardAddProductHandel(
            @ModelAttribute("product_name") String product_name,
            @ModelAttribute("category") Long category,
            @ModelAttribute("description") String description,
            @ModelAttribute("design") String design,
            @ModelAttribute("gender") String gender,
            RedirectAttributes redirectAttributes) throws Exception {

        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            Category cate = categoryService.getCategoryById(category);

            Product newPro = new Product();
            newPro.setCreatedAt(LocalDateTime.now());
            newPro.setDescription(description);
            newPro.setGender(gender);
            newPro.setIs_Active(1);
            newPro.setIs_Selling(1);
            newPro.setProductName(product_name);
            newPro.setSold(0);
            newPro.setCategory(cate);
            productService.saveProduct(newPro);

            redirectAttributes.addFlashAttribute("message", "Thêm sản phẩm thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
            return "redirect:/dashboard-myproducts";
        }
    }

    @GetMapping("/dashboard-myproducts/edit/{id}")
    public String DashboardMyProductEditView(@PathVariable Long id, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            List<Category> listCategories = categoryService.findAll();
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            model.addAttribute("listCategories", listCategories);
            return "dashboard-my-products-edit";
        }
    }

    @PostMapping("/dashboard-myproducts/edit")
    public String DashboardMyProductEditHandel(
            @ModelAttribute("product_id") Long product_id,
            @ModelAttribute("product_name") String product_name,
            @ModelAttribute("category") Long category,
            @ModelAttribute("description") String description,
            @ModelAttribute("design") String design,
            @ModelAttribute("gender") String gender,
            RedirectAttributes redirectAttributes) throws Exception {

        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            Category cate = categoryService.getCategoryById(category);
            Product product = productService.getProductById(product_id);

            if (product != null) {
                product.setProductName(product_name);
                product.setCategory(cate);
                product.setDescription(description);
                product.setGender(gender);
                productService.saveProduct(product);

                redirectAttributes.addFlashAttribute("message", "Cập nhật sản phẩm thành công!");
                redirectAttributes.addFlashAttribute("messageType", "success");
            } else {
                redirectAttributes.addFlashAttribute("message", "Sản phẩm không tồn tại!");
                redirectAttributes.addFlashAttribute("messageType", "danger");
            }

            return "redirect:/dashboard-myproducts";
        }
    }

    @GetMapping("/dashboard-myproducts/delete-image/{id}")
    public String DeleteImage(@PathVariable Long id, HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        productImageService.deleteById(id);
        return "redirect:" + referer;
    }

    @PostMapping("/dashboard-myproduct/search")
    public String DashboardMyproductSearch(@ModelAttribute("search-input") String search_input,
            @ModelAttribute("category-selected") Long category_selected, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            Page<Product> pageProduct = null;
            Pageable pageable = PageRequest.of(0, 12);
            if (category_selected > 0) {
                pageProduct = productService.findByProduct_NameAndCategory_idContaining(search_input, category_selected,
                        pageable);
            } else {
                pageProduct = productService.findByProduct_NameContaining(search_input, pageable);
            }
            List<Category> listCategories = categoryService.findAll();
            String nameCategory = null;
            if (category_selected == 0) {
                nameCategory = null;
            } else {
                for (Category y : listCategories) {
                    if (y.getId() == category_selected) {
                        nameCategory = y.getCategory_Name();
                    }
                }
            }
            model.addAttribute("pageProduct", pageProduct);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("search_dashboard", "search_dashboard");
            model.addAttribute("search_input", search_input);
            model.addAttribute("nameCategory", nameCategory);
            session.setAttribute("search_input_dashboard", search_input);
            session.setAttribute("category_selected", category_selected);
            return "dashboard-myproducts";
        }
    }

    @GetMapping("/dashboard-myproduct/search/{page}")
    public String DashboardMyproductSearchPage(@PathVariable int page, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        } else {
            String search_input = (String) session.getAttribute("search_input_dashboard");
            Long category_selected = (Long) session.getAttribute("category_selected");
            Page<Product> pageProduct = null;
            Pageable pageable = PageRequest.of(page, 12);
            if (category_selected > 0) {
                pageProduct = productService.findByProduct_NameAndCategory_idContaining(search_input, category_selected,
                        pageable);
            } else {
                pageProduct = productService.findByProduct_NameContaining(search_input, pageable);
            }
            List<Category> listCategories = categoryService.findAll();
            model.addAttribute("pageProduct", pageProduct);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("search_dashboard", "search_dashboard");
            model.addAttribute("search_input", search_input);
            model.addAttribute("category_selected", category_selected);
            session.setAttribute("search_input_dashboard", search_input);
            return "dashboard-myproducts";
        }
    }

    @GetMapping("/dashboard-myproducts/variants/{id}")
    public String viewProductVariants(@PathVariable Long id, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        }

        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            return "redirect:/dashboard-myproducts";
        }

        List<ProductVariant> listVariants = productVariantRepository.findByProductId(id);

        model.addAttribute("product", product);
        model.addAttribute("listVariants", listVariants);

        return "dashboard-my-products-variants";
    }

    @GetMapping("/dashboard-myproducts/delete/{id}")
    @Transactional
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        }

        Product product = productRepository.findById(id).orElse(null);

        if (product != null) {
            for (ProductVariant variant : product.getVariants()) {
                cartRepository.deleteByVariantId(variant.getId());
                orderItemRepository.deleteByVariantId(variant.getId());
            }
            productVariantRepository.deleteAll(product.getVariants());
            productRepository.delete(product);

            redirectAttributes.addFlashAttribute("message", "Xóa sản phẩm thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Sản phẩm không tồn tại!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/dashboard-myproducts";
    }

    @PostMapping("/dashboard-myproducts/variants")
    public String saveProductVariants(
            @RequestParam("product_id") Long productId,
            @RequestParam(value = "variant_id", required = false) Long[] variantIds,
            @RequestParam(value = "variant_color", required = false) String[] colors,
            @RequestParam(value = "variant_size", required = false) String[] sizes,
            @RequestParam(value = "variant_quantity", required = false) int[] quantities,
            @RequestParam(value = "variant_import_price", required = false) String[] importPrices,
            @RequestParam(value = "variant_price", required = false) String[] prices,
            @RequestParam(value = "variant_image", required = false) MultipartFile[] imageFiles,
            RedirectAttributes redirectAttributes) {

        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        }

        Product product = productRepository.findById(productId).orElse(null);

        if (product != null && colors != null && colors.length > 0) {
            for (int i = 0; i < colors.length; i++) {
                ProductVariant variant;

                if (variantIds != null && i < variantIds.length && variantIds[i] > 0) {
                    variant = productVariantRepository.findById(variantIds[i]).orElse(new ProductVariant());
                } else {
                    variant = new ProductVariant();
                }

                variant.setProduct(product);
                variant.setColor(colors[i]);
                variant.setSize(sizes[i]);
                variant.setQuantity(quantities[i]);

                if (importPrices != null && i < importPrices.length && !importPrices[i].trim().isEmpty()) {
                    variant.setImportPrice(new BigDecimal(importPrices[i].trim()));
                }

                if (prices != null && i < prices.length && !prices[i].trim().isEmpty()) {
                    variant.setPrice(new BigDecimal(prices[i].trim()));
                }

                variant = productVariantRepository.save(variant);

                if (imageFiles != null && i < imageFiles.length && !imageFiles[i].isEmpty()) {
                    try {
                        MultipartFile file = imageFiles[i];
                        String link = cloudinaryService.uploadFile(file);

                        if (variant.getProductImage() != null && !variant.getProductImage().isEmpty()) {
                            productImageRepository.deleteAll(variant.getProductImage());
                        }

                        ProductImage img = new ProductImage();
                        img.setUrl_Image(link);
                        img.setProductVariant(variant);
                        productImageRepository.save(img);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            redirectAttributes.addFlashAttribute("message", "Cập nhật biến thể sản phẩm thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        }

        return "redirect:/dashboard-myproducts/variants/" + productId;
    }

    @GetMapping("/dashboard-myproducts/variants/delete/{vId}")
    @Transactional
    public String deleteProductVariant(@PathVariable("vId") Long variantId,
            @RequestParam("product_id") Long productId,
            RedirectAttributes redirectAttributes) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/signin-admin";
        }

        ProductVariant variant = productVariantRepository.findById(variantId).orElse(null);

        if (variant != null) {
            if (variant.getProductImage() != null && !variant.getProductImage().isEmpty()) {
                productImageRepository.deleteAll(variant.getProductImage());
            }

            cartRepository.deleteByVariantId(variantId);
            orderItemRepository.deleteByVariantId(variantId);

            productVariantRepository.delete(variant);

            redirectAttributes.addFlashAttribute("message", "Xóa biến thể sản phẩm thành công!");
            redirectAttributes.addFlashAttribute("messageType", "success");
        } else {
            redirectAttributes.addFlashAttribute("message", "Biến thể không tồn tại!");
            redirectAttributes.addFlashAttribute("messageType", "danger");
        }

        return "redirect:/dashboard-myproducts/variants/" + productId;
    }
}