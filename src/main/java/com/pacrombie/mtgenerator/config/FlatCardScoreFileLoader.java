package com.pacrombie.mtgenerator.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class FlatCardScoreFileLoader {

    private static final String FILE_NAME = "FlatCardScore.csv";

    public Map<String, Integer> loadCardScores() {
        Map<String, Integer> scores = new HashMap<>();
        Resource resource = new ClassPathResource(FILE_NAME);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid card score line: " + line);
                }

                String cardName = normalize(parts[0]);
                int score = Integer.parseInt(parts[1].trim());

                scores.put(cardName, score);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load card score file", e);
        }

        return scores;
    }

    private String normalize(String name) {
        return name
                .replace("\"", "")
                .trim()
                .toLowerCase();
    }
}
