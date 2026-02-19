package com.nouraschool.domain.services;

import com.nouraschool.domain.enums.FormatExport;
import com.nouraschool.domain.dtos.BulletinDto;

import java.io.ByteArrayOutputStream;

public interface BulletinExportService {

    ByteArrayOutputStream export(BulletinDto bulletin, FormatExport format);
}
