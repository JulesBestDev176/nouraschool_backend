package com.nouraschool.domain.services.impl;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.nouraschool.domain.enums.FormatExport;
import com.nouraschool.domain.dtos.BulletinDto;
import com.nouraschool.domain.entities.EleveEntity;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.EleveRepository;
import com.nouraschool.domain.services.BulletinExportService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@ApplicationScoped
public class BulletinExportServiceImpl implements BulletinExportService {

    @Inject
    EleveRepository eleveRepository;

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);

    @Override
    public ByteArrayOutputStream export(BulletinDto bulletin, FormatExport format) {
        return switch (format) {
            case PDF -> exportPdf(bulletin);
            case WORD -> exportWord(bulletin);
            case EXCEL -> exportExcel(bulletin);
        };
    }

    private ByteArrayOutputStream exportPdf(BulletinDto bulletin) {
        try (var out = new ByteArrayOutputStream()) {
            var document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            var eleve = getEleve(bulletin.getEleveId());
            var nomComplet = eleve.firstName + " " + eleve.lastName;
            var classeNom = eleve.classe != null ? eleve.classe.nom : "-";

            document.add(new Paragraph("BULLETIN SCOLAIRE", TITLE_FONT));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Élève: " + nomComplet, NORMAL_FONT));
            document.add(new Paragraph("Classe: " + classeNom, NORMAL_FONT));
            document.add(new Paragraph("Trimestre: " + bulletin.getTrimestre() + " - Année: " + bulletin.getAnneeScolaire(), NORMAL_FONT));
            document.add(new Paragraph(" "));

            var table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            addPdfRow(table, "Moyenne", String.valueOf(bulletin.getMoyenne()));
            addPdfRow(table, "Moyenne classe", String.valueOf(bulletin.getMoyenneClasse()));
            addPdfRow(table, "Rang", bulletin.getRang() + " / " + bulletin.getTotalEleves());
            addPdfRow(table, "Absences", String.valueOf(bulletin.getNombreAbsences()));
            addPdfRow(table, "Retards", String.valueOf(bulletin.getNombreRetards()));
            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Appréciation:", HEADER_FONT));
            document.add(new Paragraph(bulletin.getAppreciation() != null ? bulletin.getAppreciation() : "-", NORMAL_FONT));

            document.close();
            return out;
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Erreur génération PDF: " + e.getMessage());
        }
    }

    private void addPdfRow(PdfPTable table, String label, String value) {
        var cellLabel = new PdfPCell(new Phrase(label, HEADER_FONT));
        var cellValue = new PdfPCell(new Phrase(value, NORMAL_FONT));
        cellLabel.setBorderWidth(0.5f);
        cellValue.setBorderWidth(0.5f);
        table.addCell(cellLabel);
        table.addCell(cellValue);
    }

    private ByteArrayOutputStream exportWord(BulletinDto bulletin) {
        try (var out = new ByteArrayOutputStream();
             var doc = new org.apache.poi.xwpf.usermodel.XWPFDocument()) {

            var eleve = getEleve(bulletin.getEleveId());
            var nomComplet = eleve.firstName + " " + eleve.lastName;
            var classeNom = eleve.classe != null ? eleve.classe.nom : "-";

            var title = doc.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            var titleRun = title.createRun();
            titleRun.setText("BULLETIN SCOLAIRE");
            titleRun.setBold(true);
            titleRun.setFontSize(16);

            doc.createParagraph().createRun().addBreak();
            addWordParagraph(doc, "Élève: " + nomComplet);
            addWordParagraph(doc, "Classe: " + classeNom);
            addWordParagraph(doc, "Trimestre: " + bulletin.getTrimestre() + " - Année: " + bulletin.getAnneeScolaire());
            doc.createParagraph().createRun().addBreak();

            var table = doc.createTable(5, 2);
            table.setWidth("100%");
            setWordRow(table.getRow(0), "Moyenne", String.valueOf(bulletin.getMoyenne()));
            setWordRow(table.getRow(1), "Moyenne classe", String.valueOf(bulletin.getMoyenneClasse()));
            setWordRow(table.getRow(2), "Rang", bulletin.getRang() + " / " + bulletin.getTotalEleves());
            setWordRow(table.getRow(3), "Absences", String.valueOf(bulletin.getNombreAbsences()));
            setWordRow(table.getRow(4), "Retards", String.valueOf(bulletin.getNombreRetards()));

            doc.createParagraph().createRun().addBreak();
            var apprPara = doc.createParagraph();
            var apprRun = apprPara.createRun();
            apprRun.setText("Appréciation: " + (bulletin.getAppreciation() != null ? bulletin.getAppreciation() : "-"));
            apprRun.setBold(true);

            doc.write(out);
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Erreur génération Word: " + e.getMessage());
        }
    }

    private void addWordParagraph(XWPFDocument doc, String text) {
        var p = doc.createParagraph();
        p.createRun().setText(text);
    }

    private void setWordRow(XWPFTableRow row, String label, String value) {
        row.getCell(0).setText(label);
        row.getCell(1).setText(value);
    }

    private ByteArrayOutputStream exportExcel(BulletinDto bulletin) {
        try (var wb = new XSSFWorkbook(); var out = new ByteArrayOutputStream()) {
            var sheet = wb.createSheet("Bulletin");
            var style = wb.createCellStyle();
            var font = wb.createFont();
            font.setBold(true);
            style.setFont(font);

            var eleve = getEleve(bulletin.getEleveId());
            var nomComplet = eleve.firstName + " " + eleve.lastName;
            var classeNom = eleve.classe != null ? eleve.classe.nom : "-";

            int rowNum = 0;
            createExcelTitleRow(sheet, rowNum++, "BULLETIN SCOLAIRE", style);
            rowNum++;
            createExcelRow(sheet, rowNum++, "Élève", nomComplet);
            createExcelRow(sheet, rowNum++, "Classe", classeNom);
            createExcelRow(sheet, rowNum++, "Trimestre", bulletin.getTrimestre());
            createExcelRow(sheet, rowNum++, "Année scolaire", bulletin.getAnneeScolaire());
            rowNum++;
            createExcelRow(sheet, rowNum++, "Moyenne", String.valueOf(bulletin.getMoyenne()));
            createExcelRow(sheet, rowNum++, "Moyenne classe", String.valueOf(bulletin.getMoyenneClasse()));
            createExcelRow(sheet, rowNum++, "Rang", bulletin.getRang() + " / " + bulletin.getTotalEleves());
            createExcelRow(sheet, rowNum++, "Absences", String.valueOf(bulletin.getNombreAbsences()));
            createExcelRow(sheet, rowNum++, "Retards", String.valueOf(bulletin.getNombreRetards()));
            rowNum++;
            createExcelRow(sheet, rowNum++, "Appréciation", bulletin.getAppreciation() != null ? bulletin.getAppreciation() : "-");

            for (var i = 0; i < 2; i++) sheet.autoSizeColumn(i);

            wb.write(out);
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Erreur génération Excel: " + e.getMessage());
        }
    }

    private void createExcelTitleRow(Sheet sheet, int rowNum, String label, CellStyle style) {
        var row = sheet.createRow(rowNum);
        var cell = row.createCell(0);
        cell.setCellValue(label);
        cell.setCellStyle(style);
    }

    private void createExcelRow(Sheet sheet, int rowNum, String label, String value) {
        var row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
    }

    private EleveEntity getEleve(UUID eleveId) {
        if (eleveId == null) throw new NotFoundException("EleveId requis pour l'export");
        var eleve = eleveRepository.findById(eleveId);
        if (eleve == null) throw new NotFoundException("Élève non trouvé: " + eleveId);
        return eleve;
    }
}
