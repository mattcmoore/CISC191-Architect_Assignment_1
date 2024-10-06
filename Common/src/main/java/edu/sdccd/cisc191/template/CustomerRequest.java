package edu.sdccd.cisc191.template;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class CustomerRequest {
    private Integer id;
    private Integer task;
    private Integer operation;
    private List params;


    @JsonIgnore
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static String toJSON(CustomerRequest customer) throws Exception {
        return objectMapper.writeValueAsString(customer);
    }
    public static CustomerRequest fromJSON(String input) throws Exception{
        return objectMapper.readValue(input, CustomerRequest.class);
    }
    protected CustomerRequest() {}

    public CustomerRequest(Integer id,Integer task,Integer operation,List params) {
        this.id = id;
        this.task=task;
        this.operation=operation;
        this.params=params;
    }



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTask() {
        return task;
    }

    public void setTask(Integer task) {
        this.task = task;
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }

    public List getParams() {
        return params;
    }

    public void setParams(List params) {
        this.params = params;
    }
}