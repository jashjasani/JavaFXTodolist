package com.example.todolist;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescription;
    @FXML
    private TextArea details;
    @FXML
    private DatePicker deadline;

    public TodolistItem processResults() {
        String sd = shortDescription.getText().trim();
        String d = details.getText().trim();
        LocalDate dl = deadline.getValue();
        if (sd != null && d != null && dl != null) {
            TodolistItem newItem = new TodolistItem(sd, d, dl);
            TodoData.getInstance().addTodoItem(newItem);
            return newItem;
        }
        return null;
    }
}