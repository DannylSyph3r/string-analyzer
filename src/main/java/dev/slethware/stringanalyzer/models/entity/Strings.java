package dev.slethware.stringanalyzer.models.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "strings")
@Getter
@Setter
public class Strings {

    @Id
    @Column(name = "id", length = 64)
    private java.lang.String id; // SHA-256 hash serves as PK

    @Column(name = "string_value", nullable = false)
    private java.lang.String value;

    private Integer length;

    @Column(name = "is_palindrome")
    private Boolean isPalindrome;

    @Column(name = "unique_characters")
    private Integer uniqueCharacters;

    @Column(name = "word_count")
    private Integer wordCount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "character_frequency", joinColumns = @JoinColumn(name = "string_id"))
    @MapKeyColumn(name = "character")
    @Column(name = "frequency")
    private Map<java.lang.String, Integer> characterFrequencyMap;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}