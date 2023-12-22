package com.soyhenry.expenseapp.repository.impl;

import com.soyhenry.expenseapp.domain.Expense;
import com.soyhenry.expenseapp.domain.ExpenseCategory;
import com.soyhenry.expenseapp.dto.response.MonthlyExpenseSumResponseDto;
import com.soyhenry.expenseapp.exception.DAOException;
import com.soyhenry.expenseapp.repository.ExpenseRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ExpenseRepositoryImpl implements ExpenseRepository {
    // Aglomero todas las sentencias SQL en constantes
    private static final String UPDATE_EXPENSE_BY_ID = "UPDATE Expense SET amount = ?, category_name = ?, date = ? WHERE id = ?";
    private static final String INSERT_INTO_EXPENSE = "INSERT INTO Expense (amount, category_id, category_name, date) VALUES (?, ?, ?, ?)";
    private static final String DELETE_FROM_EXPENSE_BY_ID = "DELETE FROM Expense WHERE id = ?";
    private static final String SELECT_ALL_EXPENSES = "SELECT * FROM Expense";
    private static final String SELECT_EXPENSE_BY_ID = "SELECT * FROM Expense WHERE id = ?";
    private static final String INSERT_INTO_CATEGORY_EXPENSE = "INSERT INTO ExpenseCategory (name) VALUES (?)";
    private static final String SELECT_FROM_EXPENSE_CATEGORY_BY_NAME = "SELECT * FROM ExpenseCategory WHERE name = ?";
    private static final String  sql =  "SELECT amount, date FROM Expense WHERE PARSE_DATE(date, 'yyyy-MM-dd') IS NOT NULL";

    // Objeto de JDBC de Spring que realiza todas las operaciones necesarias para
    // realizar la conexión, generar los prepared statements, ejecutar las operaciones y cerrar las conexiones
    private final JdbcTemplate jdbcTemplate;

    public ExpenseRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer insertExpense(Expense expense) {
        String categoryName = expense.getCategoryName().toLowerCase();

        // Verificamos si la categoría ya existe
        ExpenseCategory existingCategory = getCategoryByName(categoryName);

        if (existingCategory == null) {
            // Si la categoría no existe, la insertamos
            jdbcTemplate.update(INSERT_INTO_CATEGORY_EXPENSE, categoryName);

            // Recuperamos la categoría recién insertada
            existingCategory = getCategoryByName(categoryName);
        }

        // Aseguramos que la categoría existe antes de continuar
        assert existingCategory != null;

        // Luego, usamos los datos de esa categoría para completar el gasto y guardarlo
        return jdbcTemplate.update(INSERT_INTO_EXPENSE,
                expense.getAmount(),
                existingCategory.getId(),
                existingCategory.getName(),
                expense.getDate());
    }

    private ExpenseCategory getCategoryByName(String categoryName) {
        // Declaramos el parámetro a ser insertado en la query y cuantos placeholders o argumentos pasamos
        Object[] params = {categoryName};
        int[] types = {Types.VARCHAR};

        try {
            // Intentamos recuperar la categoría por nombre
            return jdbcTemplate.queryForObject(
                    SELECT_FROM_EXPENSE_CATEGORY_BY_NAME,
                    params, types,
                    new ExpenseCategoryRowMapper());
        } catch (EmptyResultDataAccessException e) {
            // Si no se encuentra ninguna categoría, devolvemos null
            return null;
        }
    }


    @Override
    public Integer updateExpense(Long id, Expense expense) {
        System.out.println("Actualizando la presentación");
        return jdbcTemplate.update(UPDATE_EXPENSE_BY_ID,
            expense.getAmount(),
            expense.getCategoryName(),
            expense.getDate(),
            id);
    }

    @Override
    public void deleteExpense(Long id) throws DAOException {
        System.out.println("Se elimina el gasto con ID: " + id);
        // Manejamos un try/catch para que, en caso de error al ejecutar la sentencia SQL de delete, arrojemos una excepción customizada
        try {
            jdbcTemplate.update(DELETE_FROM_EXPENSE_BY_ID, id);
        } catch (DataAccessException exception) {
            throw new DAOException("Hubo un error al eliminar el gasto con id " + id, exception);
        }
        System.out.println("Gasto eliminado con éxito");
    }

    @Override
    public Expense selectExpenseById(Long id) {
        // Declaramos el parámetro a ser insertado en la query y cuantos placeholders o argumentos pasamos
        Object[] params = {id};
        int[] types = {1};
        return jdbcTemplate.queryForObject(
            SELECT_EXPENSE_BY_ID,
            params, types,
            // Le definimos como debe mapear cada campo recuperado de BD en las propiedades correspondientes de la entidad
            new ExpenseRowMapper());
    }

    @Override
    public MonthlyExpenseSumResponseDto selectExpenseSumByMonth(int year, int month) {

        String sql = "SELECT amount, date FROM Expense WHERE date IS NOT NULL";

        // Realizamos la consulta y obtenemos una lista de objetos Object[]
        List<Object[]> result = jdbcTemplate.query(sql, (rs, rowNum) -> new Object[]{rs.getDouble("amount"), rs.getString("date")});

        List<Double> filteredExpenses = result.stream()
                .filter(obj -> {
                    String expenseDate = (String) obj[1];
                    LocalDate expenseLocalDate = LocalDate.parse(expenseDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    return expenseLocalDate.getYear() == year && expenseLocalDate.getMonthValue() == month;
                })
                .map(obj -> (Double) obj[0])
                .collect(Collectors.toList());

        double totalAmount = filteredExpenses.stream().mapToDouble(Double::doubleValue).sum();

        return new MonthlyExpenseSumResponseDto(year, month, totalAmount);
    }

    @Override
    public Double getTotalExpenseSum() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM Expense";
        return jdbcTemplate.queryForObject(sql, Double.class);
    }

    @Override
    public List<Expense> findByCategoryName(String name) {
        String sql = "SELECT * FROM expenses WHERE category_name = ?";
        Object[] params = {name};
        int[] types = {Types.VARCHAR};

        try {
            return jdbcTemplate.query(sql, params, types, new ExpenseRowMapper());
        } catch (EmptyResultDataAccessException e) {
            // Maneja el caso donde no se encuentra ninguna categoría con el nombre proporcionado
            return Collections.emptyList();
        }
    }


    @Override
    public List<Expense> selectExpenses() {
        // En el caso de recuperar todos los registros, no necesitamos especificar parametros de búsqueda
        return jdbcTemplate.query(SELECT_ALL_EXPENSES, new ExpenseRowMapper());
    }




    // Clase interna que permite mapear cada resultado del ResultSet a las propiedades de la entidad
    static class ExpenseRowMapper implements RowMapper<Expense> {
        @Override
        public Expense mapRow(ResultSet rs, int rowNum) throws SQLException {
            Expense expense = new Expense();
            expense.setId(rs.getLong("id"));
            expense.setAmount(rs.getDouble("amount"));
            expense.setCategoryId(rs.getLong("category_id"));
            expense.setCategoryName(rs.getString("category_name"));
            expense.setDate(rs.getString("date"));
            return expense;
        }
    }

    // Clase interna que permite mapear cada resultado del ResultSet a las propiedades de la entidad
    static class ExpenseCategoryRowMapper implements RowMapper<ExpenseCategory> {
        @Override
        public ExpenseCategory mapRow(ResultSet rs, int rowNum) throws SQLException {
            ExpenseCategory expenseCategory = new ExpenseCategory();
            expenseCategory.setId(rs.getLong("id"));
            expenseCategory.setName(rs.getString("name"));
            return expenseCategory;
        }
    }

}
