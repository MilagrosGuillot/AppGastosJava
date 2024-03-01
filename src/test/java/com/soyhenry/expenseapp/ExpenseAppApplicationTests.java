package com.soyhenry.expenseapp;

import com.soyhenry.expenseapp.controller.ExpenseController;
import com.soyhenry.expenseapp.dto.response.ExpenseResponseDto;
import com.soyhenry.expenseapp.dto.response.MonthlyExpenseSumResponseDto;
import com.soyhenry.expenseapp.repository.ExpenseRepository;
import com.soyhenry.expenseapp.service.ExpenseService;
import com.soyhenry.expenseapp.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class ExpenseAppApplicationTests {

	@Mock
	private ExpenseService expenseService;

	@InjectMocks
	private ExpenseController expenseController;

	@Test
	void testGetExpenses() {
		// Mock de datos de respuesta
		List<ExpenseResponseDto> mockResponses = Arrays.asList(
				new ExpenseResponseDto(1, "Comida", 50.0),
				new ExpenseResponseDto(2, "Transporte", 30.0)

		);

		// Configurar el comportamiento esperado del servicio
		when(expenseService.getAllExpenses()).thenReturn(mockResponses);

		// Llamar al método bajo prueba
		ResponseEntity<List<ExpenseResponseDto>> responseEntity = expenseController.getExpenses();

		// Verificar el resultado
		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(mockResponses, responseEntity.getBody());
	}
	@Test
	void testGetTotalExpenseSum() {
		// Mock del resultado esperado
		Double mockTotalSum = 150.0;


		when(expenseService.getTotalExpenseSum()).thenReturn(mockTotalSum);


		ResponseEntity<Double> responseEntity = expenseController.getTotalExpenseSum();


		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(mockTotalSum, responseEntity.getBody());
	}
	@Test
	void testGetExpenseById() {
		// ID de gasto ficticio para la prueba
		Long expenseId = 2L;

		// Mock del resultado esperado
		ExpenseResponseDto mockExpenseResponse = new ExpenseResponseDto(Math.toIntExact(expenseId), "Comida", 50.0);

		// Configurar el comportamiento esperado del servicio
		when(expenseService.getExpenseById(expenseId)).thenReturn(mockExpenseResponse);

		// Llamar al método bajo prueba
		ResponseEntity<ExpenseResponseDto> responseEntity = expenseController.getExpenseById(expenseId);


		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(mockExpenseResponse, responseEntity.getBody());
	}
}