package com.grepp.teamnotfound.app.model.vaccination;


import com.grepp.teamnotfound.app.model.vaccination.dto.VaccineDto;
import com.grepp.teamnotfound.app.model.vaccination.entity.Vaccine;
import com.grepp.teamnotfound.app.model.vaccination.repository.VaccineRepository;
import com.grepp.teamnotfound.util.NotFoundException;
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
public class VaccineService {

    private final VaccineRepository vaccineRepository;

    ModelMapper modelMapper = new ModelMapper();

    public List<VaccineDto> findAll() {
        final List<Vaccine> vaccines = vaccineRepository.findAll();

        return vaccines.stream()
                .map(VaccineDto::fromEntity)
                .toList();
    }

    public VaccineDto get(Long vaccineId) {
        return vaccineRepository.findById(vaccineId)
                .map(VaccineDto::fromEntity)
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Long create(VaccineDto vaccineDTO) {
        Vaccine vaccine = modelMapper.map(vaccineDTO, Vaccine.class);
        return vaccineRepository.save(vaccine).getVaccineId();
    }

}
