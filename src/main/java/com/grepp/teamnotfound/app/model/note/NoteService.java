package com.grepp.teamnotfound.app.model.note;

import com.grepp.teamnotfound.app.model.note.dto.NoteDto;
import com.grepp.teamnotfound.app.model.note.entity.Note;
import com.grepp.teamnotfound.app.model.note.repository.NoteRepository;
import com.grepp.teamnotfound.infra.error.exception.StructuredDataException;
import com.grepp.teamnotfound.infra.error.exception.code.NoteErrorCode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final ModelMapper modelMapper;
    private final NoteRepository noteRepository;

    // 관찰노트 정보 저장
    @Transactional
    public void createNote(NoteDto noteDto){
        Note note = modelMapper.map(noteDto, Note.class);
        noteRepository.save(note);
    }
    
    // 기존 생활기록 데이터 있는지 확인
    @Transactional(readOnly = true)
    public boolean existsLifeRecord(Long petId, LocalDate date) {
        return noteRepository.existsByPetIdAndRecordedAt(petId, date);
    }

    // 관찰노트 정보 조회
    @Transactional(readOnly = true)
    public String getNote(Long petId, LocalDate recordedAt){
        Note note = noteRepository.findNote(petId, recordedAt)
                .orElseThrow(() -> new StructuredDataException(NoteErrorCode.NOTE_NOT_FOUND));

        return note.getContent() == null ? "" : note.getContent();
    }

    // 생활기록 정보 수정
    @Transactional
    public void updateNote(NoteDto noteDto){
        Note note = modelMapper.map(noteDto, Note.class);
        note.setUpdatedAt(OffsetDateTime.now());
        noteRepository.save(note);
    }

    // 생활기록 정보 삭제
    @Transactional
    public void deleteNote(Long petId, LocalDate recordedAt) {
        noteRepository.delete(petId, recordedAt);
    }

}
