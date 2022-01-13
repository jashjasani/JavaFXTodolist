package com.example.todolist;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class TodoData {
    private static TodoData instance = new TodoData();
    private static String filename = "TodoListItems.txt";

    private ObservableList<TodolistItem> todoItems;
    private DateTimeFormatter formatter;

    public static TodoData getInstance(){
        return instance;
    }

    private TodoData(){
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public ObservableList<TodolistItem> getTodoItems() {
        return todoItems;
    }

    public void loadTodoItems() throws IOException {
        todoItems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);

        String input;

        try{
            while ((input= br.readLine())!=null){
                String[] itemPieces = input.split("\t");

                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString = itemPieces[2];

                LocalDate date = LocalDate.parse(dateString,formatter);
                TodolistItem todolistItem = new TodolistItem(shortDescription,details,date);

                todoItems.add(todolistItem);
            }
        } finally {
            if (br!=null){
                br.close();
            }
        }
    }

    public void storeTodoItems() throws IOException{
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);

        try{
            Iterator<TodolistItem> iter = todoItems.iterator();
            while (iter.hasNext()){
                TodolistItem item = iter.next();
                bw.write(String.format("%s\t%s\t%s\t",
                        item.getDescription(),
                        item.getDetails(),
                        item.getDeadline().format(formatter)));
            bw.newLine();
            }
        } finally {
            if (bw!=null){
                bw.close();
            }
        }
    }

    public void addTodoItem(TodolistItem todolistItem) {
        todoItems.add(todolistItem);
    }

    public void deleteTodoItem(TodolistItem item) {
        todoItems.remove(item);
    }
}
