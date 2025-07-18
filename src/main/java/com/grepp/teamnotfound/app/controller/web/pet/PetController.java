package com.grepp.teamnotfound.app.controller.web.pet;

import com.grepp.teamnotfound.app.controller.api.mypage.payload.PetWriteRequest;
import com.grepp.teamnotfound.app.model.auth.domain.Principal;
import com.grepp.teamnotfound.app.model.pet.PetService;
import com.grepp.teamnotfound.app.model.pet.dto.PetDto;
import com.grepp.teamnotfound.app.model.user.entity.User;
import com.grepp.teamnotfound.app.model.user.repository.UserRepository;
import com.grepp.teamnotfound.util.CustomCollectors;
import com.grepp.teamnotfound.util.WebUtils;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;
    private final UserRepository userRepository;

    @ModelAttribute
    public void prepareContext(final Model model) {
        model.addAttribute("userValues", userRepository.findAll(Sort.by("userId"))
            .stream()
            .collect(CustomCollectors.toSortedMap(User::getUserId, User::getEmail)));
    }

    @GetMapping
    public String list(final Model model) {
        model.addAttribute("pets", petService.findAll());
        return "pet/list";
    }

    @GetMapping("/add")
    public String add(@ModelAttribute("pet") final PetDto petDTO, Model model) {
        List<User> users = userRepository.findAll();

        model.addAttribute("users", users);

        return "pet/add";
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String add(
        @ModelAttribute("pet") @Valid PetWriteRequest request,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        @AuthenticationPrincipal Principal principal
    ) {
        if (bindingResult.hasErrors()) {
            return "pet/add";
        }

        Long userId = principal.getUserId();

        petService.create(userId, request, null);
        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pet.create.success"));
        return "redirect:/pets";
    }

//    @GetMapping("/edit/{petId}")
//    public String edit(@PathVariable(name = "petId") final Long petId, final Model model) {
//        model.addAttribute("pet", petService.get(petId));
//        return "pet/edit";
//    }
//
//    @PostMapping("/edit/{petId}")
//    public String edit(@PathVariable(name = "petId") final Long petId,
//        @ModelAttribute("pet") @Valid final PetDTO petDTO, final BindingResult bindingResult,
//        final RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            return "pet/edit";
//        }
//        petService.update(petId, petDTO);
//        redirectAttributes.addFlashAttribute(WebUtils.MSG_SUCCESS, WebUtils.getMessage("pet.update.success"));
//        return "redirect:/pets";
//    }
//
//    @PostMapping("/delete/{petId}")
//    public String delete(@PathVariable(name = "petId") final Long petId,
//        final RedirectAttributes redirectAttributes) {
//        final ReferencedWarning referencedWarning = petService.getReferencedWarning(petId);
//        if (referencedWarning != null) {
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_ERROR,
//                WebUtils.getMessage(referencedWarning.getKey(), referencedWarning.getParams().toArray()));
//        } else {
//            petService.delete(petId);
//            redirectAttributes.addFlashAttribute(WebUtils.MSG_INFO, WebUtils.getMessage("pet.delete.success"));
//        }
//        return "redirect:/pets";
//    }

}
