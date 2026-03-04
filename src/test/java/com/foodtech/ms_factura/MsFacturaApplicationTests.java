package com.foodtech.ms_factura;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Tag("integration")
@SpringBootTest
@SuppressWarnings("PMD.AtLeastOneConstructor")
class MsFacturaApplicationTests {

	@Test
	void contextLoads() {
		assertDoesNotThrow(() -> {
		});
	}

}
