package net.achraf.ensetbot.agents;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;

import java.util.Arrays;

@Component
public class AiAgent {
    private ChatClient chatClient;

    public AiAgent(ChatClient.Builder builder, ChatMemory chatMemory, ToolCallbackProvider tools) {
        Arrays.stream(tools.getToolCallbacks()).forEach(toolCallback -> {
            System.out.println("----------");
            System.out.println(toolCallback.getToolDefinition());
            System.out.println("----------");
        });
        this.chatClient = builder
                .defaultSystem("""
                    Vous un assistant qui se charge de répondre aux question
                    de l'utilisateur en fonction du contexte fourni.
                    Si aucun contexte n'est fourni , répond avec JE NE SAIS PAS
                """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultToolCallbacks(tools)
                .build();
    }

    public Flux<String> askAgent(String query){
        return chatClient.prompt()
                .user(query)
                .stream().content();

    }
}
