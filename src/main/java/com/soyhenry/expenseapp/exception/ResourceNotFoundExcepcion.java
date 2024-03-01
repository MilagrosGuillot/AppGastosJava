package com.soyhenry.expenseapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



@ResponseStatus(HttpStatus.NOT_FOUND) //va a ser enfocada a un notFound
public class ResourceNotFoundExcepcion extends RuntimeException {

    private String resourceName;      //de donde viene la consulta /expense
    private String fieldName; //recibe el nombre de la columna a consultar(id, variables)

    private Object fieldValue; //recibe un objeto del campo que estoy obteniendo(valor del id, variable)

    public ResourceNotFoundExcepcion(String fieldName, Object fieldValue) {
        super(String.format("%s no fue encontrado con: %s ", fieldName, fieldValue));
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundExcepcion(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no fue encontrado con: %s %s", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public ResourceNotFoundExcepcion(String resourceName) {
        super(String.format("No hay registros de %s en el sistema", resourceName));
        this.resourceName = resourceName;
    }
}
