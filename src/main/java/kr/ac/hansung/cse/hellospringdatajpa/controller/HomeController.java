package kr.ac.hansung.cse.hellospringdatajpa.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    @GetMapping({""}) // products 또는 /products/ 둘 다 매핑
    public String home() {
        log.info("home");
        return "index";
    }
}
