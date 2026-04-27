package com.example.demo.controller;

import com.example.demo.common.Result;
import com.example.demo.config.TestSecurityConfig;
import com.example.demo.entity.User;
import com.example.demo.factory.UserTestDataFactory;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserController API集成测试
 * 测试用户管理API端点
 */
@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@DisplayName("UserController API集成测试")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testUser = UserTestDataFactory.createDefaultUser();
        testUser.setId(1L);
        testUser.setPassword("encodedPassword");
        testUser.setCreateTime(LocalDateTime.now());

        adminUser = User.builder()
                .id(2L)
                .username("admin")
                .email("admin@example.com")
                .role("admin")
                .status("0")
                .delFlag("0")
                .build();
    }

    @Nested
    @DisplayName("用户列表查询API测试")
    class UserListApiTests {

        @Test
        @WithMockUser
        @DisplayName("GET /api/users - 应该返回分页用户列表")
        void shouldReturnPaginatedUserList() throws Exception {
            // Given
            Page<User> userPage = new PageImpl<>(
                    Arrays.asList(testUser, adminUser),
                    PageRequest.of(0, 10),
                    2
            );
            when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

            // When & Then
            mockMvc.perform(get("/api/users")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sortBy", "createTime")
                            .param("sortDir", "desc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].username").value("testuser"))
                    .andExpect(jsonPath("$.data.totalElements").value(2))
                    .andExpect(jsonPath("$.data.totalPages").value(1))
                    .andExpect(jsonPath("$.data.currentPage").value(0))
                    .andExpect(jsonPath("$.data.pageSize").value(10));

            verify(userService).getAllUsers(any(Pageable.class));
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/users - 应该支持关键词搜索")
        void shouldSearchUsersWithKeyword() throws Exception {
            // Given
            Page<User> userPage = new PageImpl<>(
                    Arrays.asList(testUser),
                    PageRequest.of(0, 10),
                    1
            );
            when(userService.searchUsers(eq("test"), eq("0"), any(Pageable.class)))
                    .thenReturn(userPage);

            // When & Then
            mockMvc.perform(get("/api/users")
                            .param("keyword", "test")
                            .param("status", "0")
                            .param("page", "0")
                            .param("size", "10")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].username").value("testuser"));

            verify(userService).searchUsers(eq("test"), eq("0"), any(Pageable.class));
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/users - 应该支持升序排序")
        void shouldSupportAscendingSort() throws Exception {
            // Given
            Page<User> userPage = new PageImpl<>(
                    Arrays.asList(testUser),
                    PageRequest.of(0, 10),
                    1
            );
            when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

            // When & Then
            mockMvc.perform(get("/api/users")
                            .param("sortBy", "username")
                            .param("sortDir", "asc")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(userService).getAllUsers(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("用户详情查询API测试")
    class UserDetailApiTests {

        @Test
        @WithMockUser
        @DisplayName("GET /api/users/{id} - 应该返回指定ID的用户")
        void shouldReturnUserById() throws Exception {
            // Given
            when(userService.getUserById(1L)).thenReturn(java.util.Optional.of(testUser));

            // When & Then
            mockMvc.perform(get("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.username").value("testuser"))
                    .andExpect(jsonPath("$.data.email").value("test@example.com"));

            verify(userService).getUserById(1L);
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/users/{id} - 查询不存在的用户应返回错误")
        void shouldReturnErrorWhenUserNotFound() throws Exception {
            // Given
            when(userService.getUserById(999L)).thenReturn(java.util.Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/users/999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("用户不存在"));

            verify(userService).getUserById(999L);
        }
    }

    @Nested
    @DisplayName("用户创建API测试")
    class UserCreationApiTests {

        @Test
        @WithMockUser
        @DisplayName("POST /api/users - 应该成功创建用户")
        void shouldCreateUserSuccessfully() throws Exception {
            // Given
            User newUser = User.builder()
                    .username("newuser")
                    .email("new@example.com")
                    .password("password123")
                    .phone("13900139000")
                    .build();

            User createdUser = User.builder()
                    .id(3L)
                    .username("newuser")
                    .email("new@example.com")
                    .phone("13900139000")
                    .status("0")
                    .delFlag("0")
                    .build();

            when(userService.createUser(any(User.class))).thenReturn(createdUser);

            // When & Then
            mockMvc.perform(post("/api/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newUser)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(3))
                    .andExpect(jsonPath("$.data.username").value("newuser"))
                    .andExpect(jsonPath("$.data.email").value("new@example.com"));

            verify(userService).createUser(any(User.class));
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/users - 创建已存在用户应返回错误")
        void shouldReturnErrorWhenCreatingExistingUser() throws Exception {
            // Given
            User existingUser = User.builder()
                    .username("testuser")
                    .email("test@example.com")
                    .build();

            when(userService.createUser(any(User.class)))
                    .thenThrow(new RuntimeException("用户名已存在"));

            // When & Then
            mockMvc.perform(post("/api/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(existingUser)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("用户名已存在"));

            verify(userService).createUser(any(User.class));
        }

        @Test
        @WithMockUser
        @DisplayName("POST /api/users - 验证必填字段")
        void shouldValidateRequiredFields() throws Exception {
            // Given
            Map<String, Object> invalidUser = new HashMap<>();
            invalidUser.put("email", "test@example.com");
            // 缺少username

            // When & Then
            mockMvc.perform(post("/api/users")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidUser)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());

            verify(userService, never()).createUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("用户更新API测试")
    class UserUpdateApiTests {

        @Test
        @WithMockUser
        @DisplayName("PUT /api/users/{id} - 应该成功更新用户")
        void shouldUpdateUserSuccessfully() throws Exception {
            // Given
            User updateData = User.builder()
                    .username("updateduser")
                    .email("updated@example.com")
                    .nickname("Updated Nickname")
                    .phone("13900139000")
                    .build();

            User updatedUser = User.builder()
                    .id(1L)
                    .username("updateduser")
                    .email("updated@example.com")
                    .nickname("Updated Nickname")
                    .phone("13900139000")
                    .status("0")
                    .delFlag("0")
                    .build();

            when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

            // When & Then
            mockMvc.perform(put("/api/users/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateData)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.username").value("updateduser"))
                    .andExpect(jsonPath("$.data.nickname").value("Updated Nickname"));

            verify(userService).updateUser(eq(1L), any(User.class));
        }

        @Test
        @WithMockUser
        @DisplayName("PUT /api/users/{id} - 更新不存在的用户应返回错误")
        void shouldReturnErrorWhenUpdatingNonExistentUser() throws Exception {
            // Given
            User updateData = User.builder()
                    .username("nonexistent")
                    .build();

            when(userService.updateUser(eq(999L), any(User.class)))
                    .thenThrow(new RuntimeException("用户不存在"));

            // When & Then
            mockMvc.perform(put("/api/users/999")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateData)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("用户不存在"));

            verify(userService).updateUser(eq(999L), any(User.class));
        }
    }

    @Nested
    @DisplayName("用户删除API测试")
    class UserDeletionApiTests {

        @Test
        @WithMockUser
        @DisplayName("DELETE /api/users/{id} - 应该成功删除用户")
        void shouldDeleteUserSuccessfully() throws Exception {
            // Given
            doNothing().when(userService).deleteUser(1L);

            // When & Then
            mockMvc.perform(delete("/api/users/1")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));

            verify(userService).deleteUser(1L);
        }

        @Test
        @WithMockUser
        @DisplayName("DELETE /api/users/{id} - 删除不存在的用户应返回错误")
        void shouldReturnErrorWhenDeletingNonExistentUser() throws Exception {
            // Given
            doThrow(new RuntimeException("用户不存在")).when(userService).deleteUser(999L);

            // When & Then
            mockMvc.perform(delete("/api/users/999")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(500))
                    .andExpect(jsonPath("$.message").value("用户不存在"));

            verify(userService).deleteUser(999L);
        }
    }

    @Nested
    @DisplayName("按状态查询用户API测试")
    class UserByStatusApiTests {

        @Test
        @WithMockUser
        @DisplayName("GET /api/users/status/{status} - 应该返回指定状态的用户列表")
        void shouldReturnUsersByStatus() throws Exception {
            // Given
            List<User> activeUsers = Arrays.asList(testUser, adminUser);
            when(userService.getUsersByStatus("0")).thenReturn(activeUsers);

            // When & Then
            mockMvc.perform(get("/api/users/status/0")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].username").exists());

            verify(userService).getUsersByStatus("0");
        }

        @Test
        @WithMockUser
        @DisplayName("GET /api/users/status/{status} - 查询已禁用用户")
        void shouldReturnDisabledUsers() throws Exception {
            // Given
            User disabledUser = User.builder()
                    .username("disableduser")
                    .status("1")
                    .delFlag("0")
                    .build();
            List<User> disabledUsers = Arrays.asList(disabledUser);
            when(userService.getUsersByStatus("1")).thenReturn(disabledUsers);

            // When & Then
            mockMvc.perform(get("/api/users/status/1")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].status").value("1"));

            verify(userService).getUsersByStatus("1");
        }
    }

    @Nested
    @DisplayName("安全认证测试")
    class SecurityTests {

        @Test
        @DisplayName("未认证用户访问API应返回401")
        void shouldReturn401ForUnauthenticatedRequest() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(roles = "admin")
        @DisplayName("管理员用户可以访问用户列表")
        void shouldAllowAdminToAccessUserList() throws Exception {
            // Given
            Page<User> userPage = new PageImpl<>(List.of(testUser), PageRequest.of(0, 10), 1);
            when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

            // When & Then
            mockMvc.perform(get("/api/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(userService).getAllUsers(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("异常处理测试")
    class ExceptionHandlingTests {

        @Test
        @WithMockUser
        @DisplayName("服务器内部错误应正确处理")
        void shouldHandleInternalServerError() throws Exception {
            // Given
            when(userService.getAllUsers(any(Pageable.class)))
                    .thenThrow(new RuntimeException("Database connection failed"));

            // When & Then
            mockMvc.perform(get("/api/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @WithMockUser
        @DisplayName("参数验证错误应正确处理")
        void shouldHandleValidationError() throws Exception {
            // When & Then
            mockMvc.perform(get("/api/users")
                            .param("page", "-1")  // 无效的页码
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
