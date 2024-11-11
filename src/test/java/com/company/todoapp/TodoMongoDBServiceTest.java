package com.company.todoapp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.company.todoapp.exceptions.TodoNotFoundException;
import com.company.todoapp.models.Todo;
import com.company.todoapp.repository.mongodb.TodoMongoRepository;
import com.company.todoapp.services.TodoMongoDBService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TodoMongoDBServiceTest {

    // Mocking the TodoMongoRepository to simulate database interactions
    @Mock
    private TodoMongoRepository todoMongoRepo;

    // Injecting the mocked repository into the service
    @InjectMocks
    private TodoMongoDBService todoMongoDBService;

    @BeforeEach
    public void setUp() {
        // Initializing the mocks before each test
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllTodos() {
        Todo todo1 = new Todo("1", "Title1", "Description1", false, new Date());
        Todo todo2 = new Todo("2", "Title2", "Description2", true, new Date());
        List<Todo> todos = List.of(todo1, todo2);

        // Stubbing the findAll method
        when(todoMongoRepo.findAll()).thenReturn(todos);

        List<Todo> result = todoMongoDBService.getAllTodos();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(todo1, result.get(0));
        assertEquals(todo2, result.get(1));
    }

    @Test
    public void testGetById() {
        Todo todo = new Todo("1", "Title", "Description", true, new Date());

        // Stubbing the findById method
        when(todoMongoRepo.findById("1")).thenReturn(Optional.of(todo));

        Todo result = todoMongoDBService.getById("1");
        assertNotNull(result);
        assertEquals(todo, result);
    }

    @Test
    public void testGetByIdNotFound() {
        // Stubbing the findById method to return an empty Optional
        when(todoMongoRepo.findById("1")).thenReturn(Optional.empty());

        Todo result = todoMongoDBService.getById("1");
        assertNull(result);
    }

    @Test
    public void testGetByTitle() {
        Todo todo = new Todo("1", "Title", "Description", true, new Date());

        // Stubbing the findByTitle method
        when(todoMongoRepo.findByTitle("Title")).thenReturn(List.of(todo));

        List<Todo> result = todoMongoDBService.getByTitle("Title");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(todo, result.get(0));
    }

    @Test
    public void testGetByTitleNotFound() {
        // Stubbing the findByTitle method to return null
        when(todoMongoRepo.findByTitle("Title")).thenReturn(null);

        assertThrows(TodoNotFoundException.class, () -> todoMongoDBService.getByTitle("Title"));
    }

    @Test
    public void testAddTodo() {
        Todo todo = new Todo(null, "Title", "Description", true, new Date());
        Todo savedTodo = new Todo("1", "Title", "Description", true, new Date());

        // Stubbing the save method
        when(todoMongoRepo.save(any(Todo.class))).thenReturn(savedTodo);

        Todo result = todoMongoDBService.addTodo(todo);
        assertNotNull(result);
        assertEquals("1", result.getId()); // Assuming UUID generation works correctly
        assertEquals(savedTodo, result);
    }

    @Test
    public void testUpdateTodo() {
        Todo existingTodo = new Todo("1", "Old Title", "Old Description", false, new Date());
        Todo updatedTodo = new Todo("1", "New Title", "New Description", true, new Date());

        // Stubbing the findById method
        when(todoMongoRepo.findById("1")).thenReturn(Optional.of(existingTodo));
        // Stubbing the save method
        when(todoMongoRepo.save(any(Todo.class))).thenReturn(updatedTodo);

        Todo result = todoMongoDBService.updateTodo("1", updatedTodo);
        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        assertTrue(result.isStatus());
    }

    @Test
    public void testUpdateTodoNotFound() {
        Todo todo = new Todo("1", "Title", "Description", true, new Date());

        // Stubbing the findById method to return an empty Optional
        when(todoMongoRepo.findById("1")).thenReturn(Optional.empty());

        Todo result = todoMongoDBService.updateTodo("1", todo);
        assertNull(result);
    }

    @Test
    public void testDeleteTodo() {
        Todo todo = new Todo("1", "Title", "Description", true, new Date());

        // Stubbing the findById method
        when(todoMongoRepo.findById("1")).thenReturn(Optional.of(todo));

        Todo result = todoMongoDBService.deleteTodo("1");
        assertNotNull(result);
        assertEquals(todo, result);
        // Verifying the deleteById method was called
        verify(todoMongoRepo).deleteById("1");
    }

    @Test
    public void testDeleteTodoNotFound() {
        // Stubbing the findById method to return an empty Optional
        when(todoMongoRepo.findById("1")).thenReturn(Optional.empty());

        Todo result = todoMongoDBService.deleteTodo("1");
        assertNull(result);
        // Verifying the deleteById method was never called
        verify(todoMongoRepo, never()).deleteById(anyString());
    }
}