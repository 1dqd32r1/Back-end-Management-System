package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.factory.UserTestDataFactory;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * UserService单元测试
 * 测试用户服务层的所有业务逻辑
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService单元测试")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = UserTestDataFactory.createDefaultUser();
        testUser.setId(1L);
        testUser.setPassword("encodedPassword");
    }

    @Nested
    @DisplayName("用户查询测试")
    class UserQueryTests {

        @Test
        @DisplayName("应该成功分页查询所有用户")
        void shouldGetAllUsersPaginated() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> expectedPage = new PageImpl<>(
                    Arrays.asList(testUser),
                    pageable,
                    1
            );
            when(userRepository.findAll(pageable)).thenReturn(expectedPage);

            // When
            Page<User> result = userService.getAllUsers(pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
            verify(userRepository).findAll(pageable);
        }

        @Test
        @DisplayName("应该根据ID成功查询用户")
        void shouldGetUserById() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.getUserById(1L);

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("testuser");
            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("查询不存在的用户ID应该返回空Optional")
        void shouldReturnEmptyWhenUserNotFound() {
            // Given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // When
            Optional<User> result = userService.getUserById(999L);

            // Then
            assertThat(result).isEmpty();
            verify(userRepository).findById(999L);
        }

        @Test
        @DisplayName("应该成功搜索用户")
        void shouldSearchUsers() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> expectedPage = new PageImpl<>(
                    Arrays.asList(testUser),
                    pageable,
                    1
            );
            when(userRepository.searchUsers("test", "ACTIVE", pageable))
                    .thenReturn(expectedPage);

            // When
            Page<User> result = userService.searchUsers("test", "ACTIVE", pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            verify(userRepository).searchUsers("test", "ACTIVE", pageable);
        }

        @Test
        @DisplayName("应该根据状态查询用户")
        void shouldGetUsersByStatus() {
            // Given
            List<User> expectedUsers = Arrays.asList(testUser);
            when(userRepository.findActiveByStatus("0")).thenReturn(expectedUsers);

            // When
            List<User> result = userService.getUsersByStatus("0");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo("0");
            verify(userRepository).findActiveByStatus("0");
        }

        @Test
        @DisplayName("应该查询所有活跃用户")
        void shouldGetAllActiveUsers() {
            // Given
            User activeUser = UserTestDataFactory.createDefaultUser();
            List<User> expectedUsers = Arrays.asList(activeUser);
            when(userRepository.findAllActiveUsers()).thenReturn(expectedUsers);

            // When
            List<User> result = userService.getAllActiveUsers();

            // Then
            assertThat(result).hasSize(1);
            verify(userRepository).findAllActiveUsers();
        }

        @Test
        @DisplayName("应该根据用户名查询活跃用户")
        void shouldGetActiveUserByUsername() {
            // Given
            when(userRepository.findActiveByUsername("testuser"))
                    .thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.getActiveUserByUsername("testuser");

            // Then
            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("testuser");
            verify(userRepository).findActiveByUsername("testuser");
        }
    }

    @Nested
    @DisplayName("用户创建测试")
    class UserCreationTests {

        @Test
        @DisplayName("应该成功创建新用户")
        void shouldCreateUserSuccessfully() {
            // Given
            User newUser = User.builder()
                    .username("newuser")
                    .email("new@example.com")
                    .password("password123")
                    .build();

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            lenient().when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            lenient().when(userRepository.existsByPhone(anyString())).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });

            // When
            User result = userService.createUser(newUser);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getPassword()).isEqualTo("encodedPassword");
            assertThat(result.getDelFlag()).isEqualTo("0");

            verify(userRepository).existsByUsername("newuser");
            verify(passwordEncoder).encode("password123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("创建已存在用户名的用户应该抛出异常")
        void shouldThrowExceptionWhenUsernameExists() {
            // Given
            User newUser = UserTestDataFactory.createDefaultUser();
            when(userRepository.existsByUsername("testuser")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.createUser(newUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("用户名已存在");

            verify(userRepository).existsByUsername("testuser");
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("创建已存在邮箱的用户应该抛出异常")
        void shouldThrowExceptionWhenEmailExists() {
            // Given
            User newUser = User.builder()
                    .username("newuser")
                    .email("test@example.com")
                    .build();
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            lenient().when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.createUser(newUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("邮箱已被注册");

            verify(userRepository).existsByEmail("test@example.com");
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("创建已存在手机号的用户应该抛出异常")
        void shouldThrowExceptionWhenPhoneExists() {
            // Given
            User newUser = User.builder()
                    .username("newuser")
                    .phone("13800138000")
                    .build();
            lenient().when(userRepository.existsByUsername("newuser")).thenReturn(false);
            lenient().when(userRepository.existsByEmail(anyString())).thenReturn(false);
            when(userRepository.existsByPhone("13800138000")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.createUser(newUser))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("手机号已被注册");

            verify(userRepository).existsByPhone("13800138000");
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("用户更新测试")
    class UserUpdateTests {

        @Test
        @DisplayName("应该成功更新用户信息")
        void shouldUpdateUserSuccessfully() {
            // Given
            User updateDetails = User.builder()
                    .username("updateduser")
                    .email("updated@example.com")
                    .phone("13900139000")
                    .nickname("Updated Nickname")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("updateduser")).thenReturn(false);
            when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
            when(userRepository.existsByPhone("13900139000")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            User result = userService.updateUser(1L, updateDetails);

            // Then
            assertThat(result).isNotNull();
            assertThat(testUser.getUsername()).isEqualTo("updateduser");
            assertThat(testUser.getEmail()).isEqualTo("updated@example.com");
            assertThat(testUser.getNickname()).isEqualTo("Updated Nickname");
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("更新不存在的用户应该抛出异常")
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {
            // Given
            User updateDetails = UserTestDataFactory.createUserForUpdate();
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.updateUser(999L, updateDetails))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("用户不存在");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("更新到已存在的用户名应该抛出异常")
        void shouldThrowExceptionWhenUpdatingToExistingUsername() {
            // Given
            User updateDetails = User.builder()
                    .username("existinguser")
                    .build();

            User existingUser = User.builder()
                    .username("testuser")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.updateUser(1L, updateDetails))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("用户名已存在");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("更新到已存在的邮箱应该抛出异常")
        void shouldThrowExceptionWhenUpdatingToExistingEmail() {
            // Given
            User updateDetails = User.builder()
                    .username("testuser")
                    .email("existing@example.com")
                    .build();

            User existingUser = User.builder()
                    .username("testuser")
                    .email("test@example.com")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> userService.updateUser(1L, updateDetails))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("邮箱已被注册");

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("用户删除测试")
    class UserDeletionTests {

        @Test
        @DisplayName("应该成功软删除用户")
        void shouldDeleteUserSuccessfully() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            userService.deleteUser(1L);

            // Then
            assertThat(testUser.getDelFlag()).isEqualTo("1");
            verify(userRepository).save(testUser);
        }

        @Test
        @DisplayName("删除不存在的用户应该抛出异常")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            // Given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> userService.deleteUser(999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("用户不存在");

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("密码管理测试")
    class PasswordManagementTests {

        @Test
        @DisplayName("应该成功修改密码")
        void shouldChangePasswordSuccessfully() {
            // Given
            User userWithPassword = User.builder()
                    .id(1L)
                    .username("testuser")
                    .password("oldEncodedPassword")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(userWithPassword));
            when(passwordEncoder.matches("oldPassword", "oldEncodedPassword"))
                    .thenReturn(true);
            when(passwordEncoder.encode("newPassword"))
                    .thenReturn("newEncodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(userWithPassword);

            // When
            userService.changePassword(1L, "oldPassword", "newPassword");

            // Then
            assertThat(userWithPassword.getPassword()).isEqualTo("newEncodedPassword");
            verify(passwordEncoder).matches("oldPassword", "oldEncodedPassword");
            verify(passwordEncoder).encode("newPassword");
            verify(userRepository).save(userWithPassword);
        }

        @Test
        @DisplayName("使用错误原密码修改密码应该抛出异常")
        void shouldThrowExceptionWhenOldPasswordIncorrect() {
            // Given
            User userWithPassword = User.builder()
                    .id(1L)
                    .password("oldEncodedPassword")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(userWithPassword));
            when(passwordEncoder.matches("wrongPassword", "oldEncodedPassword"))
                    .thenReturn(false);

            // When & Then
            assertThatThrownBy(() ->
                    userService.changePassword(1L, "wrongPassword", "newPassword"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("原密码错误");

            verify(passwordEncoder, never()).encode(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("应该成功重置密码")
        void shouldResetPasswordSuccessfully() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.encode("newPassword"))
                    .thenReturn("newEncodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            userService.resetPassword(1L, "newPassword");

            // Then
            assertThat(testUser.getPassword()).isEqualTo("newEncodedPassword");
            verify(passwordEncoder).encode("newPassword");
            verify(userRepository).save(testUser);
        }
    }

    @Nested
    @DisplayName("登录信息更新测试")
    class LoginInfoUpdateTests {

        @Test
        @DisplayName("应该成功更新登录信息")
        void shouldUpdateLoginInfoSuccessfully() {
            // Given
            String loginIp = "192.168.1.100";
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            userService.updateLoginInfo(1L, loginIp);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();

            assertThat(savedUser.getLoginIp()).isEqualTo(loginIp);
            assertThat(savedUser.getLastLoginTime()).isNotNull();
            assertThat(savedUser.getLastLoginTime()).isBefore(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("为不存在的用户更新登录信息应该抛出异常")
        void shouldThrowExceptionWhenUpdatingLoginInfoForNonExistentUser() {
            // Given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() ->
                    userService.updateLoginInfo(999L, "192.168.1.100"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("用户不存在");

            verify(userRepository, never()).save(any(User.class));
        }
    }
}
