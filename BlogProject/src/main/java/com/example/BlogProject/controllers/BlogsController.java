/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.BlogProject.controllers;

import com.example.BlogProject.models.Author;
import com.example.BlogProject.models.Blog;
import com.example.BlogProject.models.Tag;
import com.example.BlogProject.models.UserError;
import com.example.BlogProject.security.CustomUserDetails;
import com.example.BlogProject.service.ServiceLayer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/** Blog Controller
 *
 * @author Gage
 */
@Controller
public class BlogsController {
    
    @Autowired
    ServiceLayer service;
    
    List<UserError> errors = new ArrayList();
    
    /**
     * 
     * @param model
     * @return 
     */
    @GetMapping("/")
    public String index(Model model){
        List<Blog> blist = service.getBlogs();

        model.addAttribute("blogs", blist);
        model.addAttribute("tags", service.getTags());
        model.addAttribute("errors", errors);
        
        Author author = service.getAuthorEmail("admin@admin.com");
        if(author == null) {
            Author admin = new Author();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode("123456");

            admin.setAuthorEmail("admin@admin.com");
            admin.setAuthorAdmin(true);
            admin.setAuthorName("admin");
            admin.setAuthorPassword(encodedPassword);
            service.saveAuthor(author);
        }
        
        return "blogs";
    }
    
    /**
     * 
     * @param model
     * @return 
     */
    @GetMapping("/createBlog")
    public String createBlog(Model model){
        return "createBlog";
    }
    
    /**
     * 
     * @param id
     * @param model
     * @return 
     */
    @GetMapping("/viewBlog")
    public String viewBlog(Integer id, Model model){
        Blog blog = service.getBlog(id);
        model.addAttribute("blog", blog);
        
        return "viewBlog";
    }
    
    /**
     * 
     * @param id
     * @param model
     * @return 
     */
    @GetMapping("/viewBlogByTag")
    public String viewBlogByTag(Integer id, Model model){
        Tag tag = service.getTag(id);
        model.addAttribute("tag", tag);
        
        return "viewBlogByTag";
    }
    
        /** adds a tag to a blog post
     * 
     * @param tagName
     * @param model
     * @return 
     */
    @PostMapping("/tag")
    public String addTagToBlog(String tagName, Model model){
        errors.clear();
        
        Tag t = service.getTagName(tagName);
        if(t != null){
            model.addAttribute("tag", t);
            return "viewBlogByTag";
        }
        UserError e = new UserError();
        e.setErrorName("Tag Name Error");
        e.setErrorDescription("Tag Name Incorrect or does not exist");
        errors.add(e);
        
        return "redirect:/";
    }
}
