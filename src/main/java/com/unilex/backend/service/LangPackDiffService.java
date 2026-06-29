package com.unilex.backend.service;

import com.unilex.backend.dto.LangPackDiffDto;

public interface LangPackDiffService {
    LangPackDiffDto.DiffResult diff(LangPackDiffDto req);
}
