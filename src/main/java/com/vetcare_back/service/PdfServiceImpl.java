package com.vetcare_back.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.vetcare_back.dto.diagnosis.DiagnosisResponseDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfServiceImpl implements IPdfService {

    @Override
    public byte[] generateDiagnosisPdf(DiagnosisResponseDTO diagnosis) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Header
            addHeader(document);
            
            // Información del diagnóstico
            addDiagnosisInfo(document, diagnosis);
            
            // Información de la mascota
            addPetInfo(document, diagnosis);
            
            // Información del propietario
            addOwnerInfo(document, diagnosis);
            
            // Información de la cita
            addAppointmentInfo(document, diagnosis);
            
            // Diagnóstico y tratamiento
            addDiagnosisDetails(document, diagnosis);
            
            // Información del veterinario
            addVetInfo(document, diagnosis);
            
            // Footer
            addFooter(document);
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }
    }
    
    private void addHeader(Document document) {
        Paragraph title = new Paragraph("VETCARE")
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(41, 128, 185));
        document.add(title);
        
        Paragraph subtitle = new Paragraph("Diagnóstico Veterinario")
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitle);
    }
    
    private void addDiagnosisInfo(Document document, DiagnosisResponseDTO diagnosis) {
        addSectionTitle(document, "Información del Diagnóstico");
        
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));
        
        addTableRow(table, "ID Diagnóstico:", diagnosis.getId().toString());
        addTableRow(table, "Fecha de Emisión:", diagnosis.getDate().toString());
        addTableRow(table, "Estado:", diagnosis.getActive() ? "Activo" : "Inactivo");
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }
    
    private void addPetInfo(Document document, DiagnosisResponseDTO diagnosis) {
        addSectionTitle(document, "Información de la Mascota");
        
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));
        
        var pet = diagnosis.getAppointment().getPet();
        addTableRow(table, "Nombre:", pet.getName());
        addTableRow(table, "Especie:", pet.getSpecies());
        addTableRow(table, "Raza:", pet.getBreed());
        addTableRow(table, "Edad:", pet.getAge() + " años");
        if (pet.getWeight() != null) {
            addTableRow(table, "Peso:", pet.getWeight() + " kg");
        }
        addTableRow(table, "Sexo:", pet.getSex());
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }
    
    private void addOwnerInfo(Document document, DiagnosisResponseDTO diagnosis) {
        addSectionTitle(document, "Información del Propietario");
        
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));
        
        var owner = diagnosis.getAppointment().getPet().getOwner();
        addTableRow(table, "Nombre:", owner.getName());
        addTableRow(table, "Email:", owner.getEmail());
        if (owner.getPhone() != null) {
            addTableRow(table, "Teléfono:", owner.getPhone());
        }
        if (owner.getAddress() != null) {
            addTableRow(table, "Dirección:", owner.getAddress());
        }
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }
    
    private void addAppointmentInfo(Document document, DiagnosisResponseDTO diagnosis) {
        addSectionTitle(document, "Información de la Cita");
        
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));
        
        var appointment = diagnosis.getAppointment();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        addTableRow(table, "Fecha de Consulta:", appointment.getStartDateTime().format(formatter));
        addTableRow(table, "Servicio:", appointment.getService().getName());
        addTableRow(table, "Estado:", appointment.getStatus().toString());
        if (appointment.getNote() != null && !appointment.getNote().isBlank()) {
            addTableRow(table, "Notas:", appointment.getNote());
        }
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }
    
    private void addDiagnosisDetails(Document document, DiagnosisResponseDTO diagnosis) {
        addSectionTitle(document, "Diagnóstico");
        Paragraph desc = new Paragraph(diagnosis.getDescription())
                .setMarginLeft(10)
                .setMarginBottom(10);
        document.add(desc);
        
        if (diagnosis.getTreatment() != null && !diagnosis.getTreatment().isBlank()) {
            addSectionTitle(document, "Tratamiento");
            Paragraph treatment = new Paragraph(diagnosis.getTreatment())
                    .setMarginLeft(10)
                    .setMarginBottom(10);
            document.add(treatment);
        }
        
        if (diagnosis.getMedications() != null && !diagnosis.getMedications().isBlank()) {
            addSectionTitle(document, "Medicamentos");
            Paragraph meds = new Paragraph(diagnosis.getMedications())
                    .setMarginLeft(10)
                    .setMarginBottom(10);
            document.add(meds);
        }
        
        document.add(new Paragraph("\n"));
    }
    
    private void addVetInfo(Document document, DiagnosisResponseDTO diagnosis) {
        addSectionTitle(document, "Veterinario");
        
        Table table = new Table(2);
        table.setWidth(UnitValue.createPercentValue(100));
        
        var vet = diagnosis.getVet();
        addTableRow(table, "Nombre:", vet.getName());
        addTableRow(table, "Email:", vet.getEmail());
        if (vet.getPhone() != null) {
            addTableRow(table, "Teléfono:", vet.getPhone());
        }
        
        document.add(table);
        document.add(new Paragraph("\n"));
    }
    
    private void addFooter(Document document) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        Paragraph footer = new Paragraph("Documento generado el " + LocalDateTime.now().format(formatter))
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.GRAY)
                .setMarginTop(20);
        document.add(footer);
    }
    
    private void addSectionTitle(Document document, String title) {
        Paragraph section = new Paragraph(title)
                .setFontSize(14)
                .setBold()
                .setFontColor(new DeviceRgb(52, 73, 94))
                .setMarginTop(10)
                .setMarginBottom(5);
        document.add(section);
    }
    
    private void addTableRow(Table table, String label, String value) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label).setBold())
                .setBackgroundColor(new DeviceRgb(236, 240, 241))
                .setPadding(5);
        Cell valueCell = new Cell()
                .add(new Paragraph(value))
                .setPadding(5);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}
