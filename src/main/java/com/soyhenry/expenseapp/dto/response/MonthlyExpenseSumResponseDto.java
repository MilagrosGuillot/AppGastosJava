package com.soyhenry.expenseapp.dto.response;

public class MonthlyExpenseSumResponseDto {
    private int year;
    private int month;
    private double totalAmount;

    // Constructores, getters y setters

    public MonthlyExpenseSumResponseDto(int year, int month, double totalAmount) {
        this.year = year;
        this.month = month;
        this.totalAmount = totalAmount;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}
