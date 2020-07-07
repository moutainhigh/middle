package com.njwd.entity.basedata;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BaseAccountBook implements Serializable {
    private String accountBookId;

    private String enteId;

    private String companyId;

    private String currencyId;

    private String subjectId;

    private String accountBookCode;

    private String accountBookName;

    private static final long serialVersionUID = -5650790279461998521L;
}