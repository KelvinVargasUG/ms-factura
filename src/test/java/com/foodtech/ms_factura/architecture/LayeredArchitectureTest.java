package com.foodtech.ms_factura.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

@SuppressWarnings({ "PMD.AtLeastOneConstructor", "PMD.LongVariable",
                "PMD.LawOfDemeter", "PMD.JUnitTestsShouldIncludeAssert" })
@Tag("integration")
class LayeredArchitectureTest {

        private static final String LAYER_DOMAIN = "Domain";
        private static final String LAYER_APPLICATION = "Application";
        private static final String LAYER_INFRASTRUCTURE = "Infrastructure";

        private static final JavaClasses CLASSES = new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPackages("com.foodtech.ms_factura");

        // Validar la arquitectura hexagonal: dirección de dependencias entre capas
        @Test
        void hexagonalLayerDirectionShouldBeRespected() {
                layeredArchitecture()
                                .consideringOnlyDependenciesInLayers()
                                .layer(LAYER_DOMAIN).definedBy("..domain..")
                                .layer(LAYER_APPLICATION).definedBy("..application..")
                                .layer(LAYER_INFRASTRUCTURE).definedBy("..infrastructure..")
                                .whereLayer(LAYER_DOMAIN)
                                .mayOnlyBeAccessedByLayers(LAYER_APPLICATION, LAYER_INFRASTRUCTURE)
                                .whereLayer(LAYER_APPLICATION).mayOnlyBeAccessedByLayers(LAYER_INFRASTRUCTURE)
                                .whereLayer(LAYER_INFRASTRUCTURE).mayNotBeAccessedByAnyLayer()
                                .check(CLASSES);
        }

        // Validar que la capa de dominio no dependa de frameworks o librerías externas
        @Test
        void domainShouldBeFrameworkAgnostic() {
                noClasses()
                                .that().resideInAPackage("..domain..")
                                .should().dependOnClassesThat().resideInAnyPackage(
                                                "org.springframework..",
                                                "jakarta..",
                                                "javax..",
                                                "org.apache.kafka..",
                                                "org.springframework.amqp..",
                                                "java.net.http..")
                                .check(CLASSES);
        }
}
