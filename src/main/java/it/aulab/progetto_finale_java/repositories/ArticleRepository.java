package it.aulab.progetto_finale_java.repositories;

import java.util.List;

import org.springframework.data.repository.ListCrudRepository;

import it.aulab.progetto_finale_java.models.Article;
import it.aulab.progetto_finale_java.models.Category;
import it.aulab.progetto_finale_java.models.User;

public interface ArticleRepository extends ListCrudRepository<Article, Long> {

    List<Article> findByCategory(Category category);
    List<Article> findByUser(User user);

    List<Article> findByIsAcceptedTrue();
    List<Article> findByIsAcceptedFalse();
    List<Article> findByIsAcceptedNull();
}
