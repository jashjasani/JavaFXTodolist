package com.example.todolist;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


public class HelloController {
    @FXML
    private TextArea textArea;
    private List<TodolistItem> todolistItems;
    @FXML
    private ListView todolistView;
    @FXML
    private Label date;
    @FXML
    private BorderPane mainBorderpane;
    @FXML
    private ToggleButton filterToggleButton;
    @FXML
    private ContextMenu listContextMenu;
    FilteredList<TodolistItem> filteredList = new FilteredList<TodolistItem>(TodoData.getInstance().getTodoItems(),
            new Predicate<TodolistItem>() {
                @Override
                public boolean test(TodolistItem item) {
                    return true;
                }
            });
    public void showNewItemDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderpane.getScene().getWindow());
        dialog.setTitle("Add new to do item");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("dialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e){
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            DialogController controller = fxmlLoader.getController();
            TodolistItem newItem = controller.processResults();
            //todolistView.getItems().setAll(TodoData.getInstance().getTodoItems());
            if (newItem!=null){
                todolistView.getSelectionModel().select(newItem);
            }
        }
    }
    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TodolistItem item =(TodolistItem) todolistView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });
        listContextMenu.getItems().addAll(deleteMenuItem);
        todolistView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodolistItem>() {
            @Override
            public void changed(ObservableValue<? extends TodolistItem> observableValue, TodolistItem o, TodolistItem t1) {
                if(t1!=null){
                    TodolistItem item = (TodolistItem) todolistView.getSelectionModel().getSelectedItem();
                    textArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    date.setText(df.format(item.getDeadline()));
                }
            }
        });
        SortedList<TodolistItem> sortedList = new SortedList<>(filteredList,
                new Comparator<TodolistItem>() {
                    @Override
                    public int compare(TodolistItem o1, TodolistItem o2) {
                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                });
//        todolistView.setItems(TodoData.getInstance().getTodoItems());
        todolistView.setItems(sortedList);
        todolistView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todolistView.getSelectionModel().selectFirst();
        todolistView.setCellFactory(new Callback<ListView, ListCell>() {
            @Override
            public ListCell<TodolistItem> call(ListView listView) {
                ListCell<TodolistItem> cell = new ListCell<TodolistItem>(){
                    @Override
                    protected void updateItem(TodolistItem todolistItem, boolean b) {
                        super.updateItem(todolistItem, b);
                        if(b){
                            setText(null);
                        } else {
                            setText(todolistItem.getDescription());
                            if(todolistItem.getDeadline().isBefore(LocalDate.now().plusDays(1))){
                                setTextFill(Color.RED);
                            } else if(todolistItem.getDeadline().equals(LocalDate.now().plusDays(1))){
                                setTextFill(Color.ORANGE);
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs,wasEmpty,isNowEmpty)->{
                            if (isNowEmpty){
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );
                return cell;
            }
        });
    }
    public void handleClick(){
        TodolistItem item=(TodolistItem) todolistView.getSelectionModel().getSelectedItem();
        textArea.setText(item.getDetails());
        date.setText(item.getDeadline().toString());
    }
    public void deleteItem(TodolistItem item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete item : " + item.getDescription());
        alert.setContentText("Are you sure ? Press Ok to confirm , or cancel to Back out.");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get()==ButtonType.OK){
            TodoData.getInstance().deleteTodoItem(item);
        }
    }
    public void handleKeyPressed(KeyEvent keyEvent){
        TodolistItem selectedItem = (TodolistItem) todolistView.getSelectionModel().getSelectedItem();
        if (selectedItem!=null){
            if(keyEvent.getCode().equals(KeyCode.DELETE)){
                deleteItem(selectedItem);
            }
        }
    }
    public void handleFilterButton(){
        if(filterToggleButton.isSelected()){
            filteredList.setPredicate(new Predicate<TodolistItem>() {
                @Override
                public boolean test(TodolistItem item) {
                    if(item.getDeadline().equals(LocalDate.now())){
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        } else {
            filteredList.setPredicate(new Predicate<TodolistItem>() {
                @Override
                public boolean test(TodolistItem item) {
                    return true;
                }
            });
        }
    }
}
