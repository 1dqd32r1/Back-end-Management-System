package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.factory.UserTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepository集成测试
 * 测试数据访问层的数据库操作
 */
@DataJpaTest
@DisplayName("UserRepository集成测试")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User testUser3;

    @BeforeEach
    void setUp() {
        testUser1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .phone("13800138001")
                .status("ACTIVE")
                .nickname("User One")
                .role("USER")
                .delFlag("0")
                .build();

        testUser2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .phone("13800138002")
                .status("ACTIVE")
                .nickname("User Two")
                .role("ADMIN")
                .delFlag("0")
                .build();

        testUser3 = User.builder()
                .username("user3")
                .email("user3@example.com")
                .phone("13800138003")
                .status("DISABLED")
                .nickname("User Three")
                .role("USER")
                .delFlag("1")  // 已删除
                .build();
    }

    @Nested
    @DisplayName("基础CRUD操作测试")
    class BasicCrudTests {

        @Test
        @DisplayName("应该成功保存用户")
        void shouldSaveUser() {
            // When
            User savedUser = userRepository.save(testUser1);

            // Then
            assertThat(savedUser).isNotNull();
            assertThat(savedUser.getId()).isNotNull();
            assertThat(savedUser.getUsername()).isEqualTo("user1");
        }

        @Test
        @DisplayName("应该根据ID查询用户")
        void shouldFindUserById() {
            // Given
            User savedUser = userRepository.save(testUser1);

            // When
            Optional<User> foundUser = userRepository.findById(savedUser.getId());

            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getUsername()).isEqualTo("user1");
        }

        @Test
        @DisplayName("应该查询所有用户")
        void shouldFindAllUsers() {
            // Given
            userRepository.save(testUser1);
            userRepository.save(testUser2);

            // When
            List<User> users = userRepository.findAll();

            // Then
            assertThat(users).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("应该成功更新用户")
        void shouldUpdateUser() {
            // Given
            User savedUser = userRepository.save(testUser1);
            savedUser.setNickname("Updated Nickname");

            // When
            User updatedUser = userRepository.save(savedUser);

            // Then
            assertThat(updatedUser.getNickname()).isEqualTo("Updated Nickname");
        }

        @Test
        @DisplayName("应该成功删除用户")
        void shouldDeleteUser() {
            // Given
            User savedUser = userRepository.save(testUser1);
            Long userId = savedUser.getId();

            // When
            userRepository.deleteById(userId);

            // Then
            Optional<User> deletedUser = userRepository.findById(userId);
            assertThat(deletedUser).isEmpty();
        }
    }

    @Nested
    @DisplayName("按属性查询测试")
    class QueryByPropertyTests {

        @BeforeEach
        void setupData() {
            userRepository.save(testUser1);
            userRepository.save(testUser2);
        }

        @Test
        @DisplayName("应该根据用户名查询用户")
        void shouldFindByUsername() {
            // When
            Optional<User> foundUser = userRepository.findByUsername("user1");

            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getEmail()).isEqualTo("user1@example.com");
        }

        @Test
        @DisplayName("应该根据邮箱查询用户")
        void shouldFindByEmail() {
            // When
            Optional<User> foundUser = userRepository.findByEmail("user2@example.com");

            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getUsername()).isEqualTo("user2");
        }

        @Test
        @DisplayName("应该根据手机号查询用户")
        void shouldFindByPhone() {
            // When
            Optional<User> foundUser = userRepository.findByPhone("13800138001");

            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getUsername()).isEqualTo("user1");
        }

        @Test
        @DisplayName("应该根据状态查询用户")
        void shouldFindByStatus() {
            // When
            List<User> activeUsers = userRepository.findByStatus("ACTIVE");

            // Then
            assertThat(activeUsers).hasSizeGreaterThanOrEqualTo(2);
            assertThat(activeUsers).allMatch(user -> "ACTIVE".equals(user.getStatus()));
        }

        @Test
        @DisplayName("应该根据角色查询用户")
        void shouldFindByRole() {
            // Given
            User adminUser = User.builder()
                    .username("admin")
                    .email("admin@example.com")
                    .role("ADMIN")
                    .delFlag("0")
                    .build();
            userRepository.save(adminUser);

            // When
            List<User> adminUsers = userRepository.findByRole("ADMIN");

            // Then
            assertThat(adminUsers).isNotEmpty();
            assertThat(adminUsers).allMatch(user -> "ADMIN".equals(user.getRole()));
        }

        @Test
        @DisplayName("应该根据用户名模糊查询")
        void shouldFindByUsernameContaining() {
            // When
            List<User> users = userRepository.findByUsernameContainingIgnoreCase("user");

            // Then
            assertThat(users).hasSizeGreaterThanOrEqualTo(2);
        }
    }

    @Nested
    @DisplayName("存在性检查测试")
    class ExistenceCheckTests {

        @BeforeEach
        void setupData() {
            userRepository.save(testUser1);
        }

        @Test
        @DisplayName("应该正确检查用户名是否存在")
        void shouldCheckUsernameExists() {
            // Then
            assertThat(userRepository.existsByUsername("user1")).isTrue();
            assertThat(userRepository.existsByUsername("nonexistent")).isFalse();
        }

        @Test
        @DisplayName("应该正确检查邮箱是否存在")
        void shouldCheckEmailExists() {
            // Then
            assertThat(userRepository.existsByEmail("user1@example.com")).isTrue();
            assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
        }

        @Test
        @DisplayName("应该正确检查手机号是否存在")
        void shouldCheckPhoneExists() {
            // Then
            assertThat(userRepository.existsByPhone("13800138001")).isTrue();
            assertThat(userRepository.existsByPhone("19999999999")).isFalse();
        }
    }

    @Nested
    @DisplayName("复杂查询测试")
    class ComplexQueryTests {

        @BeforeEach
        void setupData() {
            userRepository.save(testUser1);
            userRepository.save(testUser2);
            userRepository.save(testUser3);
        }

        @Test
        @DisplayName("应该根据关键词和状态分页搜索用户")
        void shouldSearchUsersWithKeywordAndStatus() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<User> result = userRepository.searchUsers("user", "ACTIVE", pageable);

            // Then
            assertThat(result.getContent()).isNotEmpty();
            assertThat(result.getContent()).allMatch(user ->
                    "ACTIVE".equals(user.getStatus()) &&
                    (user.getUsername().contains("user") ||
                            user.getEmail().contains("user") ||
                            user.getPhone().contains("user") ||
                            user.getNickname().contains("user"))
            );
        }

        @Test
        @DisplayName("应该仅根据关键词搜索用户")
        void shouldSearchUsersWithKeywordOnly() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<User> result = userRepository.searchUsers("user1", null, pageable);

            // Then
            assertThat(result.getContent()).isNotEmpty();
            assertThat(result.getContent()).allMatch(user ->
                    user.getUsername().contains("user1") ||
                            user.getEmail().contains("user1") ||
                            user.getPhone().contains("user1") ||
                            user.getNickname().contains("user1")
            );
        }

        @Test
        @DisplayName("应该查询所有活跃用户（未删除）")
        void shouldFindAllActiveUsers() {
            // When
            List<User> activeUsers = userRepository.findAllActiveUsers();

            // Then
            assertThat(activeUsers).hasSize(2);
            assertThat(activeUsers).allMatch(user -> "0".equals(user.getDelFlag()));
        }

        @Test
        @DisplayName("应该根据用户名查询活跃用户")
        void shouldFindActiveByUsername() {
            // When
            Optional<User> foundUser = userRepository.findActiveByUsername("user1");

            // Then
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getDelFlag()).isEqualTo("0");
        }

        @Test
        @DisplayName("查询已删除的用户应该返回空")
        void shouldNotFindDeletedUser() {
            // When
            Optional<User> foundUser = userRepository.findActiveByUsername("user3");

            // Then
            assertThat(foundUser).isEmpty();
        }

        @Test
        @DisplayName("应该根据状态和活跃状态查询用户")
        void shouldFindActiveByStatus() {
            // When
            List<User> activeUsers = userRepository.findActiveByStatus("ACTIVE");

            // Then
            assertThat(activeUsers).hasSize(2);
            assertThat(activeUsers).allMatch(user ->
                    "ACTIVE".equals(user.getStatus()) && "0".equals(user.getDelFlag())
            );
        }

        @Test
        @DisplayName("应该根据角色和活跃状态查询用户")
        void shouldFindActiveByRole() {
            // When
            List<User> adminUsers = userRepository.findActiveByRole("ADMIN");

            // Then
            assertThat(adminUsers).hasSize(1);
            assertThat(adminUsers.get(0).getRole()).isEqualTo("ADMIN");
            assertThat(adminUsers.get(0).getDelFlag()).isEqualTo("0");
        }

        @Test
        @DisplayName("应该正确统计用户数量")
        void shouldCountByStatus() {
            // When
            long activeCount = userRepository.countByStatus("ACTIVE");
            long disabledCount = userRepository.countByStatus("DISABLED");

            // Then
            assertThat(activeCount).isGreaterThanOrEqualTo(2);
            assertThat(disabledCount).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("应该正确统计已删除用户数量")
        void shouldCountByDelFlag() {
            // When
            long deletedCount = userRepository.countByDelFlag("1");
            long activeCount = userRepository.countByDelFlag("0");

            // Then
            assertThat(deletedCount).isGreaterThanOrEqualTo(1);
            assertThat(activeCount).isGreaterThanOrEqualTo(2);
        }
    }

    @Nested
    @DisplayName("分页查询测试")
    class PaginationTests {

        @BeforeEach
        void setupData() {
            for (int i = 1; i <= 25; i++) {
                User user = User.builder()
                        .username("pagetest" + i)
                        .email("pagetest" + i + "@example.com")
                        .status("ACTIVE")
                        .delFlag("0")
                        .build();
                userRepository.save(user);
            }
        }

        @Test
        @DisplayName("应该支持分页查询")
        void shouldSupportPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            // When
            Page<User> page = userRepository.findAll(pageable);

            // Then
            assertThat(page.getContent()).hasSize(10);
            assertThat(page.getTotalPages()).isEqualTo(3);
            assertThat(page.getTotalElements()).isGreaterThanOrEqualTo(25);
            assertThat(page.getNumber()).isEqualTo(0);
        }

        @Test
        @DisplayName("应该支持第二页查询")
        void shouldFetchSecondPage() {
            // Given
            Pageable pageable = PageRequest.of(1, 10);

            // When
            Page<User> page = userRepository.findAll(pageable);

            // Then
            assertThat(page.getContent()).hasSize(10);
            assertThat(page.getNumber()).isEqualTo(1);
        }

        @Test
        @DisplayName("应该支持不同页面大小")
        void shouldSupportDifferentPageSize() {
            // Given
            Pageable pageable = PageRequest.of(0, 5);

            // When
            Page<User> page = userRepository.findAll(pageable);

            // Then
            assertThat(page.getContent()).hasSize(5);
            assertThat(page.getSize()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("审计字段测试")
    class AuditFieldTests {

        @Test
        @DisplayName("保存时应该自动设置创建时间和更新时间")
        void shouldAutoSetTimestampsOnCreate() {
            // When
            User savedUser = userRepository.save(testUser1);

            // Then
            assertThat(savedUser.getCreateTime()).isNotNull();
            assertThat(savedUser.getUpdateTime()).isNotNull();
            assertThat(savedUser.getCreateTime()).isBefore(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("更新时应该自动更新更新时间")
        void shouldAutoUpdateTimestampOnUpdate() throws InterruptedException {
            // Given
            User savedUser = userRepository.save(testUser1);
            LocalDateTime originalUpdateTime = savedUser.getUpdateTime();
            Thread.sleep(10); // 确保时间差

            // When
            savedUser.setNickname("Updated");
            User updatedUser = userRepository.save(savedUser);

            // Then
            assertThat(updatedUser.getUpdateTime())
                    .isAfter(originalUpdateTime);
        }

        @Test
        @DisplayName("应该自动设置默认状态和删除标记")
        void shouldAutoSetDefaultFlags() {
            // Given
            User user = User.builder()
                    .username("defaulttest")
                    .build();

            // When
            User savedUser = userRepository.save(user);

            // Then
            assertThat(savedUser.getStatus()).isEqualTo("0");
            assertThat(savedUser.getDelFlag()).isEqualTo("0");
        }
    }
}
