package WebProject.WebProject.service;

import WebProject.WebProject.entity.Product;
import WebProject.WebProject.entity.ProductVariant;
import WebProject.WebProject.entity.dtos.ChatbotReply;
import WebProject.WebProject.repository.ProductRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ChatbotService {

    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${GROQ_API_KEY}")
    private String apiKey;

    @Value("${GROQ_URL}")
    private String apiUrl;

    @Value("${GROQ_MODEL}")
    private String model;

    public ChatbotService(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public ChatbotReply getChatbotReply(String messageText) {
        try {
            if (messageText == null || messageText.trim().isEmpty()) {
                return new ChatbotReply(
                    "Bạn đang tìm sản phẩm nào tại FashionStore ạ?",
                    Collections.emptyList()
                );
            }

            String text = normalize(messageText);
            BigDecimal maxPrice = extractBudget(text);

            List<Product> allProducts =
                productRepository.findActiveProductsWithVariants(1);

            String categoryKeyword = detectCategory(text);

            String gender = detectGender(text);

            List<Product> filteredProducts = allProducts.stream()
                .filter(p -> matchesCategory(p, categoryKeyword))
                .filter(p -> matchesGender(p, gender))
                .filter(p -> matchesDescription(p, text))
                .filter(p -> hasAvailableVariant(p, text, maxPrice))
                .sorted(Comparator.comparing(
                    (Product p) -> getBestPrice(p, text, maxPrice)
                ))
                .limit(3)
                .collect(Collectors.toList());

            List<Product> upsellProducts = new ArrayList<>();

            if (maxPrice != null && filteredProducts.isEmpty()) {
                upsellProducts = allProducts.stream()
                    .filter(p -> matchesCategory(p, categoryKeyword))
                    .filter(p -> matchesGender(p, gender))
                    .filter(p -> matchesDescription(p, text))
                    .filter(p -> hasAvailableVariant(p, text, null))
                    .filter(p -> {
                        BigDecimal price = getBestPrice(p, text, null);
                        return price.compareTo(maxPrice) > 0
                            && price.compareTo(
                                maxPrice.multiply(new BigDecimal("1.25"))
                            ) <= 0;
                    })
                    .sorted(Comparator.comparing(
                        (Product p) -> getBestPrice(p, text, null)
                    ))
                    .limit(2)
                    .collect(Collectors.toList());
            }

            if (filteredProducts.isEmpty()
                    && upsellProducts.isEmpty()
                    && !isSmallTalk(text)) {

                filteredProducts = allProducts.stream()
                    .filter(p -> matchesCategory(p, categoryKeyword))
                    .filter(p -> matchesGender(p, gender))
                    .filter(p -> hasAvailableVariant(p, text, maxPrice))
                    .limit(3)
                    .collect(Collectors.toList());
            }

            String productContext = buildProductContext(
                filteredProducts, upsellProducts, maxPrice
            );

            String reply = callGroq(messageText, productContext);

            List<Long> ids = new ArrayList<>();

            filteredProducts.forEach(p -> ids.add(p.getId()));
            upsellProducts.forEach(p -> ids.add(p.getId()));

            return new ChatbotReply(reply, ids);

        } catch (Exception e) {
            e.printStackTrace();

            return new ChatbotReply(
                "Xin lỗi bạn, chatbot FashionStore đang gặp sự cố. "
                + "Bạn vui lòng thử lại sau nhé!",
                Collections.emptyList()
            );
        }
    }

    private String normalize(String text) {
        String result = text.toLowerCase(Locale.ROOT).trim();
        result = Normalizer.normalize(result, Normalizer.Form.NFD);
        result = result.replaceAll("\\p{M}", "");
        return result.replace('đ', 'd');
    }

    private BigDecimal extractBudget(String text) {
        Pattern pattern = Pattern.compile(
            "(\\d+(?:[.,]\\d+)?)\\s*(triệu|trieu|tr|nghìn|nghin|ngàn|ngan|k|đồng|dong|vnd|đ)?\\b"
        );

        Matcher matcher = pattern.matcher(text);
        BigDecimal maxPrice = null;

        while (matcher.find()) {
            String number = matcher.group(1);
            String unit = matcher.group(2);

            if (unit == null) {
                continue;
            }

            BigDecimal value = new BigDecimal(number.replace(",", "."));

            if (unit.equals("triệu") || unit.equals("trieu")
                    || unit.equals("tr")) {
                value = value.multiply(new BigDecimal("1000000"));
            } else if (unit.equals("k") || unit.equals("nghìn")
                    || unit.equals("nghin") || unit.equals("ngàn")
                    || unit.equals("ngan")) {
                value = value.multiply(new BigDecimal("1000"));
            }

            if (maxPrice == null || value.compareTo(maxPrice) > 0) {
                maxPrice = value;
            }
        }

        if (maxPrice == null) {
            Matcher plainNumber = Pattern
                .compile("\\b(\\d{5,})\\b")
                .matcher(text);

            if (plainNumber.find()) {
                maxPrice = new BigDecimal(plainNumber.group(1));
            }
        }

        return maxPrice;
    }

    private String detectCategory(String text) {
        if (text.contains("ao khoac") || text.contains("jacket")
                || text.contains("hoodie")) {
            return "ao khoac";
        }

        if (text.contains("quan jean") || text.contains("jeans")) {
            return "quan jean";
        }

        if (text.contains("quan")) {
            return "quan";
        }

        if (text.contains("vay") || text.contains("dam")) {
            return "vay";
        }

        if (text.contains("ao thun") || text.contains("t shirt")
                || text.contains("tshirt")) {
            return "ao thun";
        }

        if (text.contains("ao so mi") || text.contains("shirt")) {
            return "ao so mi";
        }

        if (text.contains("ao")) {
            return "ao";
        }

        return null;
    }

    private String detectGender(String text) {
        if (text.contains("nam")) {
            return "nam";
        }

        if (text.contains("nu") || text.contains("nữ")) {
            return "nu";
        }

        return null;
    }

    private boolean matchesCategory(Product p, String keyword) {
        if (keyword == null) {
            return true;
        }

        if (p.getCategory() == null
                || p.getCategory().getCategory_Name() == null) {
            return false;
        }

        return normalize(p.getCategory().getCategory_Name())
            .contains(keyword);
    }

    private boolean matchesGender(Product p, String gender) {
        if (gender == null) {
            return true;
        }

        if (p.getGender() == null) {
            return false;
        }

        return normalize(p.getGender()).contains(gender);
    }

    private boolean matchesDescription(Product p, String text) {
        return p.getProductName() != null
            || p.getDescription() != null;
    }

    private boolean hasAvailableVariant(
            Product p, String text, BigDecimal maxPrice) {

        if (p.getVariants() == null) {
            return false;
        }

        return p.getVariants().stream().anyMatch(v -> {
            if (v.getQuantity() <= 0 || v.getPrice() == null) {
                return false;
            }

            if (maxPrice != null
                    && v.getPrice().compareTo(maxPrice) > 0) {
                return false;
            }

            if (text.contains("size s") && !equalsValue(v.getSize(), "S")) {
                return false;
            }
            if (text.contains("size m") && !equalsValue(v.getSize(), "M")) {
                return false;
            }
            if (text.contains("size l") && !equalsValue(v.getSize(), "L")) {
                return false;
            }
            if (text.contains("size xl")
                    && !equalsValue(v.getSize(), "XL")) {
                return false;
            }

            String color = v.getColor() == null
                ? "" : normalize(v.getColor());

            if (text.contains("mau den") && !color.contains("den")) {
                return false;
            }
            if (text.contains("mau trang") && !color.contains("trang")) {
                return false;
            }
            if (text.contains("mau do") && !color.contains("do")) {
                return false;
            }
            if (text.contains("mau xanh") && !color.contains("xanh")) {
                return false;
            }

            return true;
        });
    }

    private boolean equalsValue(String actual, String expected) {
        return actual != null && actual.trim().equalsIgnoreCase(expected);
    }

    private BigDecimal getBestPrice(
            Product p, String text, BigDecimal maxPrice) {

        if (p.getVariants() == null) {
            return BigDecimal.valueOf(Long.MAX_VALUE);
        }

        return p.getVariants().stream()
            .filter(v -> v.getQuantity() > 0 && v.getPrice() != null)
            .filter(v -> maxPrice == null
                || v.getPrice().compareTo(maxPrice) <= 0)
            .filter(v -> matchesVariant(v, text))
            .map(ProductVariant::getPrice)
            .min(BigDecimal::compareTo)
            .orElse(BigDecimal.valueOf(Long.MAX_VALUE));
    }

    private boolean matchesVariant(ProductVariant v, String text) {
        String size = normalize(v.getSize() == null ? "" : v.getSize());
        String color = normalize(v.getColor() == null ? "" : v.getColor());

        if (text.contains("size xl") && !size.equals("xl")) return false;
        if (text.contains("size s") && !size.equals("s")) return false;
        if (text.contains("size m") && !size.equals("m")) return false;
        if (text.contains("size l") && !size.equals("l")) return false;

        if (text.contains("mau den") && !color.contains("den")) return false;
        if (text.contains("mau trang") && !color.contains("trang")) return false;
        if (text.contains("mau do") && !color.contains("do")) return false;
        if (text.contains("mau xanh") && !color.contains("xanh")) return false;

        return true;
    }

    private boolean isSmallTalk(String text) {
        return text.matches(
            "^(xin chao|chao|hello|hi|hey|cam on|thank you|bye|tam biet)$"
        );
    }

    private String formatPrice(BigDecimal price) {
        return price == null ? "Liên hệ"
            : String.format("%,.0f VNĐ", price);
    }

    private String buildProductContext(
            List<Product> products,
            List<Product> upsellProducts,
            BigDecimal maxPrice) {

        StringBuilder sb = new StringBuilder();

        sb.append("CỬA HÀNG: FashionStore\n");
        sb.append("Ngân sách khách: ")
            .append(maxPrice == null ? "Chưa xác định"
                : formatPrice(maxPrice))
            .append("\n\n");

        appendProducts(sb, "SẢN PHẨM PHÙ HỢP", products);

        if (!upsellProducts.isEmpty()) {
            appendProducts(
                sb, "SẢN PHẨM CÓ THỂ NÂNG NGÂN SÁCH", upsellProducts
            );
        }

        if (products.isEmpty() && upsellProducts.isEmpty()) {
            sb.append("Không tìm thấy sản phẩm phù hợp trong dữ liệu hiện có.\n");
        }

        return sb.toString();
    }


    private void appendProducts(
            StringBuilder sb, String heading, List<Product> products) {
        sb.append(heading).append(":\n");

        for (Product p : products) {
            List<ProductVariant> availableVariants =
                    p.getVariants() == null
                            ? Collections.emptyList()
                            : p.getVariants().stream()
                            .filter(v -> v.getQuantity() > 0)
                            .filter(v -> v.getPrice() != null)
                            .collect(Collectors.toList());

            if (availableVariants.isEmpty()) {
                continue;
            }

            BigDecimal bestPrice = availableVariants.stream()
                    .map(ProductVariant::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(null);

            sb.append("- ID nội bộ: ").append(p.getId())
                    .append(" | Tên: ").append(p.getProductName())
                    .append(" | Danh mục: ")
                    .append(p.getCategory() == null
                            ? "Chưa phân loại"
                            : p.getCategory().getCategory_Name())
                    .append(" | Giới tính: ")
                    .append(p.getGender() == null ? "Chưa xác định" : p.getGender())
                    .append(" | Giá thấp nhất của biến thể còn hàng: ")
                    .append(formatPrice(bestPrice))
                    .append(" | Mô tả: ")
                    .append(p.getDescription() == null
                            ? "Chưa có mô tả"
                            : p.getDescription())
                    .append("\n");

            for (ProductVariant v : availableVariants) {
                sb.append("  - Size: ")
                        .append(v.getSize() == null ? "Chưa xác định" : v.getSize())
                        .append(", màu: ")
                        .append(v.getColor() == null ? "Chưa xác định" : v.getColor())
                        .append(", giá: ")
                        .append(formatPrice(v.getPrice()))
                        .append(", tồn kho: ")
                        .append(v.getQuantity())
                        .append("\n");
            }
        }

        sb.append("\n");
    }


    private String callGroq(String message, String productContext)
            throws Exception {


        String systemPrompt =
                "Bạn là FashionStore AI, trợ lý tư vấn thời trang của cửa hàng FashionStore. "
                        + "Luôn trả lời bằng tiếng Việt, thân thiện, tự nhiên và dễ đọc. "
                        + "Chỉ sử dụng dữ liệu sản phẩm được cung cấp trong ngữ cảnh. "
                        + "Không tự bịa tên sản phẩm, giá, màu sắc, size hoặc số lượng tồn kho. "
                        + "Ưu tiên sản phẩm phù hợp ngân sách, giới tính, danh mục, màu sắc và size khách yêu cầu. "
                        + "Chỉ giới thiệu sản phẩm còn hàng và phù hợp với yêu cầu. "
                        + "Nếu không tìm được sản phẩm phù hợp, hãy nói rõ và hỏi khách có muốn "
                        + "tham khảo sản phẩm khác hay điều chỉnh ngân sách không. "
                        + "Không sử dụng bảng Markdown, ký tự | hoặc định dạng bảng. "
                        + "Không hiển thị ID sản phẩm cho khách hàng. "
                        + "Khi giới thiệu sản phẩm, trình bày mỗi sản phẩm thành một mục riêng theo mẫu:\n"
                        + "Tên sản phẩm\n"
                        + "Giá: [giá sản phẩm]\n"
                        + "Mô tả ngắn: [thông tin có trong dữ liệu]\n"
                        + "Không bắt buộc phải có mô tả nếu dữ liệu không cung cấp. "
                        + "Mỗi câu trả lời chỉ nên giới thiệu tối đa 3 sản phẩm. "
                        + "Có thể sử dụng emoji vừa phải để tạo cảm giác thân thiện. "
                        + "Kết thúc bằng một câu hỏi ngắn giúp khách chọn sản phẩm hoặc cung cấp "
                        + "thêm nhu cầu. Không cảm ơn khách hàng ở mọi câu trả lời một cách máy móc.\n\n"
                        + productContext;


        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages",
            Arrays.asList(systemMessage, userMessage));
        requestBody.put("temperature", 0.3);
        requestBody.put("max_tokens", 600);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> request =
            new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            apiUrl + "/chat/completions",
            request,
            String.class
        );

        JsonNode root = objectMapper.readTree(response.getBody());

        return root.path("choices").path(0)
            .path("message").path("content")
            .asText("Xin lỗi, mình chưa thể trả lời câu hỏi này.");
    }
}