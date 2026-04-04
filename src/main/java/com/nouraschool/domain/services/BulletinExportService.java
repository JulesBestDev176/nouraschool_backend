package com.nouraschool.domain.services;

import com.nouraschool.domain.enums.FormatExport;
import com.nouraschool.domain.dtos.BulletinDto;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

public interface BulletinExportService {

    ByteArrayOutputStream export(BulletinDto bulletin, FormatExport format);

    /** Export d'un seul bulletin (PDF). */
    ByteArrayOutputStream exportSinglePdf(BulletinDto bulletin);

    /** Export des bulletins d'une classe en PDF (un PDF par bulletin, retourné en ZIP). */
    ByteArrayOutputStream exportByClassePdf(UUID classeId, String anneeScolaire, String trimestre);

    /** Export de tous les bulletins de l'école pour un trimestre (ZIP de PDF). */
    ByteArrayOutputStream exportAllBulletinsPdf(String anneeScolaire, String trimestre);

    /** Génère le rapport de fin de trimestre (statistiques par classe, effectifs, moyennes). */
    ByteArrayOutputStream generateRapportTrimestrePdf(String anneeScolaire, String trimestre);
}
