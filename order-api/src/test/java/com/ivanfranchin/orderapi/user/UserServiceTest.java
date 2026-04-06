package com.ivanfranchin.orderapi.user;

import com.ivanfranchin.orderapi.security.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    // -- getUsers --

    @Test
    void getUsers_returnsAllUsers() {
        User user1 = new User("alice", "pass", "Alice", "alice@example.com", Role.USER);
        User user2 = new User("bob", "pass", "Bob", "bob@example.com", Role.ADMIN);
        when(userRepository.findAllByOrderByUsernameAsc()).thenReturn(List.of(user1, user2));

        List<User> result = userService.getUsers();

        assertThat(result).hasSize(2).containsExactly(user1, user2);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUsers_returnsEmptyListWhenNoUsers() {
        when(userRepository.findAllByOrderByUsernameAsc()).thenReturn(List.of());

        List<User> result = userService.getUsers();

        assertThat(result).isEmpty();
        verifyNoMoreInteractions(userRepository);
    }

    // -- countUsers --

    @Test
    void countUsers_delegatesToRepository() {
        when(userRepository.count()).thenReturn(5L);

        assertThat(userService.countUsers()).isEqualTo(5L);
        verifyNoMoreInteractions(userRepository);
    }

    // -- getUserByUsername --

    @Test
    void getUserByUsername_returnsUserWhenFound() {
        User user = new User("alice", "pass", "Alice", "alice@example.com", Role.USER);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByUsername("alice");

        assertThat(result).isPresent().contains(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void getUserByUsername_returnsEmptyWhenNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserByUsername("unknown");

        assertThat(result).isEmpty();
        verifyNoMoreInteractions(userRepository);
    }

    // -- hasUserWithUsername --

    @Test
    void hasUserWithUsername_returnsTrueWhenExists() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        assertThat(userService.hasUserWithUsername("alice")).isTrue();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void hasUserWithUsername_returnsFalseWhenNotExists() {
        when(userRepository.existsByUsername("ghost")).thenReturn(false);

        assertThat(userService.hasUserWithUsername("ghost")).isFalse();
        verifyNoMoreInteractions(userRepository);
    }

    // -- hasUserWithEmail --

    @Test
    void hasUserWithEmail_returnsTrueWhenExists() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThat(userService.hasUserWithEmail("alice@example.com")).isTrue();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void hasUserWithEmail_returnsFalseWhenNotExists() {
        when(userRepository.existsByEmail("ghost@example.com")).thenReturn(false);

        assertThat(userService.hasUserWithEmail("ghost@example.com")).isFalse();
        verifyNoMoreInteractions(userRepository);
    }

    // -- validateAndGetUserByUsername --

    @Test
    void validateAndGetUserByUsername_returnsUserWhenFound() {
        User user = new User("alice", "pass", "Alice", "alice@example.com", Role.USER);
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        User result = userService.validateAndGetUserByUsername("alice");

        assertThat(result).isEqualTo(user);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void validateAndGetUserByUsername_throwsWhenNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.validateAndGetUserByUsername("ghost"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("ghost");
        verifyNoMoreInteractions(userRepository);
    }

    // -- saveUser --

    @Test
    void saveUser_delegatesToRepositoryAndReturnsUser() {
        User user = new User("alice", "pass", "Alice", "alice@example.com", Role.USER);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.saveUser(user);

        assertThat(result).isEqualTo(user);
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    // -- countAdmins --

    @Test
    void countAdmins_delegatesToRepository() {
        when(userRepository.countByRole(Role.ADMIN)).thenReturn(2L);

        assertThat(userService.countAdmins()).isEqualTo(2L);
        verifyNoMoreInteractions(userRepository);
    }

    // -- deleteUser --

    @Test
    void deleteUser_delegatesToRepository() {
        User user = new User("alice", "pass", "Alice", "alice@example.com", Role.USER);

        userService.deleteUser(user);

        verify(userRepository).delete(user);
        verifyNoMoreInteractions(userRepository);
    }
}
