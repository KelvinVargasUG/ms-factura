package com.foodtech.ms_factura.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.foodtech.ms_factura", importOptions = ImportOption.DoNotIncludeTests.class)
class LayeredArchitectureTest {
        // Definir las capas y sus dependencias permitidas para validar la arquitectura
        // hexagonal
        @ArchTest
        static final ArchRule hexagonal_layer_direction = layeredArchitecture()
                        .consideringOnlyDependenciesInLayers()
                        .layer("Domain").definedBy("..domain..")
                        .layer("Application").definedBy("..application..")
                        .layer("Infrastructure").definedBy("..infrastructure..")
                        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure")
                        .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
                        .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer();
        // Validar que la capa de dominio no dependa de frameworks o librerías
        // específicas para mantenerla agnóstica
        @ArchTest
        static final ArchRule domain_is_framework_agnostic = noClasses()
                        .that().resideInAPackage("..domain..")
                        .should().dependOnClassesThat().resideInAnyPackage(
                                        "org.springframework..",
                                        "jakarta..",
                                        "javax..",
                                        "org.apache.kafka..",
                                        "org.springframework.amqp..",
                                        "java.net.http..");
}
