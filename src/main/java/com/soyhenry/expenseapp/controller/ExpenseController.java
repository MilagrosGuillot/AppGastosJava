package com.soyhenry.expenseapp.controller;

import com.soyhenry.expenseapp.dto.response.MonthlyExpenseSumResponseDto;
import com.soyhenry.expenseapp.dto.request.ExpenseRequestDto;
import com.soyhenry.expenseapp.dto.response.ExpenseResponseDto;
import com.soyhenry.expenseapp.exception.BadRequestException;
import com.soyhenry.expenseapp.exception.DAOException;
import com.soyhenry.expenseapp.exception.ResourceNotFoundExcepcion;
import com.soyhenry.expenseapp.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/expense")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // El endpoint con POST envía un body definido por las propiedades del dto
    @PostMapping()
    public ResponseEntity<String> createExpense(@RequestBody @Valid ExpenseRequestDto expenseRequestDto) {
        try {
            String response = expenseService.createExpense(expenseRequestDto);
            System.out.println("ExpenseController: creando un gasto");
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        } catch (DataAccessException exDt) {
            throw new BadRequestException(exDt.getMessage());
        }
    }


    // El endpoint con PUT envía un body definido por las propiedades del dto para actualizar el gasto con id especificado por parametro
    @PutMapping("/update")
    public ResponseEntity<String> updateExpense(@RequestParam Long id,
                                                @RequestBody @Valid ExpenseRequestDto expenseRequestDto) {

            String response = expenseService.updateExpense(id, expenseRequestDto);
            System.out.println("ExpenseController: actualizando el gasto");

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(response);
    }

    // El endpoint DELETE eliminará un gasto con el id especificado por path
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpense(@PathVariable Long id) throws DAOException {
        boolean deleted = expenseService.deleteExpense(id);
        System.out.println("ExpenseController: eliminando el gasto");
        if (!deleted) {
            throw new ResourceNotFoundExcepcion("expense", "id", id);
        }
        return ResponseEntity
                .status(HttpStatus.GONE)
                .body("Se eliminó el gasto con id: " + id);
    }


    // El endpoint GET con id definido en path recuperará un gasto especificado
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponseDto> getExpenseById(@PathVariable Long id) {
        ExpenseResponseDto expenseResponseDto = expenseService.getExpenseById(id);
        if (expenseResponseDto == null) {
            throw new ResourceNotFoundExcepcion("expense", "id", id);
        }
        System.out.println("ExpenseController: obteniendo el gasto con id: " + id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(expenseResponseDto);
    }



    // El endpoint GET sin path ni parametro recuperará todos los gastos de la BD
    @GetMapping()
    public ResponseEntity<List<ExpenseResponseDto>> getExpenses() {
        List<ExpenseResponseDto> responses = expenseService.getAllExpenses();
        if(responses == null || responses.isEmpty()){
            throw new ResourceNotFoundExcepcion("expense");
        }
        System.out.println("ExpenseController: obteniendo todos los gastos");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(responses);
    }

    // Nuevo endpoint para obtener la suma de gastos por mes
    @GetMapping("/sum-by-month")
    public ResponseEntity<MonthlyExpenseSumResponseDto> getExpenseSumByMonth(@RequestParam int year, @RequestParam int month) {
        MonthlyExpenseSumResponseDto sum = expenseService.getExpenseSumByMonth(year, month);
        System.out.println("ExpenseController: obteniendo la suma de gastos para el mes " + month + " del año " + year);

        if (sum != null) {
            return ResponseEntity.status(HttpStatus.OK).body(sum);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MonthlyExpenseSumResponseDto(year, month, 0.0));
        }
    }

    // Nuevo endpoint para obtener la suma total de gastos de todos los meses
    @GetMapping("/total-sum")
    public ResponseEntity<Double> getTotalExpenseSum() {
        Double totalSum = expenseService.getTotalExpenseSum();
        System.out.println("ExpenseController: obteniendo la suma total de gastos");

        if (totalSum != null) {
            return ResponseEntity.status(HttpStatus.OK).body(totalSum);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0.0);
        }
    }
    @GetMapping("/categories")
    public ResponseEntity<Set<String>> getExpenseCategories() {
        List<ExpenseResponseDto> responses = expenseService.getAllExpenses();
        System.out.println("ExpenseController: obteniendo todos los nombres de categorías");

        Set<String> categoryNames = responses.stream()
                .map(expense -> expense.getCategoryDto().getName())
                .collect(Collectors.toSet());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryNames);
    }
    @GetMapping("/category/{name}")
    public ResponseEntity<Double> getExpensesByCategoryName(@PathVariable String name) {
        Double expenseResponseDtos = expenseService.getExpensesByCategoryName(name);
        System.out.println("ExpenseController: obteniendo gastos con categoría: " + name);

        if (expenseResponseDtos.isNaN()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(expenseResponseDtos);
    }



}