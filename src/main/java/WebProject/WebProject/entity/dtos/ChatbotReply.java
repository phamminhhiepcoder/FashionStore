package WebProject.WebProject.entity.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ChatbotReply {
    private String reply;
    private List<Long> suggestedProducts;

    public ChatbotReply(String reply, List<Long> suggestedProducts) {
        this.reply = reply;
        this.suggestedProducts = suggestedProducts;
    }

}