package com.nouraschool.domain.services.impl;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.nouraschool.domain.dtos.BulletinDto;
import com.nouraschool.domain.entities.BulletinEntity;
import com.nouraschool.domain.entities.ClasseEntity;
import com.nouraschool.domain.entities.EleveEntity;
import com.nouraschool.domain.entities.NoteEntity;
import com.nouraschool.domain.enums.FormatExport;
import com.nouraschool.domain.exception.errors.NotFoundException;
import com.nouraschool.domain.repositories.BulletinRepository;
import com.nouraschool.domain.repositories.ClasseRepository;
import com.nouraschool.domain.repositories.EleveRepository;
import com.nouraschool.domain.repositories.NoteRepository;
import com.nouraschool.domain.services.BulletinExportService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@ApplicationScoped
public class BulletinExportServiceImpl implements BulletinExportService {

    private static final String ECOLE_NOM = "Établissement Noura School";
    private static final String ECOLE_ADRESSE = "Dakar, Sénégal";

    @Inject
    EleveRepository eleveRepository;
    @Inject
    BulletinRepository bulletinRepository;
    @Inject
    ClasseRepository classeRepository;
    @Inject
    NoteRepository noteRepository;

    @Override
    public ByteArrayOutputStream export(BulletinDto bulletin, FormatExport format) {
        return switch (format) {
            case PDF -> exportSinglePdf(bulletin);
            case WORD -> exportWord(bulletin);
            case EXCEL -> exportExcel(bulletin);
        };
    }

    @Override
    public ByteArrayOutputStream exportSinglePdf(BulletinDto bulletin) {
        return exportPdfIText7(bulletin);
    }

    @Override
    public ByteArrayOutputStream exportByClassePdf(UUID classeId, String anneeScolaire, String trimestre) {
        List<BulletinEntity> bulletins = bulletinRepository.findByClasseIdAndAnneeAndTrimestre(classeId, anneeScolaire, trimestre);
        return buildZipFromBulletins(bulletins, "bulletins_classe_" + classeId.toString().substring(0, 8));
    }

    @Override
    public ByteArrayOutputStream exportAllBulletinsPdf(String anneeScolaire, String trimestre) {
        List<BulletinEntity> bulletins = bulletinRepository.findAllByAnneeAndTrimestre(anneeScolaire, trimestre);
        return buildZipFromBulletins(bulletins, "bulletins_ecole_" + trimestre.replace(" ", "_") + "_" + anneeScolaire.replace("-", "_"));
    }

    @Override
    public ByteArrayOutputStream generateRapportTrimestrePdf(String anneeScolaire, String trimestre) {
        List<BulletinEntity> all = bulletinRepository.findAllByAnneeAndTrimestre(anneeScolaire, trimestre);
        Map<UUID, List<BulletinEntity>> byClasse = all.stream()
                .filter(b -> b.eleve != null && b.eleve.classe != null)
                .collect(Collectors.groupingBy(b -> b.eleve.classe.id));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, PageSize.A4);
            document.setMargins(40, 40, 40, 40);

            PdfFont titleFont = PdfFontFactory.createFont();
            PdfFont headerFont = PdfFontFactory.createFont();
            PdfFont normalFont = PdfFontFactory.createFont();

            addSenegalHeader(document, normalFont);
            document.add(new Paragraph("RAPPORT DE TRIMESTRE").setFont(titleFont).setFontSize(16).setBold().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Période: " + trimestre + " - Année scolaire: " + anneeScolaire).setFont(normalFont).setFontSize(10));
            document.add(new Paragraph(" "));

            float[] colWidths = {3, 2, 2, 2};
            Table table = new Table(UnitValue.createPercentArray(colWidths)).useAllAvailableWidth();
            table.addHeaderCell(createCell("Classe", headerFont, true));
            table.addHeaderCell(createCell("Effectif", headerFont, true));
            table.addHeaderCell(createCell("Moyenne classe", headerFont, true));
            table.addHeaderCell(createCell("Moy. min / max", headerFont, true));

            for (Map.Entry<UUID, List<BulletinEntity>> e : byClasse.entrySet()) {
                ClasseEntity classe = classeRepository.findById(e.getKey());
                String nomClasse = classe != null ? classe.nom : "-";
                List<BulletinEntity> bulletins = e.getValue();
                int effectif = bulletins.size();
                double moyClasse = bulletins.stream().mapToDouble(b -> b.moyenneClasse != null ? b.moyenneClasse : b.moyenne).average().orElse(0);
                double min = bulletins.stream().mapToDouble(b -> b.moyenne).min().orElse(0);
                double max = bulletins.stream().mapToDouble(b -> b.moyenne).max().orElse(0);
                table.addCell(createCell(nomClasse, normalFont, false));
                table.addCell(createCell(String.valueOf(effectif), normalFont, false));
                table.addCell(createCell(String.format("%.2f", moyClasse), normalFont, false));
                table.addCell(createCell(String.format("%.2f / %.2f", min, max), normalFont, false));
            }
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total élèves: " + all.size()).setFont(normalFont));
            document.close();
            return baos;
        } catch (IOException e) {
            throw new RuntimeException("Erreur génération rapport: " + e.getMessage());
        }
    }

    private ByteArrayOutputStream buildZipFromBulletins(List<BulletinEntity> bulletins, String zipBaseName) {
        ByteArrayOutputStream zipOut = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(zipOut)) {
            for (BulletinEntity entity : bulletins) {
                BulletinDto dto = toBulletinDto(entity);
                ByteArrayOutputStream pdfStream = exportPdfIText7(dto);
                String fileName = zipBaseName + "_" + sanitizeFileName(
                        (entity.eleve != null ? entity.eleve.lastName + "_" + entity.eleve.firstName : entity.id.toString())
                                + "_" + entity.trimestre + ".pdf");
                zos.putNextEntry(new ZipEntry(fileName));
                zos.write(pdfStream.toByteArray());
                zos.closeEntry();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur création ZIP bulletins: " + e.getMessage());
        }
        return zipOut;
    }

    private BulletinDto toBulletinDto(BulletinEntity e) {
        BulletinDto dto = new BulletinDto();
        dto.setId(e.id);
        dto.setEleveId(e.eleve != null ? e.eleve.id : null);
        dto.setTrimestre(e.trimestre);
        dto.setAnneeScolaire(e.anneeScolaire);
        dto.setMoyenne(e.moyenne);
        dto.setMoyenneClasse(e.moyenneClasse);
        dto.setRang(e.rang);
        dto.setTotalEleves(e.totalEleves);
        dto.setAppreciation(e.appreciation);
        dto.setNombreAbsences(e.nombreAbsences != null ? e.nombreAbsences : 0);
        dto.setNombreRetards(e.nombreRetards != null ? e.nombreRetards : 0);
        dto.setCreatedAt(e.createdAt);
        dto.setFichierPdfUrl(e.fichierPdfUrl);
        return dto;
    }

    private static String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private ByteArrayOutputStream exportPdfIText7(BulletinDto bulletin) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            document.setMargins(36, 36, 36, 36);

            PdfFont titleFont = PdfFontFactory.createFont();
            PdfFont headerFont = PdfFontFactory.createFont();
            PdfFont normalFont = PdfFontFactory.createFont();

            addSenegalHeader(document, normalFont);

            EleveEntity eleve = getEleve(bulletin.getEleveId());
            String nomComplet = eleve.firstName + " " + eleve.lastName;
            String classeNom = eleve.classe != null ? eleve.classe.nom : "-";
            String matricule = eleve.matricule != null ? eleve.matricule : "-";

            document.add(new Paragraph("BULLETIN SCOLAIRE").setFont(titleFont).setFontSize(16).setBold().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Élève: " + nomComplet).setFont(normalFont).setFontSize(11));
            document.add(new Paragraph("Matricule: " + matricule).setFont(normalFont).setFontSize(11));
            document.add(new Paragraph("Classe: " + classeNom).setFont(normalFont).setFontSize(11));
            document.add(new Paragraph("Trimestre: " + bulletin.getTrimestre() + "  —  Année scolaire: " + bulletin.getAnneeScolaire()).setFont(normalFont).setFontSize(11));
            document.add(new Paragraph(" "));

            List<NoteEntity> notes = noteRepository.findByEleveIdAndTrimestreAndAnnee(eleve.id, bulletin.getTrimestre(), bulletin.getAnneeScolaire());
            if (!notes.isEmpty()) {
                float[] colWidths = {3, 1, 1};
                Table matieresTable = new Table(UnitValue.createPercentArray(colWidths)).useAllAvailableWidth();
                matieresTable.addHeaderCell(createCell("Matière", headerFont, true));
                matieresTable.addHeaderCell(createCell("Coef.", headerFont, true));
                matieresTable.addHeaderCell(createCell("Note/20", headerFont, true));
                for (NoteEntity n : notes) {
                    String matiereNom = n.matiere != null ? n.matiere.nom : "-";
                    Integer coef = n.matiere != null ? n.matiere.coefficient : 1;
                    matieresTable.addCell(createCell(matiereNom, normalFont, false));
                    matieresTable.addCell(createCell(String.valueOf(coef), normalFont, false));
                    matieresTable.addCell(createCell(String.format("%.2f", n.note), normalFont, false));
                }
                document.add(matieresTable);
                document.add(new Paragraph(" "));
            }

            float[] colWidths = {2, 2};
            Table table = new Table(UnitValue.createPercentArray(colWidths)).useAllAvailableWidth();
            table.setMarginTop(10);
            addPdfRowIText7(table, "Moyenne générale", String.format("%.2f", bulletin.getMoyenne() != null ? bulletin.getMoyenne() : 0), headerFont, normalFont);
            addPdfRowIText7(table, "Moyenne de la classe", String.format("%.2f", bulletin.getMoyenneClasse() != null ? bulletin.getMoyenneClasse() : 0), headerFont, normalFont);
            addPdfRowIText7(table, "Rang", (bulletin.getRang() != null ? bulletin.getRang() : "-") + " / " + (bulletin.getTotalEleves() != null ? bulletin.getTotalEleves() : "-"), headerFont, normalFont);
            addPdfRowIText7(table, "Absences", String.valueOf(bulletin.getNombreAbsences() != null ? bulletin.getNombreAbsences() : 0), headerFont, normalFont);
            addPdfRowIText7(table, "Retards", String.valueOf(bulletin.getNombreRetards() != null ? bulletin.getNombreRetards() : 0), headerFont, normalFont);
            document.add(table);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Appréciation:").setFont(headerFont).setFontSize(11));
            document.add(new Paragraph(bulletin.getAppreciation() != null ? bulletin.getAppreciation() : "-").setFont(normalFont).setFontSize(10));
            document.add(new Paragraph(" "));

            document.close();
            return out;
        } catch (IOException e) {
            throw new RuntimeException("Erreur génération PDF bulletin: " + e.getMessage());
        }
    }

    private void addSenegalHeader(Document document, PdfFont font) {
        document.add(new Paragraph("RÉPUBLIQUE DU SÉNÉGAL").setFont(font).setFontSize(10).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
        document.add(new Paragraph("Ministère de l'Éducation nationale").setFont(font).setFontSize(9).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
        document.add(new Paragraph(ECOLE_NOM).setFont(font).setFontSize(11).setBold().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
        document.add(new Paragraph(ECOLE_ADRESSE).setFont(font).setFontSize(9).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
        try {
            InputStream logoStream = getClass().getResourceAsStream("/logo-ecole.png");
            if (logoStream != null) {
                byte[] logoBytes = logoStream.readAllBytes();
                Image img = new Image(ImageDataFactory.create(logoBytes));
                img.setWidth(60);
                img.setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(img);
            }
        } catch (IOException ignored) {
            // pas de logo
        }
        document.add(new Paragraph(" "));
    }

    private static Cell createCell(String text, PdfFont font, boolean header) {
        Cell cell = new Cell().add(new Paragraph(text).setFont(font).setFontSize(header ? 10 : 9));
        cell.setBorder(new SolidBorder(0.5f));
        if (header) cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        return cell;
    }

    private void addPdfRowIText7(Table table, String label, String value, PdfFont headerFont, PdfFont normalFont) {
        table.addCell(createCell(label, headerFont, false));
        table.addCell(createCell(value, normalFont, false));
    }

    private ByteArrayOutputStream exportWord(BulletinDto bulletin) {
        try (var out = new ByteArrayOutputStream();
             var doc = new XWPFDocument()) {

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
