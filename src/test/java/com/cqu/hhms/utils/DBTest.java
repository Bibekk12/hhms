package com.cqu.hhms.utils;

import com.cqu.hhms.model.Role;
import com.cqu.hhms.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;

public class DBTest {

    public DBTest() {
    }

    @BeforeAll
    public static void setUpClass() throws SQLException {
        DB.init();
        DB.deleteAllTables();
        
        DB.setupTables();
        DB.prefillData();

    }

    @AfterAll
    public static void tearDownClass() throws SQLException {
        DB.deleteAllTables();
    }

    @Test
    public void testInsertUser() throws SQLException {
        User testUser = new User();
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setFullName("testFullName");
        testUser.setEmail("test@example.com");
        testUser.setPhone("1234567890");
        testUser.setOtherDetails("testDetails");

        Role role = new Role();
        role.setRoleID(1);
        testUser.setRole(role);

        User result = DB.insertUser(testUser);

        assertNotNull(result, "User insertion failed.");
        assertEquals(testUser.getUsername(), result.getUsername(), "Usernames don't match.");
        assertEquals(testUser.getFullName(), result.getFullName(), "Full Names don't match.");
    }

    @Test
    public void testSelectUser() throws SQLException {
        User testUser = new User();
        testUser.setUsername("selectUsername");
        testUser.setPassword("selectPassword");
        testUser.setFullName("selectFullName");
        testUser.setEmail("select@example.com");
        testUser.setPhone("0987654321");
        testUser.setOtherDetails("selectDetails");

        Role role = new Role();
        role.setRoleID(1);
        testUser.setRole(role);

        DB.insertUser(testUser);

        User selectedUser = DB.selectUser(testUser.getUsername(), "selectPassword");
        assertNotNull(selectedUser, "User selection failed.");
        assertEquals(testUser.getUsername(), selectedUser.getUsername(), "Usernames don't match.");
    }

}
