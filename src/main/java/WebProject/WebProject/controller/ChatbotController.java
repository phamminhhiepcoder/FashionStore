package WebProject.WebProject.controller;

import WebProject.WebProject.entity.dtos.ChatbotReply;
import WebProject.WebProject.service.ChatbotService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping("/reply")
    public ChatbotReply reply(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        return chatbotService.getChatbotReply(message);
    }
}