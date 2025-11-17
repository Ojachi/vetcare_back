package com.vetcare_back.config;

import com.vetcare_back.entity.Services;
import com.vetcare_back.repository.ServiceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class ServiceDataInitializer {

    private final ServiceRepository serviceRepository;

    public ServiceDataInitializer(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Checking veterinary services initialization...");
        createInitialServices();
        log.info("Veterinary services check completed - {} total services in database", serviceRepository.count());
    }

    private void createInitialServices() {
        // Servicios mezclados - no agrupados por tipo
        createService("Consulta General", "Revisión general del estado de salud de la mascota, examen físico completo y recomendaciones", 50000.00, 30, true);
        createService("Baño y Peluquería Raza Pequeña", "Baño completo, corte y cepillado para razas pequeñas", 45000.00, 90, false);
        createService("Vacuna Antirrábica", "Inmunización contra el virus de la rabia (obligatoria)", 35000.00, 15, true);
        createService("Desparasitación Interna", "Administración de antiparasitarios internos (comprimido o jarabe)", 25000.00, 15, false);
        createService("Radiografía Simple", "Estudio radiográfico de una zona específica", 70000.00, 30, true);
        createService("Corte de Uñas", "Recorte y limado de uñas", 15000.00, 15, false);
        createService("Consulta de Urgencias", "Atención prioritaria para casos de emergencia médica veterinaria", 80000.00, 45, true);
        createService("Hemograma Completo", "Análisis completo de células sanguíneas", 60000.00, 15, true);
        createService("Esterilización Hembra (Gata)", "Ovariohisterectomía (castración) en gatas", 150000.00, 90, true);
        createService("Limpieza de Oídos", "Higiene y limpieza auricular", 20000.00, 15, false);
        createService("Vacuna Triple Felina", "Protección contra panleucopenia, rinotraqueitis y calicivirus felino", 45000.00, 15, true);
        createService("Examen de Orina", "Urianálisis completo", 35000.00, 10, true);
        createService("Consulta Especializada", "Consulta con veterinario especialista (cardiología, dermatología, etc.)", 120000.00, 60, true);
        createService("Desparasitación Externa", "Aplicación de tratamiento contra pulgas, garrapatas y ácaros", 30000.00, 15, false);
        createService("Ecografía Abdominal", "Ultrasonido de órganos abdominales", 150000.00, 45, true);

        createService("Baño Medicado", "Baño con shampoo especializado para tratamiento dermatológico", 40000.00, 60, false);
        createService("Castración Macho (Gato)", "Orquiectomía en gatos", 100000.00, 60, true);
        createService("Química Sanguínea", "Perfil bioquímico completo (función renal, hepática, glucosa, etc.)", 80000.00, 15, true);
        createService("Vacuna Séxtuple Canina", "Protección contra moquillo, hepatitis, leptospirosis, parvovirus, parainfluenza y coronavirus", 55000.00, 20, true);
        createService("Examen Coprológico", "Análisis de heces para detección de parásitos", 25000.00, 10, true);
        createService("Esterilización Hembra (Perra Pequeña)", "Ovariohisterectomía en perras de razas pequeñas (hasta 10kg)", 200000.00, 120, true);
        createService("Limpieza Dental sin Anestesia", "Profilaxis dental básica sin sedación", 80000.00, 45, false);
        createService("Test de Leucemia Felina (FeLV)", "Prueba rápida para detección de leucemia felina", 55000.00, 15, true);
        createService("Desparasitación Completa", "Tratamiento integral interno y externo", 50000.00, 20, false);
        createService("Radiografía de Contraste", "Estudio radiográfico con medio de contraste", 120000.00, 45, true);
        createService("Baño y Peluquería Raza Grande", "Baño completo, corte y cepillado para razas grandes", 65000.00, 120, false);
        createService("Vacuna Óctuple Canina", "Cobertura ampliada contra 8 enfermedades virales y bacterianas", 65000.00, 20, true);
        createService("Aplicación de Vacunas Básicas", "Administración de vacunas de rutina por personal capacitado", 30000.00, 20, true);
        createService("Test de Inmunodeficiencia Felina (FIV)", "Prueba rápida para detección de VIF", 55000.00, 15, true);
        createService("Toma de Muestras Básicas", "Recolección de muestras para análisis de laboratorio", 20000.00, 10, true);
        createService("Electrocardiograma", "Evaluación de la actividad eléctrica del corazón", 80000.00, 30, true);
        createService("Pesaje y Medición", "Control de peso y medidas corporales", 10000.00, 10, false);
        createService("Test de Parvovirus Canino", "Diagnóstico rápido de parvovirus en perros", 45000.00, 15, true);
        createService("Administración de Medicamentos", "Suministro de medicamentos prescritos", 15000.00, 15, true);
        createService("Esterilización Hembra (Perra Grande)", "Ovariohisterectomía en perras de razas grandes (más de 10kg)", 280000.00, 150, true);
        createService("Control de Signos Vitales", "Monitoreo de temperatura, pulso y respiración", 25000.00, 15, false);
        createService("Test de Distemper Canino", "Detección de moquillo canino", 45000.00, 15, true);
        createService("Limpieza de Heridas Menores", "Curación y desinfección de heridas superficiales", 35000.00, 20, false);
        createService("Vacuna Contra Tos de las Perreras", "Protección contra Bordetella bronchiseptica", 40000.00, 15, true);
        createService("Vendajes y Curaciones", "Aplicación de vendajes y cuidado post-operatorio básico", 40000.00, 25, false);
        createService("Cirugía de Tejidos Blandos", "Procedimientos quirúrgicos en órganos internos", 350000.00, 180, true);
        createService("Cepillado Dental Básico", "Limpieza dental superficial sin anestesia", 30000.00, 30, false);
        createService("Limpieza Dental con Anestesia", "Profilaxis dental profunda bajo anestesia general", 250000.00, 120, true);
        createService("Aplicación de Pipetas Antiparasitarias", "Colocación de tratamientos tópicos contra parásitos", 25000.00, 10, false);
        createService("Vacuna Contra Leishmaniasis", "Prevención de leishmaniasis canina", 180000.00, 20, true);
        createService("Consulta de Control", "Seguimiento post-tratamiento o revisión de estado de salud periódico", 40000.00, 20, true);
        createService("Cirugía Ortopédica", "Reparación de fracturas y problemas óseos", 800000.00, 240, true);
        createService("Terapia de Rehabilitación Básica", "Ejercicios y cuidados de rehabilitación supervisados", 50000.00, 45, false);
        createService("Extracción Dental", "Remoción de piezas dentales dañadas o enfermas", 180000.00, 90, true);
        createService("Castración Macho (Perro)", "Orquiectomía en perros", 120000.00, 75, true);
        createService("Hospitalización Día", "Internación y monitoreo por 24 horas", 120000.00, 1440, true);
        createService("Hospitalización UCI", "Cuidados intensivos con monitoreo constante (por día)", 250000.00, 1440, true);
        createService("Fluidoterapia", "Administración de líquidos intravenosos (sesión)", 45000.00, 60, true);
        createService("Colocación de Microchip", "Implantación de microchip de identificación", 60000.00, 15, true);
        createService("Certificado de Salud", "Expedición de certificado veterinario para viajes", 35000.00, 20, true);
        createService("Eutanasia Humanitaria", "Procedimiento de eutanasia asistida con dignidad", 150000.00, 60, true);
        createService("Inseminación Artificial", "Procedimiento de reproducción asistida", 300000.00, 90, true);
        createService("Control Prenatal", "Seguimiento de gestación en mascotas", 70000.00, 45, true);
        createService("Asistencia de Parto", "Atención profesional durante el parto", 200000.00, 180, true);
        createService("Terapia Física y Rehabilitación", "Sesión de fisioterapia veterinaria", 80000.00, 60, true);
        createService("Acupuntura Veterinaria", "Sesión de acupuntura terapéutica", 100000.00, 45, true);
    }

    private void createService(String name, String description, double price, int durationMinutes, boolean requiresVeterinarian) {
        if (serviceRepository.findByName(name).isEmpty()) {
            Services service = Services.builder()
                    .name(name)
                    .description(description)
                    .price(BigDecimal.valueOf(price))
                    .durationMinutes(durationMinutes)
                    .requiresVeterinarian(requiresVeterinarian)
                    .active(true)
                    .build();
            serviceRepository.save(service);
            log.debug("Created service: {}", name);
        }
    }
}