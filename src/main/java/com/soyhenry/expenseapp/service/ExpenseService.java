package com.soyhenry.expenseapp.service;

import com.soyhenry.expenseapp.dto.response.MonthlyExpenseSumResponseDto;
import com.soyhenry.expenseapp.dto.request.ExpenseRequestDto;
import com.soyhenry.expenseapp.dto.response.ExpenseResponseDto;
import com.soyhenry.expenseapp.exception.DAOException;

import java.util.List;

public interface ExpenseService {
    String createExpense(ExpenseRequestDto expenseRequestDto);
    String updateExpense(Long id, ExpenseRequestDto expenseRequestDto);
    boolean deleteExpense(Long id) throws DAOException;
    List<ExpenseResponseDto> getAllExpenses();
    ExpenseResponseDto getExpenseById(Long id);
    MonthlyExpenseSumResponseDto getExpenseSumByMonth(int year, int month);
    Double getTotalExpenseSum();

   Double getExpensesByCategoryName(String name);
}
