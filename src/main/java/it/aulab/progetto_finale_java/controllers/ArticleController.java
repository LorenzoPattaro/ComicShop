package it.aulab.progetto_finale_java.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.aulab.progetto_finale_java.dtos.ArticleDto;
import it.aulab.progetto_finale_java.dtos.CategoryDto;
import it.aulab.progetto_finale_java.models.Article;
import it.aulab.progetto_finale_java.models.Category;
import it.aulab.progetto_finale_java.repositories.ArticleRepository;
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

    @Autowired
    private ArticleRepository articleRepository;;

    @Autowired
    private ModelMapper modelMapper;

    //rotta per l index
    @GetMapping
    public String articlesIndex(Model viewModel) {
        viewModel.addAttribute("title", "Tutti gli articoli");

        List<ArticleDto> articles = new ArrayList<ArticleDto>();
        for(Article article: articleRepository.findByIsAcceptedTrue()) {
            articles.add(modelMapper.map(article, ArticleDto.class));
        }
    
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

    //rotta pagina dettaglio
    @GetMapping("detail/{id}")
    public String detailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Article detail");
        viewModel.addAttribute("article", articleService.read(id));
        return "article/detail";
    }

    //rotta dettaglio di un articolo per il revisore
    @GetMapping("revisor/detail/{id}")
    public String revisorDetailArticle(@PathVariable("id") Long id, Model viewModel) {
        viewModel.addAttribute("title", "Article detail");
        viewModel.addAttribute("article", articleService.read(id));
        return "revisor/detail";
    }

    //rotta dedicata all'azione del revisore
    @PostMapping("/accept")
    public String articleSetAccepted(@RequestParam("action") String action, @RequestParam("articleId") Long articleId, RedirectAttributes redirectAttributes) {
        
        if(action.equals("accept")) {
            articleService.setIsAccepted(true, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Articolo accettato!");
        } else if(action.equals("reject")) {
            articleService.setIsAccepted(false, articleId);
            redirectAttributes.addFlashAttribute("resultMessage", "Articolo rifiutato!");
        } else {
            redirectAttributes.addFlashAttribute("resultMessage", "Azione non corretta!");
        }
        return "redirect:/revisor/dashboard";
    }

    //rotta di ricerca di un articolo
    @GetMapping("/search")
    public String articleSearch(@Param("keyword") String keyword, Model viewModel) {
        viewModel.addAttribute("title", "Tutti gli articoli trovati");

        List<ArticleDto> articles = articleService.search(keyword);
        List<ArticleDto> acceptedArticles = articles.stream().filter(article -> Boolean.TRUE.equals(article.getIsAccepted())).collect(Collectors.toList());

        viewModel.addAttribute("articles", acceptedArticles);

        return "article/articles";
    }
}
