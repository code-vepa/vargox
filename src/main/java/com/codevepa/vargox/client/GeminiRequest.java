package com.codevepa.vargox.client;

import java.util.List;

public class GeminiRequest {

    private List<Content> contents;

    public GeminiRequest(String promptText) {
        this.contents = List.of(new Content(List.of(new Part(promptText))));
    }

    public List<Content> getContents() {
        return contents;
    }

    public static class Content {
        private List<Part> parts;

        public Content(List<Part> parts) {
            this.parts = parts;
        }

        public List<Part> getParts() {
            return parts;
        }
    }

    public static class Part {
        private String text;

        public Part(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
