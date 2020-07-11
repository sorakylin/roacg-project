package com.roacg.service.tc.flow.model.dto;

import com.roacg.service.tc.flow.model.enums.TranslateResponsibilitiesEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 贡献者
 */
@Data
public class ContributorDTO {

    @NotNull
    private Long contributorId;

    //职责
    @NotNull
    private TranslateResponsibilitiesEnum responsibilities;
}
