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
public class TagCardScoreFileLoader {

    private static final String FILE_NAME = "TagCardScores.csv";

    public Map<String, Integer> loadTagScores() {
        Map<String, Integer> scores = new HashMap<>();
        Resource resource = new ClassPathResource(FILE_NAME);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid tag score line: " + line);
                }

                String tag = normalize(parts[0]);
                int score = Integer.parseInt(parts[1].trim());

                if (scores.put(tag, score) != null) {
                    throw new IllegalArgumentException("Duplicate tag score entry: " + tag);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load tag score file", e);
        }

        return scores;
    }

    private String normalize(String tag) {
        return tag
                .replace("\"", "")
                .trim()
                .toLowerCase();
    }
}
