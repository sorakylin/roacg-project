package com.roacg.service.tc.flow.model.req;

import com.roacg.service.tc.flow.model.dto.ContributorDTO;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class CreateTextDocumentREQ {

    @NotNull(message = "parentId不能为空!")
    private Long parentId;

    @NotEmpty(message = "原文不能为空")
    @Length(message = "原文不能超过 10000 字", max = 10000)
    private String originTextContent;

    @NotEmpty(message = "名字不能为空")
    @Length(message = "文件名不能超过20字", max = 20)
    private String documentName;

    //指定审核人
    private Long reviewerId;

    //贡献者
    @NotEmpty(message = "工作流中必须拥有至少一人")
    @Valid
    private List<ContributorDTO> contributors;
}
