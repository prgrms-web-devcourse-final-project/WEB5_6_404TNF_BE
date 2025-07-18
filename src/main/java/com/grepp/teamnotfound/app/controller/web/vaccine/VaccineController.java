package com.grepp.teamnotfound.app.controller.web.vaccine;


import com.grepp.teamnotfound.app.model.vaccination.VaccineService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vaccines")
public class VaccineController {

    private final VaccineService vaccineService;

    public VaccineController(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("vaccines", vaccineService.findAll());
        return "vaccine/list";
    }
}