package com.voroshilov.task;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * For implement this task focus on clear code, and make this solution as simple readable as possible
 * Don't worry about performance, concurrency, etc
 * You can use in Memory collection for sore data
 * <p>
 * Please, don't change class name, and signature for methods save, search, findById
 * Implementations should be in a single class
 * This class could be auto tested
 */
public class DocumentManager {

    private final Map<String, Document> documentRepo = new HashMap<>();

    /**
     * Implementation of this method should upsert the document to your storage
     * And generate unique id if it does not exist, don't change [created] field
     *
     * @param document - document content and author data
     * @return saved document
     */
    public Document save(Document document) {
        if (Objects.isNull(document.getId()) || document.getId().isBlank() || document.getId().isEmpty()) {
            document.setId(UUID.randomUUID().toString());
        }

        return documentRepo.put(document.getId(), document);
    }

    /**
     * Implementation this method should find documents which match with request
     *
     * @param request - search request, each field could be null
     * @return list matched documents
     */
    public List<Document> search(SearchRequest request) {
        return documentRepo.values().stream()
                .filter(document -> {
                    if (Objects.nonNull(request.getTitlePrefixes()) && !request.getTitlePrefixes().isEmpty()) {
                        return request.getTitlePrefixes().stream()
                                .anyMatch(prefix -> Objects.nonNull(document.getTitle()) && document.getTitle().startsWith(prefix));
                    } else return true;
                })
                .filter(document -> {
                    if (Objects.nonNull(request.getContainsContents()) && !request.getContainsContents().isEmpty()) {
                        return request.getContainsContents().stream()
                                .anyMatch(content -> Objects.nonNull(document.getContent()) && document.getContent().contains(content));
                    } else return true;
                })
                .filter(document -> {
                    if (Objects.nonNull(request.getAuthorIds()) && !request.getAuthorIds().isEmpty()) {
                        return request.getAuthorIds().contains(document.getAuthor().getId());
                    } return true;
                })
                .filter(document -> {
                    if (Objects.nonNull(request.getCreatedFrom())) {
                        return document.getCreated().isAfter(request.getCreatedFrom());
                    } else return true;
                })
                .filter(document -> {
                    if (Objects.nonNull(request.getCreatedTo())) {
                        return document.getCreated().isBefore(request.getCreatedTo());
                    } else return true;
                })
                .toList();
    }

    /**
     * Implementation this method should find document by id
     *
     * @param id - document id
     * @return optional document
     */
    public Optional<Document> findById(String id) {
        if (id == null || id.isBlank() || id.isEmpty()) throw new IllegalArgumentException("id is null or empty");
        return Optional.ofNullable(documentRepo.get(id));
    }

    @Data
    @Builder
    public static class SearchRequest {
        private List<String> titlePrefixes;
        private List<String> containsContents;
        private List<String> authorIds;
        private Instant createdFrom;
        private Instant createdTo;
    }

    @Data
    @Builder
    public static class Document {
        private String id;
        private String title;
        private String content;
        private Author author;
        private Instant created;
    }

    @Data
    @Builder
    public static class Author {
        private String id;
        private String name;
    }
}