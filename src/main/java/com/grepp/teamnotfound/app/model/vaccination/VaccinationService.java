package com.grepp.teamnotfound.app.model.vaccination;


import com.grepp.teamnotfound.app.controller.api.mypage.payload.VaccinatedItem;
import com.grepp.teamnotfound.app.model.pet.entity.Pet;
import com.grepp.teamnotfound.app.model.vaccination.dto.VaccinationDto;
import com.grepp.teamnotfound.app.model.vaccination.entity.Vaccination;
import com.grepp.teamnotfound.app.model.vaccination.entity.Vaccine;
import com.grepp.teamnotfound.app.model.vaccination.repository.VaccinationRepository;
import com.grepp.teamnotfound.app.model.vaccination.repository.VaccineRepository;
import com.grepp.teamnotfound.infra.error.exception.BusinessException;
import com.grepp.teamnotfound.infra.error.exception.code.VaccinationErrorCode;
import com.grepp.teamnotfound.util.NotFoundException;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final VaccineRepository vaccineRepository;

    ModelMapper modelMapper = new ModelMapper();

    public List<VaccinationDto> findAll() {
        List<Vaccination> vaccinations = vaccinationRepository.findAll();

        return vaccinations.stream()
            .map(VaccinationDto::fromEntity)
            .toList();
    }

    public VaccinationDto get(Long vaccinationId) {
        return vaccinationRepository.findById(vaccinationId)
            .map(VaccinationDto::fromEntity)
            .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Long create(VaccinationDto vaccinationDTO) {
        Vaccination vaccination = modelMapper.map(vaccinationDTO, Vaccination.class);

        vaccinationRepository.save(vaccination);

        return vaccination.getVaccinationId();
    }
    @Transactional
    public void savePetVaccinations(Pet pet, List<? extends VaccinatedItem> vaccinatedItems) {
        if (vaccinatedItems == null || vaccinatedItems.isEmpty()) {
            return;
        }

        for (VaccinatedItem item : vaccinatedItems) {
            Vaccination vaccination = modelMapper.map(item, Vaccination.class);
            vaccination.setPet(pet);

            Vaccine vaccine = vaccineRepository.findById(item.getVaccineId())
                .orElseThrow(() -> new BusinessException(VaccinationErrorCode.VACCINE_NOT_FOUND));
            vaccination.setVaccine(vaccine);

            vaccinationRepository.save(vaccination);
        }
    }

    @Transactional
    public void delete(Long vaccinationId) {
        vaccinationRepository.deleteById(vaccinationId);
    }
    @Transactional
    public void softDelete(Long petId) {
        vaccinationRepository.softDelete(petId, OffsetDateTime.now());
    }

}
