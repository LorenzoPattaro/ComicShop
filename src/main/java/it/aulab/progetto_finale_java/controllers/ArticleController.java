package it.aulab.progetto_finale_java.controllers;

import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_java.dtos.ArticleDto;
import it.aulab.progetto_finale_java.dtos.CategoryDto;
import it.aulab.progetto_finale_java.models.Article;
import it.aulab.progetto_finale_java.models.Category;
import it.aulab.progetto_finale_java.services.ArticleService;
import it.aulab.progetto_finale_java.services.CrudService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    @Qualifier("categoryService")
    private CrudService<CategoryDto, Category, Long> categoryService;

    @Autowired
    private ArticleService articleService;

    //rotta per l index
    @GetMapping
    public String articlesIndex(Model viewModel) {
        viewModel.addAttribute("title", "Tutti gli articoli");

        List<ArticleDto> articles = articleService.readAll();
    
        Collections.sort(articles, Comparator.comparing(ArticleDto::getPublishDate).reversed());
        viewModel.addAttribute("articles", articles);
        return "article/articles";
    }


    //Rotta per la creazione di un articolo
    @GetMapping("create")
    public String articleCreate(Model viewModel) {
        viewModel.addAttribute("title", "Crea un articolo");
        viewModel.addAttribute("article", new Article());
        viewModel.addAttribute("categories", categoryService.readAll());
        return "article/create";
    }

    @PostMapping
    public String articleStore(@Valid @ModelAttribute("article") Article article,
                            BindingResult result,
                            RedirectAttributes redirectAttributes,
                            Principal principal,
                            MultipartFile file,
                            Model viewModel) {

        //controllo degli errori con le validazioni
        if (result.hasErrors()) {
            viewModel.addAttribute("title", "Crea un articolo");
            viewModel.addAttribute("article", article);
            viewModel.addAttribute("categories", categoryService.readAll());
            return "article/create";
        }

        articleService.create(article, principal, file);
        redirectAttributes.addFlashAttribute("successMessage", "Articolo aggiunto con successo!");

        return "redirect:/";
    }
}
