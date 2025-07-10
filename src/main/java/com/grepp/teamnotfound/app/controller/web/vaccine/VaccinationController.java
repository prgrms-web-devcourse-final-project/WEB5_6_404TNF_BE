package com.grepp.teamnotfound.app.controller.web.vaccine;


import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.pet.repository.PetRepository;
import com.grepp.teamnotfound.app.model.vaccination.VaccinationService;
import com.grepp.teamnotfound.app.model.vaccination.dto.VaccinationDto;
import com.grepp.teamnotfound.app.model.vaccination.entity.Vaccine;
import com.grepp.teamnotfound.app.model.vaccination.repository.VaccineRepository;
import com.grepp.teamnotfound.util.CustomCollectors;
import com.grepp.teamnotfound.util.WebUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/vaccinations")
public class VaccinationController {

    private final VaccinationService vaccinationService;
    private final VaccineRepository vaccineRepository;
    private final PetRepository petRepository;

    @ModelAttribute
    public void prepareContext(Model model) {
        model.addAttribute("petValues", petRepository.findAll(Sort.by("petId"))
            .stream()
            .collect(CustomCollectors.toSortedMap(Pet::getPetId, Pet::getName)));

        model.addAttribute("vaccineValues", vaccineRepository.findAll(Sort.by("vaccineId"))
            .stream()
            .collect(CustomCollectors.toSortedMap(Vaccine::getVaccineId, Vaccine::getName)));
    }


    @GetMapping
    public String list(Model model) {
        model.addAttribute("vaccinations", vaccinationService.findAll());
        return "vaccination/list";
    }

    @GetMapping("/add")
    public String add(
        @ModelAttribute("vaccination") VaccinationDto vaccinationDTO,
        Model model
    ) {
        List<Vaccine> vaccines = vaccineRepository.findAll();
        List<Pet> pets = petRepository.findAll();

        model.addAttribute("vaccines", vaccines);
        model.addAttribute("pets", pets);

        return "vaccination/add";
    }

    @PostMapping("/add")
    public String add(
        @ModelAttribute("vaccination") @Valid VaccinationDto vaccinationDTO,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "vaccination/add";
        }
        vaccinationService.create(vaccinationDTO);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("vaccination.create.success"));
        return "redirect:/vaccinations";
    }

    @GetMapping("/edit/{vaccinationId}")
    public String edit(
        @PathVariable(name = "vaccinationId") Long vaccinationId,
        Model model
    ) {
        model.addAttribute("vaccination", vaccinationService.get(vaccinationId));
        return "vaccination/edit";
    }

}
