package com.devanmejia.appmanager.repository;

import com.devanmejia.appmanager.repository.app.AppRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@ExtendWith(SpringExtension.class)
public class AppRepositoryTest {
    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:s");

    private final AppRepository appRepository;

    @Autowired
    public AppRepositoryTest(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    @Test
    public void findAllByUserEmail_Test() {
        var pageable = PageRequest.of(0, 3);
        var actual = appRepository.findAllByUserId(1, pageable);
        assertEquals(3, actual.stream().count());
        pageable = PageRequest.of(1, 3);
        actual = appRepository.findAllByUserId(1, pageable);
        assertEquals(1, actual.stream().count());
        pageable = PageRequest.of(2, 3);
        actual = appRepository.findAllByUserId(1, pageable);
        assertEquals(0, actual.stream().count());
    }

    @Test
    public void findAllByUserEmail_When_AppList_Is_Empty_Test() {
        var pageable = PageRequest.of(0, 3);
        var actual = appRepository.findAllByUserId(2, pageable);
        assertEquals(0, actual.stream().count());
        pageable = PageRequest.of(1, 3);
        actual = appRepository.findAllByUserId(2, pageable);
        assertEquals(0, actual.stream().count());
    }

    @Test
    public void findAllByUserEmail_When_User_Does_Not_Exist_Test() {
        var pageable = PageRequest.of(0, 3);
        var actual = appRepository.findAllByUserId(9, pageable);
        assertEquals(0, actual.stream().count());
        pageable = PageRequest.of(1, 3);
        actual = appRepository.findAllByUserId(9, pageable);
        assertEquals(0, actual.stream().count());
    }

    @Test
    public void getUserAppsAmount_Test() {
        var actual = appRepository.getUserAppsAmount(1);
        assertEquals(4, actual);
    }

    @Test
    public void getUserAppsAmount_When_NotesList_Is_Empty_Test() {
        var actual = appRepository.getUserAppsAmount(2);
        assertEquals(0, actual);
    }

    @Test
    public void getUserAppsAmount_When_User_Does_Not_Exist_Test() {
        var actual = appRepository.getUserAppsAmount(9);
        assertEquals(0, actual);
    }

    @Test
    public void findUserAppById_Test() {
        var actualOptional = appRepository.findUserAppById(1, 1);
        assertTrue(actualOptional.isPresent());
        var actual = actualOptional.get();
        assertEquals("Simple CRUD App", actual.getName());
        assertEquals("13.03.2022 03:14:7", actual.getCreationTime().format(DATE_FORMATTER));
    }

    @Test
    public void findUserAppById_When_User_Does_Not_Exist_Test() {
        var actual = appRepository.findUserAppById(1, 9);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void findUserAppById_When_Note_Does_Not_Exist_Test() {
        var actual = appRepository.findUserAppById(7, 1);
        assertTrue(actual.isEmpty());
    }
}
