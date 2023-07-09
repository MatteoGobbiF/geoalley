package com.mgf.geoalley.service;

import com.mgf.geoalley.exceptions.UserNotFoundException;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;

    private UserServiceImpl userServiceImplUnderTest;

    @BeforeEach
    public void setUp() {
        userServiceImplUnderTest = new UserServiceImpl(mockUserRepository);
    }

    @Test
    public void testCheckCredentials() {

        final Optional<User> user = Optional.of(new User("username", "email", "password"));
        when(mockUserRepository.findByUsernameAndPassword("username", "password")).thenReturn(user);

        final Optional<User> result = userServiceImplUnderTest.checkCredentials("username", "password");

    }

    @Test
    public void testCheckCredentials_UserRepositoryReturnsAbsent() {

        when(mockUserRepository.findByUsernameAndPassword("username", "password")).thenReturn(Optional.empty());

        final Optional<User> result = userServiceImplUnderTest.checkCredentials("username", "password");

        assertThat(result).isNull();
    }


    @Test
    public void testShowProfile_UserRepositoryReturnsAbsent() {

    	final Model model = new ConcurrentModel();
        final HttpServletRequest request = null;
        when(mockUserRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceImplUnderTest.showProfile(0, model, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testShowEditProfile_UserRepositoryReturnsAbsent() {

    	final Model model = new ConcurrentModel();
        final HttpServletRequest request = null;
        when(mockUserRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceImplUnderTest.showEditProfile(0, model, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testFindUserAndCheckPermissions_UserRepositoryReturnsAbsent() {

    	final HttpServletRequest request = null;
        when(mockUserRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceImplUnderTest.findUserAndCheckPermissions(0, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testEditProfile_UserRepositoryFindByIdReturnsAbsent() {

    	final Model model = new ConcurrentModel();
        final HttpServletRequest request = null;
        when(mockUserRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceImplUnderTest.editProfile(0, model, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testEditPassword_UserRepositoryFindByIdReturnsAbsent() {

    	final Model model = new ConcurrentModel();
        final HttpServletRequest request = null;
        when(mockUserRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userServiceImplUnderTest.editPassword(0, model, request))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void testGetAllUsers() {

    	final List<User> users = List.of(new User("username", "email", "password"));
        when(mockUserRepository.findAll()).thenReturn(users);

        final List<User> result = userServiceImplUnderTest.getAllUsers();

    }

    @Test
    public void testGetAllUsers_UserRepositoryReturnsNoItems() {
        when(mockUserRepository.findAll()).thenReturn(Collections.emptyList());

        final List<User> result = userServiceImplUnderTest.getAllUsers();

        assertThat(result).isEqualTo(Collections.emptyList());
    }
}
