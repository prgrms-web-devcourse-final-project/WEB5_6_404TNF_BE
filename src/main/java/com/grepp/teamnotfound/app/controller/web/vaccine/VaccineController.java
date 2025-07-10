package com.grepp.teamnotfound.app.controller.web.vaccine;


import com.grepp.teamnotfound.app.model.vaccination.VaccineService;
import com.grepp.teamnotfound.app.model.vaccination.dto.VaccineDto;
import com.grepp.teamnotfound.util.WebUtils;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/add")
    public String add(@ModelAttribute("vaccine") VaccineDto vaccineDTO) {
        return "vaccine/add";
    }

    @PostMapping("/add")
    public String add(
        @ModelAttribute("vaccine") @Valid VaccineDto vaccineDTO,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "vaccine/add";
        }
        vaccineService.create(vaccineDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("vaccine.create.success"));
        return "redirect:/vaccines";
    }
}
