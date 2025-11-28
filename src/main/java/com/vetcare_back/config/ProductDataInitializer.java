package com.vetcare_back.config;

import com.vetcare_back.entity.Product;
import com.vetcare_back.entity.ProductCategory;
import com.vetcare_back.repository.ProductCategoryRepository;
import com.vetcare_back.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
@org.springframework.core.annotation.Order(2)
public class ProductDataInitializer {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;

    public ProductDataInitializer(ProductRepository productRepository, ProductCategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (productRepository.count() == 0) {
            log.info("Initializing products...");
            
            ProductCategory alimentos = categoryRepository.findByActiveTrue().stream()
                    .filter(c -> c.getName().equals("Alimentos")).findFirst().orElse(null);
            ProductCategory medicamentos = categoryRepository.findByActiveTrue().stream()
                    .filter(c -> c.getName().equals("Medicamentos")).findFirst().orElse(null);
            ProductCategory higiene = categoryRepository.findByActiveTrue().stream()
                    .filter(c -> c.getName().equals("Higiene y Cuidado")).findFirst().orElse(null);
            ProductCategory accesorios = categoryRepository.findByActiveTrue().stream()
                    .filter(c -> c.getName().equals("Accesorios")).findFirst().orElse(null);
            ProductCategory suplementos = categoryRepository.findByActiveTrue().stream()
                    .filter(c -> c.getName().equals("Suplementos")).findFirst().orElse(null);
            ProductCategory antiparasitarios = categoryRepository.findByActiveTrue().stream()
                    .filter(c -> c.getName().equals("Antiparasitarios")).findFirst().orElse(null);

            List<Product> products = List.of(
                // ALIMENTOS
                Product.builder()
                    .name("PetEssentials Adult Dog Food")
                    .description("Alimento premium para perro adulto, rico en proteínas, omegas y vitaminas esenciales.")
                    .price(new BigDecimal("85000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302002/ea90ee8f-3a81-418a-8492-4a67cc08016a_tdrg3g.png")
                    .stock(60)
                    .active(true)
                    .category(alimentos)
                    .build(),

                Product.builder()
                    .name("HappyPaws Chicken Training Treats")
                    .description("Snacks horneados sabor pollo, ideales para entrenamiento y refuerzo positivo.")
                    .price(new BigDecimal("15000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302221/1673359_qspmz6.webp")
                    .stock(90)
                    .active(true)
                    .category(alimentos)
                    .build(),

                Product.builder()
                    .name("MiauLife Salmon Cat Bites")
                    .description("Bocaditos suaves para gato, sabor salmón natural, altos en taurina.")
                    .price(new BigDecimal("18000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302001/aaaaa_meozd2.avif")
                    .stock(75)
                    .active(true)
                    .category(alimentos)
                    .build(),

                // MEDICAMENTOS
                Product.builder()
                    .name("VetPharma Amoxicillin 250mg")
                    .description("Antibiótico de amplio espectro para perros y gatos. Uso veterinario.")
                    .price(new BigDecimal("38000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302367/sapo1_adx8nz.jpg")
                    .stock(40)
                    .active(true)
                    .category(medicamentos)
                    .build(),

                Product.builder()
                    .name("CaninCare Anti-Inflammatory Drops")
                    .description("Gotas antiinflamatorias para reducir dolor y molestias articulares.")
                    .price(new BigDecimal("32000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302366/s-l1600_mppncw.webp")
                    .stock(50)
                    .active(true)
                    .category(medicamentos)
                    .build(),

                Product.builder()
                    .name("FelixVet Ear Infection Solution")
                    .description("Solución ótica especializada para infecciones leves y moderadas.")
                    .price(new BigDecimal("29000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302365/virbac-easotic-ear-infection-treatment-for-dogs-10ml_z4f12m.webp")
                    .stock(55)
                    .active(true)
                    .category(medicamentos)
                    .build(),

                // HIGIENE Y CUIDADO
                Product.builder()
                    .name("CleanPets Soft Shampoo")
                    .description("Shampoo suave con avena y aloe vera, ideal para pieles sensibles.")
                    .price(new BigDecimal("26000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302434/sapo2_yacnrf.jpg")
                    .stock(70)
                    .active(true)
                    .category(higiene)
                    .build(),

                Product.builder()
                    .name("FreshPaws Odor Control Spray")
                    .description("Spray eliminador de olores para mascotas y superficies del hogar.")
                    .price(new BigDecimal("22000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302434/antigarrapata_vnfove.jpg")
                    .stock(65)
                    .active(true)
                    .category(higiene)
                    .build(),

                Product.builder()
                    .name("GroomMaster Coat Brush")
                    .description("Cepillo profesional para remover nudos y pelo muerto.")
                    .price(new BigDecimal("34000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302436/cepillos_tt0e0s.jpg")
                    .stock(45)
                    .active(true)
                    .category(higiene)
                    .build(),

                // ACCESORIOS
                Product.builder()
                    .name("PawStyle Comfort Collar")
                    .description("Collar acolchado ajustable, ideal para paseos diarios.")
                    .price(new BigDecimal("18000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302480/collar_y5hgdx.jpg")
                    .stock(90)
                    .active(true)
                    .category(accesorios)
                    .build(),

                Product.builder()
                    .name("DogFun Rubber Chew Toy")
                    .description("Juguete resistente hecho de caucho natural no tóxico.")
                    .price(new BigDecimal("25000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302479/chewy_drdbuo.jpg")
                    .stock(100)
                    .active(true)
                    .category(accesorios)
                    .build(),

                Product.builder()
                    .name("SleepyPet Soft Bed")
                    .description("Cama acolchada con base antideslizante y tela respirable.")
                    .price(new BigDecimal("95000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302480/camita_ywtpiz.jpg")
                    .stock(30)
                    .active(true)
                    .category(accesorios)
                    .build(),

                // SUPLEMENTOS
                Product.builder()
                    .name("NutriPets Multivitamin Complex")
                    .description("Multivitamínico completo para fortalecer defensas y energía.")
                    .price(new BigDecimal("30000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302537/multivitaminico_v8ma5f.jpg")
                    .stock(50)
                    .active(true)
                    .category(suplementos)
                    .build(),

                Product.builder()
                    .name("PetStrong Joint Support")
                    .description("Suplemento para articulaciones con glucosamina y condroitina.")
                    .price(new BigDecimal("45000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302534/proteina_pewylr.jpg")
                    .stock(40)
                    .active(true)
                    .category(suplementos)
                    .build(),

                Product.builder()
                    .name("MiauBoost Omega 3 Drops")
                    .description("Suplemento líquido con omega 3 para brillo del pelaje y salud cardíaca.")
                    .price(new BigDecimal("28000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302536/omega3_bnxh4x.avif")
                    .stock(65)
                    .active(true)
                    .category(suplementos)
                    .build(),

                // ANTIPARASITARIOS
                Product.builder()
                    .name("StopPest Flea & Tick Collar")
                    .description("Collar antipulgas y antigarrapatas con protección hasta por 8 meses.")
                    .price(new BigDecimal("35000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302604/correparasi_ceo3nu.jpg")
                    .stock(70)
                    .active(true)
                    .category(antiparasitarios)
                    .build(),

                Product.builder()
                    .name("VetShield Deworming Tablets")
                    .description("Tabletas antiparasitarias internas de amplio espectro.")
                    .price(new BigDecimal("23000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302601/pepas_crfnye.jpg")
                    .stock(55)
                    .active(true)
                    .category(antiparasitarios)
                    .build(),

                Product.builder()
                    .name("FeliGuard Spot-On")
                    .description("Spot-on para gatos que elimina pulgas y previene nuevas infestaciones.")
                    .price(new BigDecimal("28000"))
                    .image("https://res.cloudinary.com/dphq4f927/image/upload/v1764302601/gatopiojo_ew2yzw.jpg")
                    .stock(40)
                    .active(true)
                    .category(antiparasitarios)
                    .build()
            );

            productRepository.saveAll(products);
            log.info("✅ {} products initialized successfully", products.size());
        } else {
            log.info("Products already exist - skipping initialization");
        }
    }
}
