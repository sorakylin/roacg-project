package com.roacg.service.tc.flow.model.req;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class CreateDirREQ {

    @NotNull(message = "parentId不能为空!")
    private Long parentId;

    @NotBlank(message = "目录名字不能为空")
    @Length(message = "目录名不能超过20字", max = 20)
    private String dirName;
}
