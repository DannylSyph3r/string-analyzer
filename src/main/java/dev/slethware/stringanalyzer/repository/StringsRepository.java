package dev.slethware.stringanalyzer.repository;

import dev.slethware.stringanalyzer.models.entity.Strings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StringsRepository extends JpaRepository<Strings, String> {

    Optional<Strings> findByValue(String value);

    @Query("SELECT s FROM Strings s WHERE " +
            "(:isPalindrome IS NULL OR s.isPalindrome = :isPalindrome) AND " +
            "(:minLength IS NULL OR s.length >= :minLength) AND " +
            "(:maxLength IS NULL OR s.length <= :maxLength) AND " +
            "(:wordCount IS NULL OR s.wordCount = :wordCount)")
    List<Strings> findByFilters(
            @Param("isPalindrome") Boolean isPalindrome,
            @Param("minLength") Integer minLength,
            @Param("maxLength") Integer maxLength,
            @Param("wordCount") Integer wordCount
    );
}